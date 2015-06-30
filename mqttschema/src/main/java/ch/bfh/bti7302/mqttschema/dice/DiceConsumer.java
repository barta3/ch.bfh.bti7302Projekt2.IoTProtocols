package ch.bfh.bti7302.mqttschema.dice;
import java.util.Scanner;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttMessageJson;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DiceConsumer {

//	public static final String BROKER_URL = "tcp://178.62.210.231:1883"; // Mosquitto Websockets
	public static final String BROKER_URL = "tcp://127.0.0.1:61613"; // Apollo
	
	static ObjectMapper mapper = new ObjectMapper(); // create once, reuse
	public static void main(String[] args) throws MqttException, InterruptedException {
	    new DiceConsumer().start();
	}
	private void start() throws MqttException, InterruptedException {
		MqttClient client = null;
		try {
			// Setup
			MqttConnectOptions connOpt = new MqttConnectOptions();
			connOpt.setCleanSession(true);
			connOpt.setKeepAliveInterval(30);
			connOpt.setUserName("admin");
			connOpt.setPassword("password".toCharArray());
			client = new MqttClient(BROKER_URL, "JavaClientDiceConsumer");

			client.connect(connOpt);
			
			client.setCallback(new Subscriber());
			client.subscribe("dice/state/get/#");
			
			boolean run = true;
			while (run) {
			    
			    Scanner scanner = new Scanner(System.in);
			    String input = scanner.nextLine();
			    
			    if("exit".equals(input)) {
			        run = false;
			    }
			    
			    String[] vals = input.split("\\s");
			    
			    MqttMessage msg = new MqttMessage(vals[1].getBytes());
			    client.publish("dice/state/" + vals[0] + "/set", msg);
			}
		
		} finally {
			System.out.println("Disconnect");
			client.disconnect();
		}
	}
	class Subscriber implements MqttCallback {

        public void connectionLost(Throwable cause) {
          System.out.println("connectionLost " + cause.getMessage());  
        }

        public void messageArrived(String topic, MqttMessage message) throws Exception {
            
            if(!message.isRetained()) {
//                return;
            }
            
            System.out.println("messageArrived " + topic + " : " + new String(message.getPayload()));
        }

        public void deliveryComplete(IMqttDeliveryToken token) {
//            System.out.println("deliveryComplete");
        }
	    
	}


}