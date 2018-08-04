package com.wallethub.parser.config;

import com.wallethub.parser.listener.JobCompletionNotificationListener;
import com.wallethub.parser.model.AccessLog;
import com.wallethub.parser.processor.AccessLogItemProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class BatchProcessConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private DataSource dataSource;

    //to be suplanted by jobParameter on runtime
    private static final String OVERRIDDEN_BY_EXPRESSION = null;

    @Bean
    @StepScope
    //StepScope anotation in other to read the jobParameter value
    public FlatFileItemReader<AccessLog> reader(@Value("#{jobParameters[accesslog]}") String path){
        FlatFileItemReader<AccessLog> reader = new FlatFileItemReader<AccessLog>();

        reader.setResource(new ClassPathResource(path));
        reader.setLineMapper(new DefaultLineMapper<AccessLog>() {{
            setLineTokenizer(new DelimitedLineTokenizer("|"){{
                setNames("date_event", "ip_address","request_method","status_code","user_agent");
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<AccessLog>() {{
                setTargetType(AccessLog.class);
            }});
        }});

        return reader;
    }

    @Bean
    public JdbcBatchItemWriter<AccessLog> writer() {
        JdbcBatchItemWriter<AccessLog> writer = new JdbcBatchItemWriter<AccessLog>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<AccessLog>());
        writer.setSql("INSERT INTO access_log (date_event, ip_address,request_method,status_code, user_agent) VALUES (:date_event, :ip_address,:request_method,:status_code,:user_agent)");
        writer.setDataSource(dataSource);
        return writer;
    }

    @Bean
    public Job customUserJob(JobCompletionNotificationListener listener) {
        return jobBuilderFactory.get("customUserJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1())
                .end()
                .build();
    }

    @Bean
    public AccessLogItemProcessor processor(){
        return new AccessLogItemProcessor();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                //bigger chunkSize to test faster, would go down on production
                .<AccessLog, AccessLog> chunk(4000)
                .reader(reader(OVERRIDDEN_BY_EXPRESSION))
                .processor(processor())
                .writer(writer())
                .build();
    }


}


