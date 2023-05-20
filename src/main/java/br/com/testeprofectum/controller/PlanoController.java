package br.com.testeprofectum.controller;

import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.testeprofectum.dto.PlanoDTO;
import br.com.testeprofectum.enums.ErrosEnum;
import br.com.testeprofectum.exception.RegraNegocioException;
import br.com.testeprofectum.model.Plano;
import br.com.testeprofectum.service.PlanoService;
import br.com.testeprofectum.utils.ResponseErrosUtil;

@RestController
@RequestMapping(path = "/planos")
public class PlanoController {

	private PlanoService service;

	public PlanoController(PlanoService service) {
		this.service = service;
	}

	@PostMapping(path = "/salvar")
	public ResponseEntity<Object> salvarPlano(@RequestBody PlanoDTO dto) {
		if(dto == null) 
			ResponseErrosUtil.respostaErro010();
		
		try {
			Plano plano = service.converterDeDTO(dto);
			plano = service.criarPlano(plano);
			return new ResponseEntity<>(plano, HttpStatus.CREATED);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@GetMapping
	public ResponseEntity<Object> listarTodosOsPlanos() {
		return service.listarTodosOsPlanos();
	}

	@GetMapping(path = "/buscar")
	public ResponseEntity<Object> buscarPlano(@RequestParam UUID idPlano) {
		try {
			Optional<Plano> plano = service.buscarPlanoPorId(idPlano);
			return ResponseEntity.ok(plano);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PutMapping(path = "/{cpf}/atualizar")
	public ResponseEntity<? extends Object> atualizarPlano(@PathVariable UUID idPlano, @RequestBody PlanoDTO dto) {
		if(dto == null) 
			ResponseErrosUtil.respostaErro010();
			
		if (service.verificarListaDePlanos().size() == 0)
			return ResponseErrosUtil.respostaErro004();
		
		if(service.verificarExistencia(idPlano) == false)
			return ResponseErrosUtil.respostaErro002();
		
		return service.buscarPlanoPorId(idPlano).map(entidade -> {
			try {
				Plano plano = service.converterDeDTO(dto);
				plano.setIdPlano(entidade.getIdPlano());
				service.atualizarPlano(idPlano, plano);
				return ResponseEntity.ok(plano);
			} catch (RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getLocalizedMessage());
			}
		}).orElseGet(() -> new ResponseEntity<Object>(ErrosEnum.ERRO_003.getMensagemErro(), HttpStatus.BAD_REQUEST));
	}

	@DeleteMapping(path = "/deletar/{cpf}")
	public ResponseEntity<? extends Object> deletarPlano(@PathVariable UUID idPlano) {
		if (service.verificarListaDePlanos().size() == 0)
			return ResponseErrosUtil.respostaErro004();
		
		if(service.verificarExistencia(idPlano) == false)
			return ResponseErrosUtil.respostaErro002();
		
		return service.buscarPlanoPorId(idPlano).map(entidade -> {
			try {
				service.deletarPlano(idPlano);
				return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
			} catch (RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
		}).orElseGet(() -> new ResponseEntity<Object>(ErrosEnum.ERRO_003.getMensagemErro(), HttpStatus.BAD_REQUEST));
	}
	
}
