package com.ecommerce.controllers;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.ecommerce.dtos.ClienteRequestDto;
import com.ecommerce.dtos.ClienteResponseDto;
import com.ecommerce.dtos.EmailMessageDto;
import com.ecommerce.helpers.ClienteEmailHelper;
import com.ecommerce.models.Cliente;
import com.ecommerce.producers.EmailMessageProducer;
import com.ecommerce.responses.ClienteGetResponse;
import com.ecommerce.services.ClienteService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "Clientes")
@RestController
public class ClienteController
{
	@Autowired
	private ClienteService clienteService;
	
	@Autowired
	private EmailMessageProducer emailMessageProducer;

	@Autowired
	private ObjectMapper objectMapper;

	private String dataMinima = "0001-01-01";
	SimpleDateFormat simpleDateFormatBR = new SimpleDateFormat("dd/MM/yyyy");
	SimpleDateFormat simpleDateFormatMySQL = new SimpleDateFormat("yyyy-MM-dd");

	@CrossOrigin
	@ApiOperation("Endpoint para consulta de clientes.")
	@RequestMapping(value = "/api/clientes", method = RequestMethod.GET)
	public ResponseEntity<List<ClienteGetResponse>> get()
	{	
		try
		{	
			List<ClienteGetResponse> lista = new ArrayList<ClienteGetResponse>();
			for (Cliente registro : clienteService.get())
			{
				ClienteGetResponse response = new ClienteGetResponse();
				response.setId(registro.get_id());
				response.setNome(registro.getNome().toUpperCase());
				response.setTelefone(registro.getTelefone());
				response.setEmail(registro.getEmail());
				if (registro.getCadastradoEm() != null && !simpleDateFormatBR.format(registro.getCadastradoEm()).equals(dataMinima))
					response.setCadastradoEm(simpleDateFormatBR.format(registro.getCadastradoEm()));
				if (registro.getAtualizadoEm() != null && !simpleDateFormatBR.format(registro.getAtualizadoEm()).equals(dataMinima))
					response.setAtualizadoEm(simpleDateFormatBR.format(registro.getAtualizadoEm()));
				lista.add(response);
			}
			return ResponseEntity.status(HttpStatus.OK).body(lista);
		}
		catch(Exception e)
		{
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@CrossOrigin
	@ApiOperation("Endpoint para consulta de 1 registro.")
	@RequestMapping(value = "/api/cliente/{id}", method = RequestMethod.GET)
	public ResponseEntity<ClienteGetResponse> get(@PathVariable String id)
	{
		try
		{
			ClienteGetResponse resultado = new ClienteGetResponse();
			for (Cliente registro : clienteService.get())
			{
				if (registro.get_id().equals(id))
				{
					resultado.setId(id);
					resultado.setNome(registro.getNome().toUpperCase());
					resultado.setTelefone(registro.getTelefone());
					resultado.setEmail(registro.getEmail());
					resultado.setSenha(registro.getSenha());
					if (registro.getCadastradoEm() != null && !simpleDateFormatBR.format(registro.getCadastradoEm()).equals(dataMinima))
						resultado.setCadastradoEm(simpleDateFormatBR.format(registro.getCadastradoEm()));
					if (registro.getAtualizadoEm() != null && !simpleDateFormatBR.format(registro.getAtualizadoEm()).equals(dataMinima))
						resultado.setAtualizadoEm(simpleDateFormatBR.format(registro.getAtualizadoEm()));
	    			return ResponseEntity.status(HttpStatus.OK).body(resultado);
				}
			}
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		catch(Exception e)
		{
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}
	
	@ApiOperation("Serviço para cadastro de clientes.")
	@PostMapping("/v1/clientes")
	public ResponseEntity<ClienteResponseDto> post(@Valid @RequestBody ClienteRequestDto dto)
	{
		ClienteResponseDto clienteResponse = new ClienteResponseDto();
		HttpStatus status = null;
		try
		{
			ModelMapper modelMapper = new ModelMapper();
			Cliente cliente = clienteService.save(modelMapper.map(dto, Cliente.class));	
			clienteResponse.setMessage("Cliente cadastrado com sucesso.");
			clienteResponse.setData(cliente);
			status = HttpStatus.CREATED;
			// enviando mensagem
			EmailMessageDto emailMessage = ClienteEmailHelper.gerarMensagemDeCriacaoDeConta(cliente);
			String message = objectMapper.writeValueAsString(emailMessage);
			emailMessageProducer.send(message);
		}
		catch (IllegalArgumentException e)
		{
			clienteResponse.setMessage(e.getMessage());
			status = HttpStatus.BAD_REQUEST;	
		}
		catch (JsonProcessingException e)
		{
			e.printStackTrace();
		}
		return ResponseEntity.status(status).body(clienteResponse);
	}

	@ApiOperation("Serviço para atualização de clientes.")
	@PutMapping("/v1/clientes")
	public ResponseEntity<ClienteResponseDto> put(@Valid @RequestBody ClienteRequestDto dto)
	{
		ClienteResponseDto clienteResponse = new ClienteResponseDto();
		HttpStatus status = null;
		try
		{
			ModelMapper modelMapper = new ModelMapper();
			if (dto.get_id().equals(""))
			{
				clienteResponse.setMessage("ID é obrigatório.");
				status = HttpStatus.NOT_MODIFIED;				
			}
			Cliente cliente = clienteService.save(modelMapper.map(dto, Cliente.class));
			clienteResponse.setMessage("Cliente atualizado com sucesso.");
			clienteResponse.setData(cliente);
			status = HttpStatus.OK;
		}
		catch (IllegalArgumentException e)
		{
			clienteResponse.setMessage(e.getMessage());
			status = HttpStatus.BAD_REQUEST;
		}
		return ResponseEntity.status(status).body(clienteResponse);
	}

	@ApiOperation("Serviço para exclusão de clientes.")
	@DeleteMapping("/v1/clientes")
	public ResponseEntity<ClienteResponseDto> delete(@PathVariable String id)
	{
		ClienteResponseDto clienteResponse = new ClienteResponseDto();
		HttpStatus status = null;
		try
		{
			ModelMapper modelMapper = new ModelMapper();
			Cliente cliente = new Cliente();
			cliente.set_id(id);
			cliente = clienteService.delete(modelMapper.map(cliente, Cliente.class));
			clienteResponse.setMessage("Dados excluídos com sucesso.");
			clienteResponse.setData(cliente);
			status = HttpStatus.OK;
		}
		catch (IllegalArgumentException e)
		{
			clienteResponse.setMessage(e.getMessage());
			status = HttpStatus.BAD_REQUEST;
		}
		catch (Exception e)
		{
			clienteResponse.setMessage(e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return ResponseEntity.status(status).body(clienteResponse);
	}
}