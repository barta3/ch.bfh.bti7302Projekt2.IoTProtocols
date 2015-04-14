import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttSubscriber implements MqttCallback{

	public static final String BROKER_URL = "tcp://178.62.210.231:1883";
//	public static final String BROKER_URL = "tcp://127.0.0.1:61613";
	public static void main(String[] args) throws MqttException {

		MqttSubscriber subs = new MqttSubscriber();
		subs.setUp();
	}
private void setUp() throws MqttException {
		MqttClient client = null;
		client = new MqttClient(BROKER_URL, "JavaClient2");
		client.connect();
		
		client.subscribe("abaertschi");
		client.setCallback(this);
	
}
@Override
public void connectionLost(Throwable cause) {
	// TODO Auto-generated method stub
	System.out.println("Connection Lost " + cause);
}
@Override
public void messageArrived(String topic, MqttMessage message) throws Exception {
	// TODO Auto-generated method stub
	System.out.println("Message arrived: " + topic + " - " + message.toString());
	
}
@Override
public void deliveryComplete(IMqttDeliveryToken token) {
	// TODO Auto-generated method stub
	System.out.println("Delivery complete " + token.getMessageId());
	
}
}