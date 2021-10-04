package com.br.api.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.br.api.ObjetoErro;
import com.br.api.model.Usuario;
import com.br.api.repository.UsuarioRepository;
import com.br.api.service.ServiceEnviarEmail;



@RestController
@RequestMapping(value = "/recuperar")
public class RecuperarController {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private ServiceEnviarEmail serviceEnviaEmail;
	
	@ResponseBody
	@PostMapping(value = "/")
	public ResponseEntity<ObjetoErro>recuperar(@RequestBody Usuario login) throws Exception{
		
ObjetoErro objetoErro = new ObjetoErro();
		
		//vai buscar o email q veio da tela
		Usuario user = usuarioRepository.findUserByLogin(login.getLogin()); 
		
		if(user == null) {
			
			objetoErro.setCode("400");/*Não encontrado*/
			objetoErro.setError("Usuário não encontrado.");
		}else {
			/*Gerar uma senha em formato de data e vai atualizar no banco de dados*/
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String senhaNova = dateFormat.format(Calendar.getInstance().getTime());
			
			String senhaCriptografada  = new BCryptPasswordEncoder().encode(senhaNova);
			
			/*Senha que vai set atualizada no banco criptografada*/
			usuarioRepository.updateSenha(senhaCriptografada, user.getId());
			System.out.println("inciou");
			serviceEnviaEmail
			.enviarEmail("Recuperação de senha", user.getLogin(),
					"Sua nova senha é: " + senhaNova); /*passa a senha pro usuario legivel/*
			
			/*Rotina de envio de E-mail*/
			objetoErro.setCode("200");/*Não encontrado*/
			objetoErro.setError("Acesso enviado para seu e-mail.");
		}
		
		return new  ResponseEntity<ObjetoErro>(objetoErro, HttpStatus.OK);
	}

}
