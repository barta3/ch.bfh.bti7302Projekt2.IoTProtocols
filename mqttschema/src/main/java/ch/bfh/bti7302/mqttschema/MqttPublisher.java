package ch.bfh.bti7302.mqttschema;
import java.util.Date;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;

public class MqttPublisher {

//	public static final String BROKER_URL = "tcp://178.62.210.231:1883"; // Mosquitto Websockets
	public static final String BROKER_URL = "tcp://127.0.0.1:61613"; // apollo
	
	static ObjectMapper mapper = new ObjectMapper(); // create once, reuse
	public static void main(String[] args) throws MqttException, InterruptedException {

		MqttClient client = null;
		try {
			// Setup
			MqttConnectOptions connOpt = new MqttConnectOptions();
			connOpt.setCleanSession(true);
			connOpt.setKeepAliveInterval(30);
			connOpt.setUserName("admin");
			connOpt.setPassword("password".toCharArray());
			client = new MqttClient(BROKER_URL, "JavaClientPushlisher");
			client.connect(connOpt);
			
			int schemaCnt = 0;
			
			
			boolean run = true;
			while (run) {
				MqttMessage msg = new MqttMessage();
				String data = getData();
				msg.setPayload(data.getBytes());
				msg.setRetained(false);
				
				if(schemaCnt++ % 5 == 0)  {
					MqttMessage schema = new MqttMessage(getSchema().getBytes());
					schema.setRetained(true);
					client.publish("abaertschi/schema", schema);
					System.out.println("Publishing Schema" + schema);
				}
			

				client.publish("abaertschi", msg);
				System.out.println("Publishing " + data);
				
				Thread.sleep(1000);
			}
		
		} finally {
			System.out.println("Disconnect");
			client.disconnect();
		}
	}
	
	private static String getData() {
		Data d = new Data();
		d.setId("MySensor");
		d.setValue((int) (Math.random() * 10));
		d.setTimestamp(new Date());
		
		try {
			return mapper.writeValueAsString(d);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return "ERROR";
	}
		
	private static String getSchema() {
		
		SchemaFactoryWrapper visitor = new SchemaFactoryWrapper();
		String schema = "ERROR";
		try {
			mapper.acceptJsonFormatVisitor(mapper.constructType(Data.class), visitor);
			com.fasterxml.jackson.module.jsonSchema.JsonSchema jsonSchema = visitor.finalSchema();
			schema =  mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonSchema);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return schema;
	}
	
//	private static  void validateSchema() {
//		try {
//			JsonNode schemaNode = JsonLoader.fromString(getSchema());
//			JsonNode dataNode = JsonLoader.fromString(getData());
//			JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
//			JsonSchema schema = factory.getJsonSchema(schemaNode);
//			
//			ProcessingReport report = schema.validate(dataNode);
//			System.out.println(report);
//			
//			
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (ProcessingException e) {
//			e.printStackTrace();
//		}
//	}
	
	
}