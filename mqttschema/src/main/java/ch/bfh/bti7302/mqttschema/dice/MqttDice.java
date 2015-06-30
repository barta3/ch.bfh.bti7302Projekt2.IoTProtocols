package ch.bfh.bti7302.mqttschema.dice;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttMessageJson;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MqttDice {

//	public static final String BROKER_URL = "tcp://178.62.210.231:1883"; // Mosquitto Websockets
	public static final String BROKER_URL = "tcp://127.0.0.1:61613"; // Apollo
	
	
	static ObjectMapper mapper = new ObjectMapper(); // create once, reuse
	
	private Dice dice = Dice.getInstance();
	MqttClient client = null;
	private boolean stateInvalid = false;
	
	public static void main(String[] args) throws MqttException, InterruptedException {
	    new MqttDice().start();
	}
	private void start() throws MqttException, InterruptedException {
		try {
			// Setup
			MqttConnectOptions connOpt = new MqttConnectOptions();
			connOpt.setCleanSession(true);
			connOpt.setKeepAliveInterval(30);
			connOpt.setUserName("admin");
			connOpt.setPassword("password".toCharArray());
			client = new MqttClient(BROKER_URL, "JavaClientDice");

			client.connect(connOpt);
			
			client.setCallback(new Subscriber());
			client.subscribe("dice/state/+/set");
			
			
			
			boolean run = true;
			while (run) {
		        MqttMessageJson<DiceEvent, DiceConfig> msg = new MqttMessageJson<DiceEvent, DiceConfig>();
		        
		        DiceEvent event = new DiceEvent();
		        event.setNumber(dice.generateNumber());
		        
		        msg.setPayload(event);
		        msg.setStatus(dice.getConfig());
		        msg.setRetained(false);
		        
		        try {
		            client.publishWithSchema("dice", msg, stateInvalid);
//		            client.publishStatus("dice" , msg);
		        } catch (MqttPersistenceException e) {
		            e.printStackTrace();
		        } catch (MqttException e) {
		            e.printStackTrace();
		        }				
				Thread.sleep(dice.getConfig().getInterval());
				System.out.println(dice.getConfig().getInterval());
			}
		
		} finally {
			System.out.println("Disconnect");
			client.disconnect();
		}
	}
	
	private void publishState() {
	    
	    MqttMessage message = new MqttMessage("state update".getBytes());
        try {
            client.publish("dice/state/get/", message);
        } catch (MqttPersistenceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MqttException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // doesn't work
//	    MqttMessageJson<DiceEvent, DiceConfig> msg = new MqttMessageJson<DiceEvent, DiceConfig>();
//	    msg.setPayload(new DiceEvent());
//	    msg.setStatus(dice.getConfig());
//        try {
//            client.publishStatus("dice" , msg);
//        } catch (MqttPersistenceException e) {
//            e.printStackTrace();
//        } catch (MqttException e) {
//            e.printStackTrace();
//        }
	}
	
	class Subscriber implements MqttCallback {

        public void connectionLost(Throwable cause) {
          System.out.println("connectionLost " + cause.getMessage());  
        }

        public void messageArrived(String topic, MqttMessage message) throws Exception {
            System.out.println("messageArrived " + topic + " : " + new String(message.getPayload()));

            if("dice/state/interval/set".equals(topic)) {
                int interval = Integer.parseInt(new String(message.getPayload()));
                System.out.println("Setting interval to " + interval);
                dice.getConfig().setInterval(interval);
                publishState();
            }
            
            if("dice/state/max/set".equals(topic)) {
                int max = Integer.parseInt(new String(message.getPayload()));
                dice.getConfig().setMax(max);
                publishState();
            }
            
        }

        public void deliveryComplete(IMqttDeliveryToken token) {
//            System.out.println("deliveryComplete");
        }
	    
	}


}