package ai.mayank.iot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

@Configuration
public class MongoConfig extends AbstractMongoConfiguration{
	private final String PASSWORD = "IhbPJ6IXoNFqOEfi";
	
	 @Override
    protected String getDatabaseName() {
        return "ior";
    }
 
    @Override
    public MongoClient mongoClient() {
    	MongoClientURI uri = new MongoClientURI(String.format("mongodb+srv://ior-admin:%s@ior.y9m2w.gcp.mongodb.net/test",PASSWORD));
        return new MongoClient(uri);
    }
 
    @Override
    protected String getMappingBasePackage() {
        return "ai.mayank.iot.tables";
    }
}
