package com.example;

import javax.sql.DataSource;

import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import com.example.mapper.ds1.Todo1Mapper;
import com.example.mapper.ds2.Todo2Mapper;

@SpringBootApplication
public class MybatisDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MybatisDemoApplication.class, args);
	}

	@Bean
	CommandLineRunner ds1(Todo1Mapper todoMapper) {
		return args -> {

			System.out.println("----ds1-----");

			Todo newTodo = new Todo();
			newTodo.setTodoTitle("Dinner");
			newTodo.setDetails("Ginza 19:00");

			todoMapper.insert(newTodo);

			Todo loadedTodo = todoMapper.select(newTodo.getTodoId());
			System.out.println("ID       : " + loadedTodo.getTodoId());
			System.out.println("TITLE    : " + loadedTodo.getTodoTitle());
			System.out.println("DETAILS  : " + loadedTodo.getDetails());
			System.out.println("FINISHED : " + loadedTodo.isFinished());

		};
	}

	@Bean
	CommandLineRunner ds2(Todo2Mapper todoMapper) {
		return args -> {
			System.out.println("----ds2-----");

			Todo newTodo = new Todo();
			newTodo.setTodoTitle("Dinner");
			newTodo.setDetails("Ginza 19:00");

			todoMapper.insert(newTodo);

			Todo loadedTodo = todoMapper.select(newTodo.getTodoId());
			System.out.println("ID       : " + loadedTodo.getTodoId());
			System.out.println("TITLE    : " + loadedTodo.getTodoTitle());
			System.out.println("DETAILS  : " + loadedTodo.getDetails());
			System.out.println("FINISHED : " + loadedTodo.isFinished());
		};
	}

	@MapperScan(basePackages = "com.example.mapper.ds1", sqlSessionFactoryRef = "sqlSessionFactory")
	static class Ds1Configuration {
		private final MyBatisConfigurationSupport support;

		public Ds1Configuration(MyBatisConfigurationSupport support) {
			this.support = support;
		}

		@Bean
		public DataSource dataSource() {
			return support.createDataSource("ds1");
		}

		@Bean
		public SqlSessionFactoryBean sqlSessionFactory(@Qualifier("dataSource") DataSource dataSource) {
			return support.createSqlSessionFactoryBean(dataSource);
		}

	}

	@MapperScan(basePackages = "com.example.mapper.ds2", sqlSessionFactoryRef = "sqlSessionFactory2")
	static class Ds2Configuration {
		private final MyBatisConfigurationSupport support;

		public Ds2Configuration(MyBatisConfigurationSupport support) {
			this.support = support;
		}

		@Bean
		public DataSource dataSource2() {
			return support.createDataSource("ds2");
		}

		@Bean
		public SqlSessionFactoryBean sqlSessionFactory2(@Qualifier("dataSource2") DataSource dataSource) {
			return support.createSqlSessionFactoryBean(dataSource);
		}

	}

	@Component
	static class MyBatisConfigurationSupport {
		private final DataSourceProperties dataSourceProperties;
		private final MybatisProperties myBatisProperties;
		private final ResourceLoader resourceLoader;

		public MyBatisConfigurationSupport(DataSourceProperties dsProperties, MybatisProperties myBatisProperties,
				ResourceLoader resourceLoader) {
			this.dataSourceProperties = dsProperties;
			this.myBatisProperties = myBatisProperties;
			this.resourceLoader = resourceLoader;
		}

		public DataSource createDataSource(String instanceName) {
			DataSource dataSource = DataSourceBuilder.create()
					.driverClassName(dataSourceProperties.determineDriverClassName())
					.url(dataSourceProperties.determineUrl().replaceFirst("testdb", instanceName))
					.username(dataSourceProperties.determineUsername()).password(dataSourceProperties.determinePassword())
					.build();
			ResourceDatabasePopulator populator = new ResourceDatabasePopulator(
					resourceLoader.getResource("classpath:schema.sql"));
			DatabasePopulatorUtils.execute(populator, dataSource);
			return dataSource;
		}

		public SqlSessionFactoryBean createSqlSessionFactoryBean(DataSource dataSource) {
			SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
			factoryBean.setVfs(SpringBootVFS.class);
			factoryBean.setDataSource(dataSource);
			Configuration configuration = new Configuration();
			if (myBatisProperties.getConfiguration() != null) {
				BeanUtils.copyProperties(myBatisProperties.getConfiguration(), configuration);
			}
			// omit ...
			factoryBean.setConfiguration(configuration);
			return factoryBean;
		}

	}

}
