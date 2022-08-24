package com.ecommerce.producers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailMessageProducer
{
	// nome do tópico (fila) para o qual a mensagem será enviada
	@Value("${topic.name.producer}")
	private String topico;

	// executar o apache kafka
	private final KafkaTemplate<String, String> kafkaTemplate;

	// método para disparar a mensagem para a fila
	public void send(String message)
	{
		kafkaTemplate.send(topico, message);
	}
}