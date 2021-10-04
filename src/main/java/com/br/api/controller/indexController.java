package com.br.api.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.tomcat.util.codec.binary.Base64;
import org.apache.tomcat.util.http.parser.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.br.api.model.UserChart;
import com.br.api.model.UserReport;
import com.br.api.model.Usuario;
import com.br.api.model.UsuarioDTO;
import com.br.api.repository.TelefoneRepository;
import com.br.api.repository.UsuarioRepository;
import com.br.api.service.ImplementacaoUserDetailsService;
import com.br.api.service.ServiceRelatorio;
import com.google.gson.Gson;






@RestController // Arquitetura REST
@RequestMapping(value = "/usuario")
public class indexController {

	@Autowired // se fosse CDI seria @Inject
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private ImplementacaoUserDetailsService implementacaoUserDetailsService;
	
	@Autowired
	private TelefoneRepository telefoneRepository;
	
	@Autowired
	private ServiceRelatorio serviceRelatorio;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	
	
	/* Serviço RESTfull */
	@GetMapping(value = "/id/{id}") // Lista usuário por id
	@CacheEvict(value = "cacheuser" , allEntries = true)  // se tiver cache que não é usado vai remover
	@CachePut(value = "cacheputuser") // se tem mudanças ou dados novos no banco, vai trazer para o cache
	public ResponseEntity<UsuarioDTO> initV1(@PathVariable(value = "id") Long id) {

		Optional<Usuario> usuario = usuarioRepository.findById(id);
		return new ResponseEntity<UsuarioDTO>(new UsuarioDTO(usuario.get()), HttpStatus.OK);
	}
	
/*               Métodos de Get buscar por id             */
	
	@GetMapping(value = "/{id}", produces = "application/json")
	@CacheEvict(value="buscarusers" ,allEntries = true )  
	@CachePut("buscarusers")
	public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {

		// vai retorna a pessoa se der certo retorna um status ok , se n encontrar dá um
		// notofund
		return usuarioRepository.findById(id).map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}
	
