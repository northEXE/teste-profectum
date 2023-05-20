package br.com.testeprofectum.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import br.com.testeprofectum.dto.PlanoDTO;
import br.com.testeprofectum.enums.ErrosEnum;
import br.com.testeprofectum.enums.InfoEnums;
import br.com.testeprofectum.exception.RegraNegocioException;
import br.com.testeprofectum.model.Plano;
import br.com.testeprofectum.repository.PlanoRepository;


@Service
public class PlanoService {

	private PlanoRepository repository;

	public PlanoService(PlanoRepository repository) {
		this.repository = repository;
	}

	public Plano criarPlano(Plano plano) {
		Optional<Plano> planoCheck = repository.findById(plano.getIdPlano());
		if (planoCheck.isPresent())
			throw new RegraNegocioException(ErrosEnum.ERRO_008.getMensagemErro());

		return repository.save(plano);
	}

	public ResponseEntity<Object> listarTodosOsPlanos() {
		List<Plano> planos = verificarListaDePlanos();
		if (planos.size() == 0) 
			return ResponseEntity.ok().body(InfoEnums.INFO_001.getMensagem());
		
		return ResponseEntity.ok(planos);
	}

	public Optional<Plano> buscarPlanoPorId(UUID idPlano) {
		Optional<Plano> plano = repository.findById(idPlano);
		if (!plano.isPresent())
			throw new RegraNegocioException(ErrosEnum.ERRO_002.getMensagemErro());

		return plano;
	}

	public Plano atualizarPlano(UUID idPlano, Plano planoModificado) {
		if (checarPlano(idPlano).equals(null))
			throw new RegraNegocioException(ErrosEnum.ERRO_009.getMensagemErro());

		return repository.save(planoModificado);
	}

	public void deletarPlano(UUID idPlano) {
		Plano plano = checarPlano(idPlano);
		if (plano.equals(null))
			throw new RegraNegocioException(ErrosEnum.ERRO_002.getMensagemErro());

		repository.delete(plano);
	}

	public Plano converterDeDTO(PlanoDTO dto) {
		Plano plano = Plano.builder()
				.nome(dto.getNome())
				.valor(dto.getValor())
				.build();

		return plano;
	}
	
	public List<Plano> verificarListaDePlanos() {
		return repository.findAll();
	}
	
	public boolean verificarExistencia(UUID idPlano){
		Optional<Plano> plano = repository.findById(idPlano);
		if(plano.isPresent())
			return true;
		
		return false;
	}
	
	private Plano checarPlano(UUID idPlano) {
		Plano plano = repository.findById(idPlano).get();
		if (plano.equals(null))
			return null;
		
		return plano;
	}
}
