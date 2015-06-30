import org.eclipse.paho.client.mqttv3.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class ErfaMqttPublisherServer implements MqttCallback {

	MqttClient erfaMqttClient;
	MqttConnectOptions connOpt;

//	final static String BROKER_URL = "tcp://iot.eclipse.org:1883";
	//final static String BROKER_URL = "tcp://localhost:1883";
	//final static String BROKER_URL = "tcp://178.62.210.231:1883";
	public static final String BROKER_URL = "tcp://127.0.0.1:61613";

	static final String RASPI_TEMP_THING = "abaertschi";

	static final int QOS_ZERO_OR_ONE_DELIVERY = 0;
	static final int QOS_AT_LEAST_ONE_DELIVERY = 1;
	static final int QOS_EXACTLY_ONE_DELIVERY = 2;

	/**
	 * Startet einen UDP Server auf 9001
	 * 
	 * @throws IOException
	 * 
	 */
	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
		ErfaMqttPublisherServer smc = new ErfaMqttPublisherServer();
		smc.setupMqttClient();
		//DatagramSocket socket = new DatagramSocket(9090);

		System.out.println("server gestartet jipipiiii");

		while (true) {
			DatagramPacket packet = new DatagramPacket(new byte[100], 100);

			packet.setAddress(InetAddress.getLocalHost());
			packet.setPort(9090);

//			Scanner in = new Scanner(System.in);
//			String data = in.next();
			
			Thread.sleep(1000);
			
			String data = Double.toString(Math.random());
			
			packet.setData(data.getBytes());

			// wenn von javame midlet eine udp nachricht erhalten wurde...
			smc.publishTemp(new String(packet.getData()));
			// smc.publishTemp(data);

			System.out.println(packet.getAddress() + " " + packet.getPort()	+ ": " + new String(packet.getData()));

			// Return the packet to the sender
			//socket.send(packet);
		}

	}

	/**
	 * 
	 * publish: Temperatur auf cloud mosquitto via mqtt
	 * 
	 */
	public void publishTemp(String temp) throws Exception {

		MqttTopic topic = erfaMqttClient.getTopic(RASPI_TEMP_THING);

		MqttMessage message = new MqttMessage(temp.getBytes());
		message.setQos(QOS_ZERO_OR_ONE_DELIVERY);
		message.setRetained(false); // zurückbehalten

		System.out.println("Publishing topic:" + topic + " temp:" + temp);

		MqttDeliveryToken token = topic.publish(message);

		token.waitForCompletion();
		Thread.sleep(100);

	}

	private void setupMqttClient() throws Exception {
		// setup MQTT Client
		connOpt = new MqttConnectOptions();
		connOpt.setCleanSession(true);
		connOpt.setKeepAliveInterval(30);
		connOpt.setUserName("admin");
		connOpt.setPassword(new char[]{'p','a','s','s','w','o','r','d'});

		erfaMqttClient = new MqttClient(BROKER_URL, "JavaClient");
		erfaMqttClient.setCallback(this);
		erfaMqttClient.connect(connOpt);

		System.out.println("Connected to " + BROKER_URL);
	}

	/**
	 * 
	 * connectionLost: callback wenn die Verbindung verloren wurde...
	 * 
	 */
	public void connectionLost(Throwable t) {
		System.out.println("Connection lost!");
		// man koennte sich hier neu verbinden wenn man freude daran hätte:)
	}

	/**
	 * 
	 * deliveryComplete: wird aufgerufen wenn nachricht auf server published
	 * wurde...
	 * 
	 */
	public void deliveryComplete(IMqttDeliveryToken token) {
		try {
			System.out.println("deliveryComplete " + new String(token.getMessage().getPayload()));
		} catch (Exception e) {
			// wird sicher nie auftreten.
		}
	}

	public void messageArrived(String arg0, MqttMessage arg1) throws Exception {
		// sollte in diesem fall nie aufgerufen werde da dieses programm nur
		// nachrichten
		// published und nirgends subscriber ist...

	}
}