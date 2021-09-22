package com.br.api.model;

import java.io.Serializable;
import java.util.ArrayList;
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
	private String senha;
	private Long id;
	
	private List<Telefone> telefones = new ArrayList<Telefone>();
	
	public UsuarioDTO(Usuario usuario) {
		this.userLogin = usuario.getLogin();
		this.userNome = usuario.getNome();
		this.userCpf = usuario.getCpf();
		this.id = usuario.getId();
		this.senha = usuario.getSenha();
		this.telefones = usuario.getTelefones();
	}

}
