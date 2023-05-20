package br.com.testeprofectum.enums;

import lombok.Getter;

@Getter
public enum InfoEnums {
	INFO_001("Não há usuários a serem listados");
	
	private String mensagem;
	
	InfoEnums(String mensagem){
		this.mensagem = mensagem;
	}
	
}
