package br.com.testeprofectum.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BeneficiarioDTO {
	
	private String nome;
	private String cpf;
	private String email;
	private Integer idade;
	private UUID idPlano;
}
