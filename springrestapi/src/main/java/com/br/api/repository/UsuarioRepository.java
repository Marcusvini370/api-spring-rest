package com.br.api.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.br.api.model.Usuario;

@Repository
@Transactional
public interface UsuarioRepository extends CrudRepository<Usuario, Long> {
	
	@Query("select u from Usuario u where u.login = ?1")
	Usuario findUserByLogin(String login);
	
	
	@Modifying
	@org.springframework.data.jpa.repository.Query(nativeQuery = true, value ="update usuario set token = ?1 where login = ?2")
	void atualizaTokenUser(String token, String login);

}
