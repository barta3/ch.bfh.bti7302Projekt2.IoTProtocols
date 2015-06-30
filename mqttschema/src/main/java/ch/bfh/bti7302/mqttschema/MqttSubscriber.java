package ch.bfh.bti7302.mqttschema;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttSubscriber implements MqttCallback {

//	public static final String BROKER_URL = "tcp://178.62.210.231:1883";
	public static final String BROKER_URL = "tcp://127.0.0.1:61613";
	
	public static void main(String[] args) throws MqttException {

		MqttSubscriber subs = new MqttSubscriber();
		subs.setUp();
	}

	private void setUp() throws MqttException {
		MqttConnectOptions connOpt = new MqttConnectOptions();
		connOpt.setCleanSession(true);
		connOpt.setKeepAliveInterval(30);
		connOpt.setUserName("admin");
		connOpt.setPassword("password".toCharArray());
		MqttClient client = new MqttClient(BROKER_URL, "JavaClientSubscriber");
		client.connect(connOpt);

		client.subscribe("abaertschi/#");
		client.setCallback(this);

	}

	public void connectionLost(Throwable cause) {
		System.out.println("Connection Lost " + cause);
	}

	public void messageArrived(String topic, MqttMessage message) throws Exception {
		System.out.println("Message arrived: " + topic + " - "	+ message.toString());
	}

	public void deliveryComplete(IMqttDeliveryToken token) {
		System.out.println("Delivery complete " + token.getMessageId());

	}
}