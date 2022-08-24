package com.ecommerce.services;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ecommerce.models.Cliente;
import com.ecommerce.repositories.IClienteRepository;

@Service
public class ClienteService
{
	@Autowired
	private IClienteRepository clienteRepository;
	
	public Cliente save(Cliente cliente)
	{		
		if (clienteRepository.findByEmail(cliente.getEmail()).isPresent())
			throw new IllegalArgumentException("E-mail já cadastrado, tente outro.");
		
		if (clienteRepository.findByTelefone(cliente.getTelefone()).isPresent())
			throw new IllegalArgumentException("Telefone já cadastrado, tente outro.");

		cliente.setSenha(getHashMd5(cliente.getSenha()));
		cliente.setCadastradoEm(Instant.now());
		cliente.setAtualizadoEm(Instant.now());
		clienteRepository.save(cliente);
		//if (cliente.get_id().equals(""))
		//	clienteRepository.insert(cliente);
		//else
		//	clienteRepository.insert(cliente);
		return cliente;
	}	
	
	public Cliente delete(Cliente cliente)
	{
		if (cliente.get_id().equals("") || clienteRepository.findById(cliente.get_id()).isPresent())
			throw new IllegalArgumentException("Registro não encontrado.");
		clienteRepository.delete(cliente);
		return cliente;
	}	

	public List<Cliente> get()
	{	
		return clienteRepository.findAll();
	}

	public Cliente get(String email, String senha)
	{	
		Optional<Cliente> optional = clienteRepository.findByEmailAndSenha(email, getHashMd5(senha));		
		if (optional.isEmpty())
			throw new IllegalArgumentException("Dados inválidos, cliente não encontrado.");
		return optional.get();
	}
	
	private static String getHashMd5(String value)
	{
		MessageDigest md;
		try
		{
			md = MessageDigest.getInstance("MD5");
		}
		catch (NoSuchAlgorithmException e)
		{
			throw new RuntimeException(e);
		}
		BigInteger hash = new BigInteger(1, md.digest(value.getBytes()));
		return hash.toString(16);
	}
}