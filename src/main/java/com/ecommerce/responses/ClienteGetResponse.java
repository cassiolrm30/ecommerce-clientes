package com.ecommerce.responses;
import lombok.Data;

@Data
public class ClienteGetResponse
{
	private String id;
	private String nome;
	private String telefone;
	private String email;
	private String senha;
	private String cadastradoEm;
	private String atualizadoEm;
}