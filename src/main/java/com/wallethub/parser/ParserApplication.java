package com.wallethub.parser;

import com.wallethub.parser.common.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ParserApplication{

    private static final Logger log = LoggerFactory.getLogger(ParserApplication.class);

    public static void main(String[] args) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {


             Validator validator = new Validator();
             JobParametersBuilder jobParametersBuilder = null;
             jobParametersBuilder = validator.initializeParameterBuilder(args[0], args[1], args[2], args[3]);


            try {

                validator.validate(jobParametersBuilder.toJobParameters());
                validator.addParam(jobParametersBuilder, jobParametersBuilder.toJobParameters());
                SpringApplication app = new SpringApplication(ParserApplication.class);
                ConfigurableApplicationContext context = app.run(args);
                JobLauncher jobLauncher = context.getBean(JobLauncher.class);
                Job job = context.getBean("customUserJob", Job.class);
                jobLauncher.run(job, jobParametersBuilder.toJobParameters());

            } catch (JobParametersInvalidException e) {
                e.printStackTrace();
            }




    }

}
