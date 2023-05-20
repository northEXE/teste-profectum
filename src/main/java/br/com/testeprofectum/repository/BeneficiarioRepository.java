package br.com.testeprofectum.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.testeprofectum.model.Beneficiario;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface BeneficiarioRepository extends JpaRepository<Beneficiario, UUID> {
	Optional<Beneficiario> findById(UUID idBeneficiario);
	Optional<Beneficiario> findByCpf(String cpf);
}
