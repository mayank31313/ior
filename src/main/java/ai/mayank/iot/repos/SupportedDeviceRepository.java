package ai.mayank.iot.repos;

import org.springframework.data.mongodb.repository.MongoRepository;

import ai.mayank.iot.tables.SupportedDevices;

public interface SupportedDeviceRepository extends MongoRepository<SupportedDevices, String>{
}
