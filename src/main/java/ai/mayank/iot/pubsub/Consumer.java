package ai.mayank.iot.pubsub;


import java.time.Duration;
import java.util.Iterator;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.InterruptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ai.mayank.iot.utils.inter_exchange.InterMessageProtocol;

@Component
public class Consumer extends Thread{
	@Autowired(required = false)
	KafkaConsumer<String, InterMessageProtocol> consumer;
	
	private Logger logger = LoggerFactory.getLogger(Consumer.class);
	
    @PostConstruct
    public void init() {
        this.start();
        logger.info("Starting Consumer");
    }
    
    @PreDestroy
    public void destroy() {
    	this.interrupt();
    	logger.info("Closing Consumer");
    	this.consumer.close();
    }
    
    public void run() {
    	if(consumer == null) {
    		logger.warn("Couldnot start kafka consumer");
    		return;
    	}
    	while(!this.isInterrupted()) {
    		try {
	    		ConsumerRecords<String, InterMessageProtocol> protocols = consumer.poll(Duration.ofMillis(1000));
	    		Iterator<ConsumerRecord<String, InterMessageProtocol>> iterator = protocols.iterator();
	    		while(iterator.hasNext()) {
	    			ConsumerRecord<String,InterMessageProtocol> record = iterator.next();
	    			logger.info(record.value().toString());
	    		}
    		}
	    	catch(Exception ex) {
	    		if(ex instanceof InterruptException)
	    			break;
	    		else
	    			ex.printStackTrace();
	    	}
    	}
    }
}
