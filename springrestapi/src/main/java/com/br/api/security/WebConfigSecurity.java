package com.br.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.br.api.service.ImplementacaoUserDetailsService;

/* Mapeia URL, enderecos, autoriza ou bloquia acesso a URLs */

@Configuration
@EnableWebSecurity
public class WebConfigSecurity extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private ImplementacaoUserDetailsService implementacaoUserDetailsService;
	
	
	// Configura as solicitações de acesso por http
	@Override
		protected void configure(HttpSecurity http) throws Exception {
			
			//Ativando a proteção contra usuário que não estão validados por token
			http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
			
			// Ativando a restrição para a página inicial do sistema
			.disable().authorizeRequests().antMatchers("/").permitAll()
			.antMatchers("/index").permitAll()
			
			/* URL de Logout - Redireciona após o user deslogar do sistema */
			.anyRequest().authenticated().and().logout().logoutSuccessUrl("/index")
			
			//Mapeia URL de logout e invalida o usuário
			.logoutRequestMatcher(new AntPathRequestMatcher("/logout"));
			
			// Filtra Requisições de login para autenticação
			
			// Filtra as demais requisições para verificar a prsença do TOKEN JWT no header http
		}
	
	
	
	
	@Override
		protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		//Service que irá consultar o usuário no banco de dados
		auth.userDetailsService(implementacaoUserDetailsService)
		
		//Padrão de codificação de senha
		.passwordEncoder(new BCryptPasswordEncoder());
		}

}
