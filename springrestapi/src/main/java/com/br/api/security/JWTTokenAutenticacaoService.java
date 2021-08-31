package com.br.api.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.br.api.ApplicationContextLoad;
import com.br.api.model.Usuario;
import com.br.api.repository.UsuarioRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
@Component
public class JWTTokenAutenticacaoService {
	
	/* Tempo  de Validade do Token 2 dias milissegundos*/
	private static final long EXPIRATION_TIME = 172800000;
	
	/* Uma senha única para compo a autenticação */
	private static final String SECRET = "SenhaExtremamenteSecreta";
	
	/* Prefixo padrão de Token */
	private static final String TOKEN_PREFIX = "Bearer";
	
	private static final String HEADER_STRING =  "Authorization";
	
	/* Gerando token de autenticado e adicionando ao cabeçalho e reposta Http */
	public void addAuthentication(HttpServletResponse response, String username) throws IOException {
		
		/* Montagem do Token */
		String JWT = Jwts.builder() /*Chama o Gerador de Token */
				.setSubject(username) /* Adiciona o usuário */
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) /* Tempo de expiração */
				.signWith(SignatureAlgorithm.HS512, SECRET).compact(); /* Compactação e algoritmo de geração de senha */
		
		String token = TOKEN_PREFIX + "" +  JWT; /* vai juntar o prefixo com o jwt e gerar o token*/
		
		/* Adiciona no cabeçalho http */
		response.addHeader(HEADER_STRING, token); /* Authorization : Bearer w87ew87e8w7e8w7e8w7e8w7e8 */
		
		/* Escreve token como resposta no corpo http */
		response.getWriter().write("{\"Authorization\": \""+token+"\"}");
	}
	
	/*
	 * Retorna o usuário validado com token ou caso não seja validado retorna null
	 */
	public Authentication getAuthentication(HttpServletRequest request) {

		/* Pega o token enviado no cabeçalho http */
		String token = request.getHeader(HEADER_STRING);

		if (token != null) {

			/* Faz a validação do token do usuário na requisição */
			String user = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token.replace(TOKEN_PREFIX, "")).getBody()
					.getSubject(); /* usuário */

			if (user != null) {
				Usuario usuario = ApplicationContextLoad.getApplicationContext().getBean(UsuarioRepository.class)
						.findUserByLogin(user);

				if (usuario != null) {
					return new UsernamePasswordAuthenticationToken(usuario.getLogin(), usuario.getSenha(),
							usuario.getAuthorities());

				}
			}

		}
		return null;
	}
	

}
