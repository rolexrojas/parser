package com.wallethub.parser;

import com.wallethub.parser.config.BatchProcessConfig;
import com.wallethub.parser.model.AccessLog;
import com.wallethub.parser.processor.AccessLogItemProcessor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {
        ParserApplication.class,
        BatchProcessConfig.class,
        AccessLog.class,
        AccessLogItemProcessor.class
})
public class ParserApplicationTests {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }


    //@Ignore
    @Test
    public void startBatchJob() throws Exception {
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParametersBuilder.addString("accesslog","access.log");
        jobParametersBuilder.addString("startDate", "2017-01-01.15:00:00");
        jobParametersBuilder.addString("duration", "hourly");
        jobParametersBuilder.addLong("threshold", Long.valueOf("200"));
        jobParametersBuilder.addString("endDate", "2017-01-01.16:00:00");
        jobParametersBuilder.addLong("randomParameter", Long.valueOf(System.currentTimeMillis()));

        //jobLauncher.run(job, new JobParameters());
        JobExecution jobExecution = jobLauncher.run(job, jobParametersBuilder.toJobParameters());

        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
    }

    @AfterJob
    @Test
    public void afterJobCompletes(){
        //validate ip blocking based on parameters
        final SqlRowSet blockedIpRowSet = getJdbcTemplate().queryForRowSet("SELECT ip_address FROM `blocked_ips`");
        int rowCount = 0;

        //validate not null rowset object
        assertNotNull(blockedIpRowSet);
        String ip_address = "";
        while(blockedIpRowSet.next()){
            if(rowCount == 0) {
                 ip_address = blockedIpRowSet.getString("ip_address");
            }
            rowCount++;
        }

        //validate 2 records where found
        assertEquals(2, rowCount);



        assertEquals("192.168.106.134", ip_address);
    }


   /* @Test
    public void jobTestWithParam() throws Exception {

        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParametersBuilder.addString("accesslog","access.log");
        jobParametersBuilder.addString("startDate", "2017-01-01.15:00:00");
        jobParametersBuilder.addString("duration", "hourly");
        jobParametersBuilder.addLong("threshold", Long.valueOf("200"));

        JobExecution jobExecution = jobLauncher.run(job, jobParametersBuilder.toJobParameters());
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
    }

   /* @Test
    public void jobTestWithParam2() throws Exception {

        JobParametersBuilder jobParametersBuild = new JobParametersBuilder();
        jobParametersBuild.addString("accesslog","access.log");
        jobParametersBuild.addString("startDate", "2017-01-01.00:00:00");
        jobParametersBuild.addString("duration", "daily");
        jobParametersBuild.addLong("threshold", Long.valueOf("500"));

        jobLauncher.run(job, jobParametersBuild.toJobParameters());

        //assertEquals(BatchStatus.COMPLETED, JobExecution.class);
    }*/

    //@Test
    //public void jobTester() {

        //JobExecution jobExecution = jobLauncherTestUtils.jobTester();
        //assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
    //}
}
