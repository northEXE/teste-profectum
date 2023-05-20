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

import br.com.testeprofectum.dto.BeneficiarioDTO;
import br.com.testeprofectum.enums.ErrosEnum;
import br.com.testeprofectum.exception.RegraNegocioException;
import br.com.testeprofectum.model.Beneficiario;
import br.com.testeprofectum.service.BeneficiarioService;
import br.com.testeprofectum.utils.ResponseErrosUtil;

@RestController
@RequestMapping(path = "/beneficiarios")
public class BeneficiarioController {

	private BeneficiarioService service;

	public BeneficiarioController(BeneficiarioService service) {
		this.service = service;
	}

	@PostMapping(path = "/salvar")
	public ResponseEntity<Object> salvarBeneficiario(@RequestBody BeneficiarioDTO dto) {
		if(dto == null) 
			ResponseErrosUtil.respostaErro010();
		
		try {
			Beneficiario beneficiario = service.converterDeDTO(dto);
			beneficiario = service.criarBeneficiario(beneficiario);
			return new ResponseEntity<>(beneficiario, HttpStatus.CREATED);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@GetMapping
	public ResponseEntity<Object> listarTodosOsBeneficiarios() {
		return service.listarTodosOsBeneficiarios();
	}

	@GetMapping(path = "/buscar")
	public ResponseEntity<Object> buscarBeneficiario(@RequestParam String cpf) {
		try {
			Optional<Beneficiario> beneficiario = service.buscarBeneficiarioPorCPF(cpf);
			return ResponseEntity.ok(beneficiario);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PutMapping(path = "/{cpf}/atualizar")
	public ResponseEntity<? extends Object> atualizarBeneficiario(@PathVariable String cpf, @RequestBody BeneficiarioDTO dto) {
		if(dto == null) 
			ResponseErrosUtil.respostaErro010();
			
		if (service.verificarListaDeBeneficiarios().size() == 0)
			return ResponseErrosUtil.respostaErro004();
		
		if(service.verificarExistencia(cpf) == false)
			return ResponseErrosUtil.respostaErro002();
		
		return service.buscarBeneficiarioPorCPF(cpf).map(entidade -> {
			try {
				Beneficiario beneficiario = service.converterDeDTO(dto);
				beneficiario.setIdBeneficiario(entidade.getIdBeneficiario());
				beneficiario.setCpf(entidade.getCpf());
				service.atualizarBeneficiario(cpf, beneficiario);
				return ResponseEntity.ok(beneficiario);
			} catch (RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getLocalizedMessage());
			}
		}).orElseGet(() -> new ResponseEntity<Object>(ErrosEnum.ERRO_003.getMensagemErro(), HttpStatus.BAD_REQUEST));
	}
	
	@PutMapping(path = "/{cpf}/adicionar-plano")
	public ResponseEntity<? extends Object> adicionarPlano(@RequestParam UUID idPlano, @PathVariable String cpf){
		if (service.verificarListaDeBeneficiarios().size() == 0)
			return ResponseErrosUtil.respostaErro004();
		
		if(service.verificarExistencia(cpf) == false)
			return ResponseErrosUtil.respostaErro002();
		
		return service.buscarBeneficiarioPorCPF(cpf).map(entidade -> {
			try {
				Beneficiario beneficiario = service.adicionarPlano(idPlano, cpf);
				return ResponseEntity.ok(beneficiario);
			} catch(RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getLocalizedMessage());
			}
		}).orElseGet(() -> new ResponseEntity<Object>(ErrosEnum.ERRO_003.getMensagemErro(), HttpStatus.BAD_REQUEST));
	}

	@DeleteMapping(path = "/deletar/{cpf}")
	public ResponseEntity<? extends Object> deletarBeneficiario(@PathVariable String cpf) {
		if (service.verificarListaDeBeneficiarios().size() == 0)
			return ResponseErrosUtil.respostaErro004();
		
		if(service.verificarExistencia(cpf) == false)
			return ResponseErrosUtil.respostaErro002();
		
		return service.buscarBeneficiarioPorCPF(cpf).map(entidade -> {
			try {
				service.deletarBeneficiario(cpf);
				return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
			} catch (RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
		}).orElseGet(() -> new ResponseEntity<Object>(ErrosEnum.ERRO_003.getMensagemErro(), HttpStatus.BAD_REQUEST));
	}	
}