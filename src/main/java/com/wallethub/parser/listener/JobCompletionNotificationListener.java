package com.wallethub.parser.listener;

import com.wallethub.parser.model.AccessLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

    private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    private final JdbcTemplate jdbcTemplate;
    private String startDate;
    private String endDate;
    private Long threshold;
    private String duration;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    @Autowired
    public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Long getThreshold() {
        return threshold;
    }

    public void setThreshold(Long threshold) {
        this.threshold = threshold;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("!!! AFTER JOB EXECUTION COMPLETED");
            //setting jobparameters
            JobParameters jobParameters = jobExecution.getJobParameters();
            setStartDate(jobParameters.getString("startDate"));
            setEndDate(jobParameters.getString("endDate"));
            setThreshold(jobParameters.getLong("threshold"));
            setDuration(jobParameters.getString("duration"));


            //query to filter the ips based on the criteria
            List<AccessLog> results = jdbcTemplate.query("SELECT DISTINCT ip_address, COUNT( ip_address ) as request_made FROM `access_log` WHERE (date_event between '" + startDate + "' AND '" + endDate + "') GROUP BY ip_address", new RowMapper<AccessLog>() {
                @Override
                public AccessLog mapRow(ResultSet rs, int row) throws SQLException {
                   //if count request made is bigger or equal than input threshold
                    if (rs.getInt(2) >= getThreshold()) {
                        //creating object just in match scenario
                        AccessLog blockedAccess = new AccessLog();
                        blockedAccess.setIp_address(rs.getString(1));
                        blockedAccess.setCommentary("This Ip was blocked because it made more than " + getThreshold() + " request and exceeded the " + getDuration() + " limit");
                        return blockedAccess;
                    }
                    //nulling for further cleaning
                    return null;
                }
            });

            //removing null objects
            results.removeIf(Objects::isNull);

            //loading into blocked ip table
            insertBatch(results);
        }
    }

    //batch load to blocked ip table
    public void insertBatch(final List<AccessLog> access) {

        String sql = "INSERT INTO BLOCKED_IPS " +
                "(ip_address, commentary) VALUES (?, ?)";

        getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                AccessLog accesLog = access.get(i);
                ps.setString(1, accesLog.getIp_address());
                ps.setString(2, accesLog.getCommentary());
            }

            @Override
            public int getBatchSize() {
                return access.size();
            }
        });
    }

  /*  public static Instant getDateFromString(String dateIn){
       // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    } */



/*
    public static Date getDateFromString(String dateIn){
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss.SSS");
        Date date = null;
        try {

            date = fmt.parse(dateIn);
            System.out.println(date);
            System.out.println(fmt.format(date));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    */

}
