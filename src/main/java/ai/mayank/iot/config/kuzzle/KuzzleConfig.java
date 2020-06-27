package ai.mayank.iot.config.kuzzle;

import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import io.kuzzle.sdk.Kuzzle;
import io.kuzzle.sdk.Options.KuzzleOptions;
import io.kuzzle.sdk.Options.Protocol.WebSocketOptions;
import io.kuzzle.sdk.Protocol.WebSocket;

@Configuration
public class KuzzleConfig {
	Logger log = LoggerFactory.getLogger(KuzzleConfig.class);
	
	@Bean
	public Kuzzle getKuzzleInstance(Environment env) {
		String kuzzleServer = "192.168.46.12";
		if(env.containsProperty("KUZZLE_SERVER")) {
			kuzzleServer = env.getProperty("KUZZLE_SERVER");
		}		
		WebSocketOptions opts = new WebSocketOptions();
	    opts.setAutoReconnect(true).setConnectionTimeout(42000);
	    Kuzzle kuzzle = null;
		try {
			WebSocket ws = new WebSocket(kuzzleServer, opts);
			kuzzle = new Kuzzle(ws);
			kuzzle.connect();
			log.info(String.format("Kuzzle Instance Connected to Server: %s", kuzzleServer));
		} catch (Exception e) {			
			log.error(String.format("Error while connecting to Kuzzle: %s\n", e.getMessage()));
			e.printStackTrace();
		}

		return kuzzle;
	}
}
