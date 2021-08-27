package com.br.api.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.br.api.model.Usuario;
import com.br.api.repository.UsuarioRepository;

@RestController // Arquitetura REST
@RequestMapping(value = "/usuario")
public class indexController {

	@Autowired // se fosse CDI seria @Inject
	private UsuarioRepository usuarioRepository;
	
	/*Serviço RESTfull */
	@GetMapping(value = "/{id}/relatoriopdf  e",  produces = "application/pdf") // Lista usuário por id
	public ResponseEntity<Usuario> relatorio(@PathVariable (value ="id") Long id) {

		Optional<Usuario> usuario = usuarioRepository.findById(id);
		
			/* O retorno seria  uma relatório */
			return  new ResponseEntity<Usuario>(usuario.get(), HttpStatus.OK);
	}
	
	/*Serviço RESTfull */
	@GetMapping(value = "/{id}",  produces = "application/json") // Lista usuário por id
	public ResponseEntity<Usuario> init(@PathVariable (value ="id") Long id) {

		Optional<Usuario> usuario = usuarioRepository.findById(id);
		
			return  new ResponseEntity<Usuario>(usuario.get(), HttpStatus.OK);
	}
	
	@GetMapping(value = "/", produces = "application/json")
	public ResponseEntity<List<Usuario>> usuario(){
		
		List<Usuario> list = (List<Usuario>) usuarioRepository.findAll();
		return new ResponseEntity<List<Usuario>>(list, HttpStatus.OK);
	}
	
	@PostMapping(value = "/", produces ="application/json")
	public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario){
		
		Usuario usuarioSalvo = usuarioRepository.save(usuario);
		
		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);
	}
	
	@PostMapping(value = "/{iduser}/idvenda/{idvenda}", produces ="application/json")
	public ResponseEntity<Usuario> cadastrarvenda(@PathVariable Long iduser,
			@PathVariable Long idvenda){
		
		//Aqui seria o processo de venda
		//Usuario usuarioSalvo = usuarioRepository.save(usuario);
		
		return new ResponseEntity("Id user : " + iduser + " idvenda : " + idvenda, HttpStatus.OK);
	}
	
	
	@PutMapping(value = "/")
	public ResponseEntity<Usuario> atualizar(@RequestBody Usuario usuario){
		
		/* Outras rotinas antes de atualizar */
		
		Usuario usuarioSalvo = usuarioRepository.save(usuario);
		
		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);
	}
	
	@DeleteMapping(value = "/{id}", produces = "application/text")
	public ResponseEntity<Void> delete(@PathVariable ("id") Long id) {
		
		usuarioRepository.deleteById(id);
		
		return ResponseEntity.noContent().build();
		
	}
	
	
}
