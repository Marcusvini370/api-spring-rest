package com.br.api.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.br.CPF;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Usuario implements UserDetails {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@EqualsAndHashCode.Include
	private Long id;
	
	@NotBlank
	@Column(unique = true)
	private String login;
	
	@NotBlank
	private String senha;
	
	@NotBlank
	private String nome;
	
	@NotBlank
	@CPF(message = "Cpf inválido")
	private String cpf;
	
	/*
	private String cep;
	private String logradouro;
	private String complemento;
	private String bairro;
	private String localidade;
	private String uf;
	*/
	

	@JsonFormat(timezone = "GMT-3", pattern = "dd/MM/yyyy")
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso = ISO.DATE, pattern = "dd/MM/yyyy")
	private Date dataNascimento;
	
	
	
	@OneToMany(mappedBy = "usuario", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Telefone> telefones = new ArrayList<Telefone>();
	
	
	private BigDecimal salario;
	private String profissao; 
	
	
	
	

	
	@OneToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "usuarios_role", uniqueConstraints = @UniqueConstraint (
			columnNames = {"usuario_id", "role_id"}, name = "unique_role_user"),
	joinColumns = @JoinColumn(name = "usuario_id", referencedColumnName = "id", table = "usuario", unique = false, updatable =false,
	foreignKey = @ForeignKey (name = "usuario_fk", value = ConstraintMode.CONSTRAINT)),
	
	inverseJoinColumns = @JoinColumn (name = "role_id", referencedColumnName = "id", table = "role", unique = false, updatable =false,
	foreignKey = @ForeignKey (name = "role_fk",value = ConstraintMode.CONSTRAINT )))
	private List<Role> roles = new ArrayList<Role>(); /* Papeis ou acessos */ 
	
	private String token = "";

	/* São os acessos do usuário com ROLE */
	@Override 
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return roles;
	}

	@JsonIgnore
	@Override
	public String getPassword() {
		return this.senha;
	}

	@JsonIgnore
	@Override
	public String getUsername() {
		return this.login;
	}

	@JsonIgnore
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@JsonIgnore
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@JsonIgnore
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@JsonIgnore
	@Override
	public boolean isEnabled() {
		return true;
	}



}
