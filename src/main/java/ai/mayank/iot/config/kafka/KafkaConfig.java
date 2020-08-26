package ai.mayank.iot.config.kafka;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.security.auth.SecurityProtocol;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import ai.mayank.iot.utils.inter_exchange.InterMessageProtocol;
/*
@Configuration
public class KafkaConfig {
	
    @Bean
    public ProducerFactory<String, InterMessageProtocol> producerFactory() {
    	Map properties = getProperties();
        properties.put("key.serializer", StringSerializer.class.getName());
        properties.put("value.serializer", JsonSerializer.class);
        
        return new DefaultKafkaProducerFactory<String,InterMessageProtocol>(properties);
    }
 
    
    @Bean
    public KafkaTemplate<String, InterMessageProtocol> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
    
	private HashMap<String,Object> getProperties(){
        HashMap<String,Object> properties = new HashMap();
        properties.put("bootstrap.servers", "116.75.243.36:30007");
        properties.put("acks", "0");
        properties.put("security.protocol", SecurityProtocol.SASL_PLAINTEXT.name);
        properties.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        properties.put(SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"ior-admin\" password=\"asfgscfvhfbgdvbhsdgsvvjdfgdfmfg\";");
        return properties;
    }
	
	
    @Bean
    public KafkaConsumer<String, InterMessageProtocol> getConsumer(){
    	Map properties = getProperties();
    	String groupId = "default";
    	try {
			groupId = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	properties.put("group.id",groupId);
    	KafkaConsumer<String, InterMessageProtocol> consumer  = new KafkaConsumer<String, InterMessageProtocol>(properties,new StringDeserializer(),new JsonDeserializer(InterMessageProtocol.class));
    	consumer.subscribe(Collections.singleton(ai.mayank.iot.utils.config.kafka.KafkaConfig.TOPIC));
    	return consumer;
    }
}
*/