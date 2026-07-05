package com.nahla.marketplace;

import com.nahla.marketplace.controller.CategoryController;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MarketplaceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MarketplaceApplication.class, args);
	}

	@Bean
	CommandLineRunner seedOnStartup(CategoryController categoryController) {
		return args -> categoryController.seedCategories();
	}
}