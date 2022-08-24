package com.ecommerce.dtos;
import com.ecommerce.models.Cliente;
import lombok.Data;

@Data
public class ClienteResponseDto
{
	private String message;
	private Cliente data;
}