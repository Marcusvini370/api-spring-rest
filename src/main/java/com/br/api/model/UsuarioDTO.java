package com.br.api.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
 
@Getter
@Setter
public class UsuarioDTO  implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String userLogin;
	private String userNome;
	private String userCpf;
	private String userSenha;
	private Long userId;
	private Date userDataNascimento;
	private BigDecimal userSalario;
	private String userProfissao;
	
	private List<Telefone> telefones = new ArrayList<Telefone>();

	
	public UsuarioDTO(Usuario usuario) {
		this.userLogin = usuario.getLogin();
		this.userNome = usuario.getNome();
		this.userDataNascimento = usuario.getDataNascimento();
		this.userCpf = usuario.getCpf();
		this.userId = usuario.getId();
		this.userSenha = usuario.getSenha();
		this.telefones = usuario.getTelefones();
	    this.userSalario = usuario.getSalario();
	    this.userProfissao = usuario.getProfissao();
	   
	}

}
