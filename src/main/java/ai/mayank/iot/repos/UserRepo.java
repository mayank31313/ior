package ai.mayank.iot.repos;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import ai.mayank.iot.tables.User;

public interface UserRepo extends MongoRepository<User, String > {
	Optional<User> findByUuid(String uuid);
}
