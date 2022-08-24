package com.ecommerce.dtos;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthenticationRequestDto
{
	@Email(message = "E-mail é inválido.")
	@NotBlank(message = "E-mail é obrigatório.")
	private String email;

	@Size(min = 8, max = 20, message = "Senha é obrigatória.")
	@NotBlank(message = "Senha é obrigatória.")
	private String senha;
}