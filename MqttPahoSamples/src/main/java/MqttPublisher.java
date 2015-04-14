import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttPublisher {

	public static final String BROKER_URL = "tcp://178.62.210.231:1883";
//	public static final String BROKER_URL = "tcp://127.0.0.1:61613";
	public static void main(String[] args) throws MqttException, InterruptedException {

		MqttClient client = null;
		try {
			client = new MqttClient(BROKER_URL, "JavaClient2");
			client.connect();
			MqttMessage msg = new MqttMessage();
			
			int msgs = 0;
			boolean run = true;
			while (run) {
				String data = Double.toString(Math.random());
				msg.setPayload(data.getBytes());
				client.publish("abaertschi", msg);
				
				System.out.println("Publishing " + data);
				
				Thread.sleep(1000);
				run = ++msgs < 3;
			}
		
		} finally {
			System.out.println("Disconnect");
			client.disconnect();
		}
	}
}