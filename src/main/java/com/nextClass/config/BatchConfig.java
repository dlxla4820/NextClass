package com.nextClass.config;


import org.hibernate.boot.model.internal.XMLContext;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

//@Configuration
//public class BatchConfig extends DefaultBatchConfiguration{
//    @Bean
//    public Job job(JobRepository jobRepository){
//        return new JobBuilder("job", jobRepository)
//                .preventRestart()
//                .start()
//                .build();
//    }
//}