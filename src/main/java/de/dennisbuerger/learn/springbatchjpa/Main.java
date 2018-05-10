package de.dennisbuerger.learn.springbatchjpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
	public static void main(String[] args) {
		// BATCH mode -> Exit after execution
		System.exit(SpringApplication.exit(SpringApplication.run(ProjectConfiguration.class, args)));
		
		// REST mode -> keep running after execution, for rest services and h2 web console
		//SpringApplication.run(BatchConfiguration.class, args);
	}
	
	
}