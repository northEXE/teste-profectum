package br.com.testeprofectum.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.testeprofectum.model.Plano;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface PlanoRepository extends JpaRepository<Plano, UUID>{
	Optional<Plano> findById(UUID idPlano);
}
