package ai.mayank.iot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

@Configuration
public class MongoConfig extends AbstractMongoConfiguration{
	@Value("${mongo.uri}")
	private String mongo_uri;
	
	@Override
    protected String getDatabaseName() {
        return "ior";
    }
 
    @Override
    public MongoClient mongoClient() {
    	MongoClientURI uri = new MongoClientURI(mongo_uri);
        return new MongoClient(uri);
    }
 
    @Override
    protected String getMappingBasePackage() {
        return "ai.mayank.iot.tables";
    }
}
