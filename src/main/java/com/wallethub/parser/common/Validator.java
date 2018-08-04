package com.wallethub.parser.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;

import java.util.Locale;

public class Validator implements JobParametersValidator {

    private static final Logger log = LoggerFactory.getLogger(Validator.class);
    private static final String DATE_PATTERN = "yyyy-MM-dd.HH:mm:ss";


    //Initializing JobParameterBuilder with someParameters
    public JobParametersBuilder initializeParameterBuilder(String param1, String param2, String param3, String param4) {

        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParametersBuilder.addString("accesslog",param1);
        jobParametersBuilder.addString("startDate", param2);
        jobParametersBuilder.addString("duration", param3);
        jobParametersBuilder.addLong("threshold", Long.valueOf(param4));
        jobParametersBuilder.addLong("randomParameter", Long.valueOf(System.currentTimeMillis()));
       // jobParametersBuilder.addString("endDate", "");

        return jobParametersBuilder;
    }

    //validate implementation of parameters
    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        String startDate = parameters.getString("startDate");
        String duration = parameters.getString("duration");
        Long threshold = parameters.getLong("threshold");


        if(Utility.isValidDateFormat(DATE_PATTERN, startDate, Locale.getDefault())){


        }else{
            throw new JobParametersInvalidException("2nd parameter['STARTDATE'] is not on the right format, please check format matches 'yyyy-MM-dd.HH:mm:ss'!");
        }

        //validate duration value is "hourly" and "daily"
        if(duration.equalsIgnoreCase("hourly")||duration.equalsIgnoreCase("daily")){

        }else{
            throw new JobParametersInvalidException("3rd parameter [DURATION] can only be either 'Hourly' or 'Daily'");
        }

        // validate signed threshold
        if(threshold >= 0){

        }else{
            log.info("3rd parameter [THRESHOLD] must equal or greater than '0'");
            throw new JobParametersInvalidException("4th parameter [THRESHOLD] must equal or greater than '0'");
        }
    }

    //Reaccessing the Jobparameter Builder to add the new parameter after modification
    public JobParametersBuilder addParam(JobParametersBuilder jobParametersBuilder, JobParameters jobParameters){
         String nextDate = Utility.nextTimeBasedOnDurationParam(DATE_PATTERN, jobParameters.getString("startDate"), Locale.getDefault(), jobParameters.getString("duration"));
            jobParametersBuilder.addString("endDate", nextDate);

            return jobParametersBuilder;
    }
}
