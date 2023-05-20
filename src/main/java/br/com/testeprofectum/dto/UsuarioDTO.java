package br.com.testeprofectum.dto;

import lombok.Builder;
import lombok.Data;


@Builder
@Data
public class UsuarioDTO {
	
	private String login;
	private String nome;
	private String senha;
}