	@GetMapping()
	@CacheEvict(value="listausers" ,allEntries = true )  
	@CachePut("listausers")
	public ResponseEntity<Page<Usuario>> listarTodos() throws InterruptedException {

		//paginação de 5 em 5 ordenado por nome
		PageRequest page = PageRequest.of(0, 5, Sort.by("nome"));
		
		//find all retorna uma implementação de página ai passamos nosso page configurado
		Page<Usuario> list = usuarioRepository.findAll(page);
		
         //vai retorna uma página configurada do tipo usuários na lista
		return new ResponseEntity<Page<Usuario>>(list, HttpStatus.OK);
		
	} 
	

	
	@GetMapping(value = "/page/{pagina}")
	@CacheEvict(value = "cacheusuarios" , allEntries = true)  // se tiver cache que não é usado vai remover
	@CachePut(value = "cacheputusuarios") // se tem mudanças ou dados novos no banco, vai trazer para o cache
	public ResponseEntity<Page<Usuario>> usuarioPagina(@PathVariable("pagina") int pagina) throws InterruptedException {

		//paginação de 5 em 5 ordenado por nome, passa a página como parametro que recebe as posição da pagina
		//de acordo com oque agente clica
		PageRequest page = PageRequest.of(pagina, 5, Sort.by("nome"));
		
		//find all retorna uma implementação de página ai passamos nosso page configurado
		Page<Usuario> usuarios = usuarioRepository.findAll(page);
		
         //vai retorna uma página configurada do tipo usuários na lista
		return new ResponseEntity<Page<Usuario>>(usuarios, HttpStatus.OK);
		
	}

 
	@PostMapping(value = "/")
	public ResponseEntity<Usuario> cadastrar(@Validated @RequestBody Usuario usuario) throws Exception {
		
		for(int pos = 0; pos < usuario.getTelefones().size(); pos ++ ) {
			usuario.getTelefones().get(pos).setUsuario(usuario); // vai amarrar os telefones aos usuários pertencentes
		}
		
		
		/*
		// Consumindo API publica externa do cep
		
		URL url  = new URL("https://viacep.com.br/ws/"+usuario.getCep()+"/json/");
		URLConnection connection = url.openConnection();
		InputStream is = connection.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		
		String cep = "";
		StringBuilder jsonCep = new StringBuilder();
		
		while((cep = br.readLine()) != null) {
			jsonCep.append(cep);
		}
			
			
			Usuario userAux = new Gson().fromJson(jsonCep.toString(), Usuario.class);
			
			usuario.setCep(userAux.getCep());
			usuario.setLogradouro(userAux.getLogradouro());
			usuario.setComplemento(userAux.getComplemento());
			usuario.setBairro(userAux.getBairro());
			usuario.setLocalidade(userAux.getLocalidade());
			usuario.setUf(userAux.getUf());
			
			
		
		// Consumindo API publica externa */
		
		String senhacriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
		usuario.setSenha(senhacriptografada);
		Usuario usuarioSalvo = usuarioRepository.save(usuario);

		implementacaoUserDetailsService.insereAcessoPadrao(usuarioSalvo.getId());
		
		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);
	}

	@PutMapping(value = "/")
	public ResponseEntity<Usuario> atualizar(@RequestBody Usuario usuario) {

		/* Outras rotinas antes de atualizar */
		
		
		for(int pos = 0; pos < usuario.getTelefones().size(); pos++) {
	     	   usuario.getTelefones().get(pos).setUsuario(usuario); 
	        }
		
		Usuario userTemporario = usuarioRepository.findById(usuario.getId()).get();
		
		//senhas diferente
		if(!userTemporario.getPassword().equals(usuario.getPassword())) {
			//se for diferente vai criptografar a senha nova do usuario
			String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
			usuario.setSenha(senhaCriptografada);
		}

		Usuario UsuarioAtualizar= usuarioRepository.save(usuario);

		return 	ResponseEntity.ok(UsuarioAtualizar);
		//ou return new ResponseEntity<Usuario>(Usuariosalvo, HttpStatus.OK);
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") Long id) {

		usuarioRepository.deleteById(id);

		return ResponseEntity.noContent().build();

	}
	
	//END-POINT 
		@GetMapping(value = "/usuarioPorNome/{nome}", produces = "application/json")
		public ResponseEntity<Page<Usuario>> usuarioPorNome (@PathVariable("nome") String nome) throws InterruptedException{
			
			PageRequest pageRequest = null;
			Page<Usuario> list = null;
			
			if (nome == null || (nome != null && nome.trim().isEmpty()) || nome.equalsIgnoreCase("undefined")) {
				pageRequest = PageRequest.of(0, 5, Sort.by("nome"));
				list = usuarioRepository.findAll(pageRequest);
			} else {
				pageRequest = PageRequest.of(0, 5, Sort.by("nome"));
				list = usuarioRepository.findUserByNamePage(nome, pageRequest);
			}
							
			return new ResponseEntity<Page<Usuario>>(list, HttpStatus.OK);
			
		}
	
	/* END-POINT de consultar de usuário por nome */
	@GetMapping("/usuarioPorNome/{nome}/page/{page}")
	@CacheEvict(value="listanome" ,allEntries = true )  
	@CachePut("listanome")
	public ResponseEntity<Page<Usuario>> buscarPorNomePage(@PathVariable("nome") String nome,
			@PathVariable("page") int page) throws InterruptedException {
			
		
		PageRequest pageRequest  = null;
		Page<Usuario> list = null;
				
		/* não informou o nome e deixou vazio o campo de pesquisa, continua na paginação*/
		if(nome == null || (nome != null && nome.trim().isEmpty())
			|| nome.equalsIgnoreCase("undefined")) {
			
			pageRequest = PageRequest.of(page, 5, Sort.by("nome"));
			 list = usuarioRepository.findAll(pageRequest);
		
			/*Informou o nome e faz o método de consulta por paginação*/
		}else {
			pageRequest = PageRequest.of(page, 5, Sort.by("nome"));
			list = usuarioRepository.findUserByNamePage(nome, pageRequest);
			
		}		
		//método de consulta sem paginação
		//List<Usuario> list = usuarioRepository.findByNome(nome.trim().toUpperCase());
		
		return new ResponseEntity<Page<Usuario>>(list, HttpStatus.OK);
	}
	
	@DeleteMapping(value = "/removerTelefone/{id}")
	public String deleteTelefone(@PathVariable("id") Long id) {
		telefoneRepository.deleteById(id);
		
		return "ok";
		
	}
	/*Endpoint Relatório - dowload*/
	@GetMapping("/relatorio") //obter um relatório
	public ResponseEntity<String> dowloadRelatorio(HttpServletRequest request) throws Exception {
		
		/*nome dinâmico do relatorio que queremos , getServletContext pra carregar onde ele está contexto*/
		byte[] pdf = serviceRelatorio.gerarRelatorio("relatorio-usuario", new HashMap(),
				request.getServletContext());
		
		/*base 63 que fica pronta para ser impressa e processadaem qlq lugar*/
		String base64Pdf = "data:application/pdf;base64," + Base64.encodeBase64String(pdf);
		
		return new ResponseEntity<String>(base64Pdf,HttpStatus.OK);
		
	}
	
	/* Endpoint Relatório com parametro */
	@PostMapping("/relatorio/") //obter um relatório
	public ResponseEntity<String> dowloadRelatorioParam(HttpServletRequest request, @RequestBody UserReport userReport) throws Exception {
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy"); /*Formato que está vindo da tela */
		
		SimpleDateFormat dateFormatParam = new SimpleDateFormat("yyyy/MM/dd"); /* Converter para Formmato do parametro do jasper */
		
		/*Faz a formatação da data*/
		String dataInicio = dateFormatParam.format(dateFormat.parse(userReport.getDataInicio()));
		
		String dataFim = dateFormatParam.format(dateFormat.parse(userReport.getDataFim()));
		
		Map<String, Object> params = new HashMap<String, Object>();
				
				/*parametros do relátorio passando as data que veio pelo usuário*/
				params.put("DATA_INICIO", dataInicio); 
				params.put("DATA_FIM", dataFim);
		
		
		/*nome dinâmico do relatorio que queremos , getServletContext pra carregar onde ele está contexto*/ 
				byte[] pdf = serviceRelatorio.gerarRelatorio("relatorio-usuario-param", params,
						request.getServletContext());
		
		/*base 63 que fica pronta para ser impressa e processadaem qlq lugar*/
		String base64Pdf = "data:application/pdf;base64," + Base64.encodeBase64String(pdf);
		
		return new ResponseEntity<String>(base64Pdf,HttpStatus.OK);
		
	}
	
	@GetMapping(value="/grafico")
	public ResponseEntity<UserChart> grafico(){
		
		UserChart userChart = new UserChart();
		
		/*Retorna duas lista 1 lista dos nome 2 lista com os salários */

	List<String> resultado =	jdbcTemplate.queryForList("select array_agg(nome) from usuario where salario > 0 and nome <> '' union all "
				+ "select cast(array_agg(salario) as character varying[]) from usuario where salario > 0 and nome <> '';", String.class);
		
		if(!resultado.isEmpty()) {
			/*vai remover as chaves por vázios pq temos que ter uma array n um objeto na posição 0*/
			String nomes = resultado.get(0).replaceAll("\\{", "").replaceAll("\\}", "");
			String salario = resultado.get(1).replaceAll("\\{", "").replaceAll("\\}", "");
			
			userChart.setSalario(salario); 
			userChart.setNome(nomes);
		}
	
	
		return new ResponseEntity<UserChart>(userChart, HttpStatus.OK);
		
	}

	
	
}
