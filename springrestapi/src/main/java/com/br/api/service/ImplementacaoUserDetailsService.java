package com.br.api.service;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;
import com.br.api.model.Usuario;
import com.br.api.repository.UsuarioRepository;

@Service
public class ImplementacaoUserDetailsService implements UserDetailsService {

	@Autowired // injeção de dependencias
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		/* Consulta no banco o usuário */
		
		Usuario usuario = usuarioRepository.findUserByLogin(username);
		
		if(usuario == null) {
			throw new UsernameNotFoundException("Usuário não foi encontrado");
		}
		
		return new User(usuario.getLogin(), usuario.getPassword(), usuario.getAuthorities()); 
	}

	public void insereAcessoPadrao(Long id) {
		
		//descobri qual é a constraint de restrição
				String constraint  = usuarioRepository.consultarConstraintRole(); //busca o nome da constrait
				
				if(constraint != null) { //se ela realmente desistir vai dar o comando de remover ela no banco
				
				//remove a constraint com nome que foi pego na consulta
				jdbcTemplate.execute(" alter table usuarios_role drop constraint " + constraint);	
				
				//insere os acesso padrão
				usuarioRepository.insereAcessoRolePadrao(id);
				}
		
	}

}
