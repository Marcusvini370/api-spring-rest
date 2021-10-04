package com.br.api.repository;

import java.util.List;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.br.api.model.Usuario;

@Repository
@Transactional
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
	
	@Query("select u from Usuario u where u.login = ?1")
	Usuario findUserByLogin(String login);
	
	
	@Modifying
	@Query(nativeQuery = true, value ="update usuario set token = ?1 where login = ?2")
	void atualizaTokenUser(String token, String login);
	
	@Query("select u from Usuario u where u.nome like %?1%")
	List<Usuario> findUserByNome(String nome);
	
	@Query(value = "Select constraint_name from information_schema.constraint_column_usage"
			+ " where table_name = 'usuarios_role' and column_name = 'role_id' and constraint_name"
			+ "<> 'unique_role_user';", nativeQuery = true ) //nativequery = sql puro
     String consultarConstraintRole();
	
	@Transactional
	@Modifying
	@Query(value= " update usuario set senha = ?1 where id = ?2", nativeQuery = true)
	void updateSenha(String senha, Long codUser);
	
	
	@Modifying
	@Query(nativeQuery = true, value =" insert into usuarios_role (usuario_id, role_id) values(?1, (select id from role where nome_role = 'ROLE_USER'));")
	void insereAcessoRolePadrao(Long idUser);
	
		default Page<Usuario> findUserByNamePage(String nome, PageRequest pageRequest) {
		
		Usuario usuario = new Usuario();
		usuario.setNome(nome); //usuário que veio da tela pra consulta
		
		/*Configurando para pesquisar por nome e paginação, consultar onde existir parte desse nome contains() ignorando as case */
		ExampleMatcher exampleMatcher = ExampleMatcher.matchingAny()
				.withMatcher("nome", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
		
		/*prepara essa consulta juntando usuário com as configurações de consulta*/
		Example<Usuario> example = Example.of(usuario, exampleMatcher);
		
		/*Toda configuração de paginação e também de consulta*/
		Page<Usuario> retorno = findAll(example, pageRequest);
		
		return retorno; //retorno que veio da consulta						
	}
}
