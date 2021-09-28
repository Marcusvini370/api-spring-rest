package com.br.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.br.api.model.Profissao;

@Repository
public interface ProfissaoRepository extends JpaRepository<Profissao, Long> {

}
