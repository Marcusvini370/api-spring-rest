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

	/* Tempo de Validade do Token 2 dias milissegundos */
	private static final long EXPIRATION_TIME = 172800000;

	/* Uma senha única para compo a autenticação */
	private static final String SECRET = "SenhaExtremamenteSecreta";

	/* Prefixo padrão de Token */
	private static final String TOKEN_PREFIX = "Bearer";

	private static final String HEADER_STRING = "Authorization";

	/* Gerando token de autenticado e adicionando ao cabeçalho e reposta Http */
	public void addAuthentication(HttpServletResponse response, String username) throws IOException {

		/* Montagem do Token */
		String JWT = Jwts.builder() /* Chama o Gerador de Token */
				.setSubject(username) /* Adiciona o usuário */
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) /* Tempo de expiração */
				.signWith(SignatureAlgorithm.HS512, SECRET).compact(); /* Compactação e algoritmo de geração de senha */

		String token = TOKEN_PREFIX + " " + JWT; /* vai juntar o prefixo com o jwt e gerar o token */

		/* Adiciona no cabeçalho http */
		response.addHeader(HEADER_STRING, token); /* Authorization : Bearer w87ew87e8w7e8w7e8w7e8w7e8 */

		/* Liberando resposta para porta diferente do projeto Angular */
		liberarCors(response);

		ApplicationContextLoad.getApplicationContext().getBean(UsuarioRepository.class).atualizaTokenUser(JWT,
				username);

		/* Escreve token como resposta no corpo http */
		response.getWriter().write("{\"Authorization\": \"" + token + "\"}");
	}

	/*
	 * Retorna o usuário validado com token ou caso não seja validado retorna null
	 */
	public Authentication getAuthentication(HttpServletRequest request, HttpServletResponse response) {

		/* Pega o token enviado no cabeçalho http */
		String token = request.getHeader(HEADER_STRING);

		try {

			/* Liberando resposta para portas difetentes que usam api ou clientes web */
			liberarCors(response);

			if (token != null) {

				String tokenLimpo = token.replace(TOKEN_PREFIX, "").trim();

				/* Faz a validação do token do usuário na requisição */
				String user = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(tokenLimpo).getBody()
						.getSubject(); /* usuário */

				if (user != null) {
					Usuario usuario = ApplicationContextLoad.getApplicationContext().getBean(UsuarioRepository.class)
							.findUserByLogin(user);

					if (usuario != null) {

						if (tokenLimpo.equalsIgnoreCase(usuario.getToken())) { // se o token do usuário que veio por
																				// requisição for igual ao do bd será
																				// validado

							return new UsernamePasswordAuthenticationToken(usuario.getLogin(), usuario.getSenha(),
									usuario.getAuthorities());
						}

					}
				}

			} /* Fim condição token */

		} catch (io.jsonwebtoken.ExpiredJwtException e) {
			try {
				response.getOutputStream()
						.println("Seu TOKEN está expirado, faça o login ou informe o novo Token para Autenticação");
			} catch (IOException e1) {
			}
		}

		/* Liberando resposta para porta diferente do projeto Angular */
		liberarCors(response);
		return null; // acesso não autorizzado
	}

	private void liberarCors(HttpServletResponse response) {

		if (response.getHeader("Access-Control-Allow-Origin") == null) {
			// liberando a resposta e requisição da api pro usuario
			response.addHeader("Access-Control-Allow-Origin", "*");
		}

		if (response.getHeader("Access-Control-Allow-Headers") == null) {

			response.addHeader("Access-Control-Allow-Headers", "*");
		}

		if (response.getHeader("Access-Contro-Request-Headers") == null) {
			response.addHeader("Access-Contro-Request-Headers", "*");
		}

		if (response.getHeader("Access-Control-Allow-Methods") == null) {
			response.addHeader("Access-Control-Allow-Methods", "*");

		}
	}

}