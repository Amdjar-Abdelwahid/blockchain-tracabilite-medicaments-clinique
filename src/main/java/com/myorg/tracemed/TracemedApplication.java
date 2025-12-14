package com.myorg.tracemed;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TracemedApplication {

	public static void main(String[] args) {
		SpringApplication.run(TracemedApplication.class, args);
	}

}
