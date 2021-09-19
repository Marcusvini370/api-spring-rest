package com.br.api;

import java.time.OffsetDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(Include.NON_NULL)//n vai incluir pra campos nulo
@Getter
@Setter
public class ObjetoErro {

	private String titulo;
	private String error;
	private String code;
	
	
	private OffsetDateTime dataHora;
	private List<Campo> campos;
	
	@AllArgsConstructor
	@Getter
	public class Campo {
		private String nome;
		private String mensagem;
	}
	
	
}
