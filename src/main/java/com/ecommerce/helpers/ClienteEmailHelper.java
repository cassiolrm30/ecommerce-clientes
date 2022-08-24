package com.ecommerce.helpers;
import com.ecommerce.dtos.EmailMessageDto;
import com.ecommerce.models.Cliente;

public class ClienteEmailHelper
{
	public static EmailMessageDto gerarMensagemDeCriacaoDeConta(Cliente cliente)
	{	
		String to = cliente.getEmail();
		String subject = "Parabéns, sua conta de cliente foi criada com sucesso!";
		String body = "Olá " + cliente.getNome()
				    + "\n\nSua conta de cliente foi cadastrada com sucesso no Ecommerce COTI."
				    + "\nSeus dados são:"
				    + "\nNome: " + cliente.getNome()
				    + "\nEmail: " + cliente.getEmail()
				    + "\nTelefone: " + cliente.getTelefone()
				    + "\n\nAtt"
				    + "\nEquipe COTI Informática";
		
		EmailMessageDto dto = new EmailMessageDto();
		dto.setTo(to);
		dto.setSubject(subject);
		dto.setBody(body);
		
		return dto;
	}	
}