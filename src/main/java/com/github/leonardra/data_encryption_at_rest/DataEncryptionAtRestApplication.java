package com.github.leonardra.data_encryption_at_rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = "com.github.leonardra.data_encryption_at_rest.*")
@ConfigurationPropertiesScan
@EnableTransactionManagement
@EnableAsync
@EnableJpaRepositories(basePackages = "com.github.leonardra.data_encryption_at_rest.*")
public class DataEncryptionAtRestApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataEncryptionAtRestApplication.class, args);
	}

}
