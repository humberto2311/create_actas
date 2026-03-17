package com.app.juridico.create_actas;

import com.app.juridico.create_actas.domain.services.GeminiService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CreateActasApplication {

	public static void main(String[] args) {
		SpringApplication.run(CreateActasApplication.class, args);
	}

	// Esta es la forma correcta de probar algo al iniciar en Spring Boot
	@Bean
	CommandLineRunner testGemini(GeminiService gemini) {
		return args -> {
			System.out.println("--- PROBANDO GEMINI ---");
			String respuesta = gemini.askQuestion("Dame una definición corta de qué es un acta jurídica.");
			System.out.println("Respuesta de Gemini: " + respuesta);
		};
	}
}