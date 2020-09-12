package ai.mayank.iot.repos;

import java.util.Optional;
import java.util.logging.SocketHandler;

import org.springframework.data.mongodb.repository.MongoRepository;

import ai.mayank.iot.tables.SocketVariables;

public interface SocketHandlerRepository extends MongoRepository<SocketVariables,String>{
	Optional<SocketVariables> findByTempId(String token);
}
