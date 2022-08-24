package com.ecommerce.requests;
import lombok.Data;

@Data
public class ClienteRequest
{
	private String id;
	private String nome;
	private String telefone;
	private String email;
	private String cadastradoEm;
	private String atualizadoEm;
}