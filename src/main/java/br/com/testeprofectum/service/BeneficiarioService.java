package br.com.testeprofectum.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import br.com.testeprofectum.dto.BeneficiarioDTO;
import br.com.testeprofectum.enums.ErrosEnum;
import br.com.testeprofectum.enums.InfoEnums;
import br.com.testeprofectum.exception.RegraNegocioException;
import br.com.testeprofectum.model.Beneficiario;
import br.com.testeprofectum.model.Plano;
import br.com.testeprofectum.repository.BeneficiarioRepository;

@Service
public class BeneficiarioService {

	private BeneficiarioRepository repository;

	private PlanoService planoService;

	public BeneficiarioService(BeneficiarioRepository repository, PlanoService planoService) {
		this.repository = repository;
		this.planoService = planoService;
	}

	public Beneficiario criarBeneficiario(Beneficiario beneficiario) {
		Optional<Beneficiario> beneficiarioCheck = repository.findByCpf(beneficiario.getCpf());
		if (beneficiarioCheck.isPresent())
			throw new RegraNegocioException(ErrosEnum.ERRO_008.getMensagemErro());

		return repository.save(beneficiario);
	}

	public ResponseEntity<Object> listarTodosOsBeneficiarios() {
		List<Beneficiario> beneficiarios = verificarListaDeBeneficiarios();
		if (beneficiarios.size() == 0)
			return ResponseEntity.ok().body(InfoEnums.INFO_001.getMensagem());

		return ResponseEntity.ok(beneficiarios);
	}

	public Optional<Beneficiario> buscarBeneficiarioPorCPF(String cpf) {
		Optional<Beneficiario> beneficiario = repository.findByCpf(cpf);
		if (!beneficiario.isPresent())
			throw new RegraNegocioException(ErrosEnum.ERRO_002.getMensagemErro());

		return beneficiario;
	}

	public Beneficiario atualizarBeneficiario(String cpf, Beneficiario beneficiarioModificado) {
		if (checarBeneficiario(cpf).equals(null))
			throw new RegraNegocioException(ErrosEnum.ERRO_009.getMensagemErro());

		return repository.save(beneficiarioModificado);
	}

	public void deletarBeneficiario(String cpf) {
		Beneficiario beneficiario = checarBeneficiario(cpf);
		if (beneficiario.equals(null))
			throw new RegraNegocioException(ErrosEnum.ERRO_002.getMensagemErro());

		repository.delete(beneficiario);
	}

	public Beneficiario adicionarPlano(UUID idPlano, String cpf) {
		if (planoService.verificarListaDePlanos().size() == 0)
			throw new RegraNegocioException(ErrosEnum.ERRO_004.getMensagemErro());

		Optional<Plano> plano = planoService.buscarPlanoPorId(idPlano);
		if (!plano.isPresent())
			throw new RegraNegocioException(ErrosEnum.ERRO_011.getMensagemErro());
		
		Optional<Beneficiario> beneficiario = buscarBeneficiarioPorCPF(cpf);
		if(beneficiario.get().getPlano() != null)
			throw new RegraNegocioException(ErrosEnum.ERRO_012.getMensagemErro());
		
		beneficiario.get().setPlano(plano.get());
		
		return repository.save(beneficiario.get());
	}

	public Beneficiario converterDeDTO(BeneficiarioDTO dto) {
		Beneficiario beneficiario = Beneficiario.builder()
				.nome(dto.getNome())
				.cpf(dto.getCpf())
				.email(dto.getEmail())
				.build();

		return beneficiario;
	}

	public List<Beneficiario> verificarListaDeBeneficiarios() {
		return repository.findAll();
	}

	public boolean verificarExistencia(String cpf) {
		Optional<Beneficiario> beneficiario = repository.findByCpf(cpf);
		if (beneficiario.isPresent())
			return true;

		return false;
	}

	private Beneficiario checarBeneficiario(String cpf) {
		Beneficiario beneficiario = repository.findByCpf(cpf).get();
		if (beneficiario.equals(null))
			return null;

		return beneficiario;
	}
}
