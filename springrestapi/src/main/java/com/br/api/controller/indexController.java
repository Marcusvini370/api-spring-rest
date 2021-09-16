package com.br.api.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.br.api.model.Usuario;
import com.br.api.model.UsuarioDTO;
import com.br.api.repository.UsuarioRepository;
import com.google.gson.Gson;




@RestController // Arquitetura REST
@RequestMapping(value = "/usuario")
public class indexController {

	@Autowired // se fosse CDI seria @Inject
	private UsuarioRepository usuarioRepository;
	
	
	
	/* Serviço RESTfull */
	@GetMapping(value = "/{id}") // Lista usuário por id
	@CacheEvict(value = "cacheuser" , allEntries = true)  // se tiver cache que não é usado vai remover
	@CachePut(value = "cacheputuser") // se tem mudanças ou dados novos no banco, vai trazer para o cache
	public ResponseEntity<UsuarioDTO> initV1(@PathVariable(value = "id") Long id) {

		Optional<Usuario> usuario = usuarioRepository.findById(id);
		return new ResponseEntity<UsuarioDTO>(new UsuarioDTO(usuario.get()), HttpStatus.OK);
	}
	

	/* Vamos supor que o carregamento de usuário seja um processo lento e 
	  queremos controlar ele com cache para agilizar o processo  */
	@GetMapping(value = "/")
	@CacheEvict(value = "cacheusuarios" , allEntries = true)  // se tiver cache que não é usado vai remover
	@CachePut(value = "cacheputusuarios") // se tem mudanças ou dados novos no banco, vai trazer para o cache
	public ResponseEntity<List<Usuario>> usuario() throws InterruptedException {

		List<Usuario> list = (List<Usuario>) usuarioRepository.findAll();
			
		return new ResponseEntity<List<Usuario>>(list, HttpStatus.OK);
	}
	
	/* END-POINT consulta de usuário por nome */
	@GetMapping(value = "/usuarioPorNome/{nome}")
	public ResponseEntity<List<Usuario>> usuarioPorNome(@PathVariable("nome") String nome) {

		List<Usuario> list =  (List<Usuario>) usuarioRepository.findUserByNome(nome);
			
		return new ResponseEntity<List<Usuario>>(list, HttpStatus.OK);
	}


	@PostMapping(value = "/")
	public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario) throws Exception {
		
		for(int pos = 0; pos < usuario.getTelefones().size(); pos ++ ) {
			usuario.getTelefones().get(pos).setUsuario(usuario); // vai amarrar os telefones aos usuários pertencentes
		}
		
		
		/*
		// Consumindo API publica externa do cep
		
		URL url  = new URL("https://viacep.com.br/ws/"+usuario.getCep()+"/json/");
		URLConnection connection = url.openConnection();
		InputStream is = connection.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		
		String cep = "";
		StringBuilder jsonCep = new StringBuilder();
		
		while((cep = br.readLine()) != null) {
			jsonCep.append(cep);
		}
			
			
			Usuario userAux = new Gson().fromJson(jsonCep.toString(), Usuario.class);
			
			usuario.setCep(userAux.getCep());
			usuario.setLogradouro(userAux.getLogradouro());
			usuario.setComplemento(userAux.getComplemento());
			usuario.setBairro(userAux.getBairro());
			usuario.setLocalidade(userAux.getLocalidade());
			usuario.setUf(userAux.getUf());
			usuario.setDdd(userAux.getDdd());*/
			
		
		// Consumindo API publica externa
		
		String senhacriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
		usuario.setSenha(senhacriptografada);
		Usuario usuarioSalvo = usuarioRepository.save(usuario);

		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);
	}

	@PutMapping(value = "/")
	public ResponseEntity<Usuario> atualizar(@RequestBody Usuario usuario) {

		/* Outras rotinas antes de atualizar */
		
		
		for(int pos = 0; pos < usuario.getTelefones().size(); pos++) {
	     	   usuario.getTelefones().get(pos).setUsuario(usuario); 
	        }
		
		Usuario userTemporario = usuarioRepository.findById(usuario.getId()).get();
		
		//senhas diferente
		if(!userTemporario.getPassword().equals(usuario.getPassword())) {
			//se for diferente vai criptografar a senha nova do usuario
			String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
			usuario.setSenha(senhaCriptografada);
		}

		Usuario UsuarioAtualizar= usuarioRepository.save(usuario);

		return 	ResponseEntity.ok(UsuarioAtualizar);
		//ou return new ResponseEntity<Usuario>(Usuariosalvo, HttpStatus.OK);
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") Long id) {

		usuarioRepository.deleteById(id);

		return ResponseEntity.noContent().build();

	}
	
	
}
