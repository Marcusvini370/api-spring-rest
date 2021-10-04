package com.br.api.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.br.api.model.Telefone;

@Repository
public interface TelefoneRepository extends CrudRepository<Telefone, Long> {

}
