package com.retailchain.franchise;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Hooks;

@SpringBootApplication
public class FranchiseManagementApiApplication {

	public static void main(String[] args) {
		// Habilitar modo debug para Reactor (útil en desarrollo)
		Hooks.onOperatorDebug();
		
		SpringApplication.run(FranchiseManagementApiApplication.class, args);
	}

}
