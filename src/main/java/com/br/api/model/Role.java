package com.br.api.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "role")
@SequenceGenerator(name = "seq_role", sequenceName = "seq_role", allocationSize = 1, initialValue = 1)
public class Role implements GrantedAuthority{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_role")
	private Long id;
	
	private String nomeRole; // PAPEL no sistema, exemplo: ROLE_ADMINASTRADOR, ROLE_GERENTE

	@Override
	public String getAuthority() { //Retorna o nome do acesso ou autorização ex: ROLE_GERENTE
		return this.nomeRole;
	}
	
	
}
