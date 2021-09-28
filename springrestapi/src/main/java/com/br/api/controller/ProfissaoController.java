package com.br.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.br.api.model.Profissao;
import com.br.api.repository.ProfissaoRepository;



@RestController
@RequestMapping(value="/profissao")
public class ProfissaoController {
	
	@Autowired
	private ProfissaoRepository profissaoRepository;
	
	@GetMapping(value = "/")
	public ResponseEntity<List<Profissao>> profissoes (){
		
		List<Profissao> lista = profissaoRepository.findAll();
		
		return new ResponseEntity<List<Profissao>>(lista, HttpStatus.OK);
		
	}

}
