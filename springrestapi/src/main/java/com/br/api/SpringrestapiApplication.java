package com.br.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EntityScan(basePackages = {"com.br.api.model"}) //varre as entidades do model e as ajuda a ser criada no  bd
@ComponentScan(basePackages = {"com.*"}) //injeções. ler tudo dentro da pasta com
@EnableJpaRepositories(basePackages = {"com.br.api.repository"}) //habilita a parte de repositórios do jpa
@EnableTransactionManagement //Controlar as transações ,resolve bastante problemas
@EnableWebMvc //habilita os recursos de mvc
@RestController // indica que o projeto é rest e os controller irá retorna json
@EnableAutoConfiguration // O srping vai configurar todo o projeto
public class SpringrestapiApplication implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(SpringrestapiApplication.class, args);
	}
	
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		
		registry.addMapping("/usuario/**").
		allowedMethods("*")
		.allowedOrigins("*");
		/* Liberando o mapeamento de usuário para todas origens */
	}

}
