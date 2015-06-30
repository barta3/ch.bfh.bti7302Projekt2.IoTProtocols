package ch.bfh.iot.smoje.raspi.server;

import java.util.concurrent.Executors;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.config.NetworkConfig;

import ch.bfh.iot.smoje.raspi.resources.PostResource;
import ch.bfh.iot.smoje.raspi.resources.TempJsonResource;

/**
 * Example Server
 */
public class RaspiCaliforniumServer {
	
	public static void main(String[] args) throws Exception {
	    
		CoapServer server = new CoapServer(NetworkConfig.getStandard());
		server.setExecutor(Executors.newScheduledThreadPool(4));
//		server.add(new RaspiCamResource("raspicam"));
//		server.add(new JsonResource("json"));
		server.add(new TempJsonResource("temperature"));
		server.add(new PostResource("postit"));
		
		server.start();
	}
}
