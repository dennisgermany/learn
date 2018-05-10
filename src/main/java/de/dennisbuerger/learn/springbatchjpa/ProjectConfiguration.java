package de.dennisbuerger.learn.springbatchjpa;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackages = { "de.dennisbuerger.test.springbatch" })
@EnableJpaRepositories("de.dennisbuerger.test.springbatch")
@EnableAutoConfiguration
@EnableTransactionManagement
@EnableBatchProcessing
public class ProjectConfiguration {

	private static final Logger log = LoggerFactory.getLogger(ProjectConfiguration.class);

	@Autowired
	StepBuilderFactory stepBuilderFactory;

	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.h2.Driver");
		dataSource.setUrl("jdbc:h2:file:~/test");
		dataSource.setUsername("sa");
		dataSource.setPassword("");
		return dataSource;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(dataSource());
		em.setPackagesToScan(new String[] { "de.dennisbuerger.test.springbatch" });

		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		em.setJpaVendorAdapter(vendorAdapter);

		return em;
	}

	@Bean
	public JpaTransactionManager jpaTransactionManager(DataSource dataSource) {
		final JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setDataSource(dataSource);
		return transactionManager;
	}

	@Bean
	public Step step1(UserRepository userRepository) {
		return stepBuilderFactory.get("step1").tasklet(new Step1(userRepository)).build();
	}

	private static final class Step1 implements Tasklet, StepExecutionListener {
		private final UserRepository userRepository;

		private Step1(UserRepository userRepository) {
			this.userRepository = userRepository;
		}

		public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
			User entity = new User("Mike", "Meier");
			userRepository.save(entity);
			if (1 == 1)
				throw new RuntimeException("wtf");
			return RepeatStatus.FINISHED;
		}

		@Override
		public void beforeStep(StepExecution stepExecution) {

		}

		@Override
		public ExitStatus afterStep(StepExecution stepExecution) {
			if (!stepExecution.getFailureExceptions().isEmpty()) {
				return new ExitStatus("WHO_KNOWS");
			} else {
				return new ExitStatus("OK");
			}
		}
	}

	@Bean
	public Step step2(UserRepository userRepository) {
		return stepBuilderFactory.get("step2").tasklet(new Tasklet() {
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
				User entity = new User("Jutta", "Schmitt");
				userRepository.save(entity);
				return RepeatStatus.FINISHED;
			}
		}).build();
	}

	@Bean
	public Job job(JobBuilderFactory jobBuilderFactory, UserRepository userRepository) throws Exception {
		return jobBuilderFactory.get("job2").incrementer(new RunIdIncrementer()).start(step1(userRepository))
				.on("WHO_KNOWS").to(step2(userRepository)).end().build();
	}
}