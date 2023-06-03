package com.spring.batch.jpa.dynamic.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.spring.batch.jpa.dynamic.entity.Customer;
import com.spring.batch.jpa.dynamic.repository.Customer_Repository;

@Configuration
@EnableBatchProcessing
public class BatchConfig {
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private JobBuilderFactory  jobBuilderFactory;
	
	@Autowired
	private Customer_Repository customerRepository;
	
	@Bean
	public FlatFileItemReader<Customer> reader(){
		
		FlatFileItemReader<Customer> flatFileItemReader = new FlatFileItemReader<Customer>();
		
		flatFileItemReader.setName("CSVReader");
		flatFileItemReader.setResource(new ClassPathResource("customers.csv"));
		flatFileItemReader.setLinesToSkip(1);
		flatFileItemReader.setLineMapper(lineMapper());
		
		return flatFileItemReader;
		
	}

	private LineMapper<Customer> lineMapper() {
		
		DefaultLineMapper<Customer> defaultLineMapper = new DefaultLineMapper<Customer>();
		
		DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
		delimitedLineTokenizer.setDelimiter(DelimitedLineTokenizer.DELIMITER_COMMA);
		delimitedLineTokenizer.setStrict(true);

		delimitedLineTokenizer.setNames("id","firstName","lastName","email","gender","contactNo","country","dob");
		
		BeanWrapperFieldSetMapper<Customer> beanWrapperFieldSetMapper = new BeanWrapperFieldSetMapper<>();
		beanWrapperFieldSetMapper.setTargetType(Customer.class);
		
		defaultLineMapper.setLineTokenizer(delimitedLineTokenizer);
		defaultLineMapper.setFieldSetMapper(beanWrapperFieldSetMapper);
		
		
		return defaultLineMapper;
	}
	
	@Bean
	public RepositoryItemWriter<Customer> objectWriter(){
		
		RepositoryItemWriter<Customer> repositoryItemWriter = new RepositoryItemWriter<Customer>();
		
		repositoryItemWriter.setRepository(customerRepository);
		repositoryItemWriter.setMethodName("save");
		
		return repositoryItemWriter; 
	}
	
	
	@Bean
	public Step step1() {
		
		return stepBuilderFactory.get("step1")
		                         .<Customer,Customer>chunk(10)
		                         .reader(reader())
		                         .processor(processor())
		                         .writer(objectWriter())
		                         .build();
	}
	
	@Bean
	public ItemProcessor<Customer, Customer> processor(){
		return customer->customer;
	}
	
	@Bean
	public Job runJob() {
		
		return jobBuilderFactory.get("runJob")
								.flow(step1())
								.end()
								.build();
	}


}
