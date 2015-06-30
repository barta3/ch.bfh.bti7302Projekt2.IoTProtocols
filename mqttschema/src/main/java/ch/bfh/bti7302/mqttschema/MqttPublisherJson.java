package ch.bfh.bti7302.mqttschema;
import java.util.Date;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttMessageJson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;

public class MqttPublisherJson {

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
			
			boolean run = true;
			while (run) {
				MqttMessageJson<Data, Data> msg = new MqttMessageJson<Data, Data>();
				Data data = getData();
				msg.setPayload(data);
				msg.setRetained(false);
				
				client.publishWithSchema("abaertschi", msg, true);
				System.out.println("Publishing " + data);
				
				Thread.sleep(1000);
			}
		
		} finally {
			System.out.println("Disconnect");
			client.disconnect();
		}
	}
	
	private static Data getData() {
		Data d = new Data();
		d.setId("MySensor");
		d.setValue((int) (Math.random() * 10));
		d.setTimestamp(new Date());
		
		return d;
//		try {
//			return mapper.writeValueAsString(d);
//		} catch (JsonProcessingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return "ERROR";
		
//		GsonBuilder builder = new GsonBuilder();
//		Gson gson = builder.create();
//		String json = gson.toJson(d);
//		return json;

		
		
	}
		
	private static String getSchema() {
//		return "{\"$schema\":\"http://json-schema.org/draft-04/schema#\",\"id\":\"http://jsonschema.net\",\"type\":\"object\",\"properties\":{\"id\":{\"id\":\"http://jsonschema.net/id\",\"type\":\"string\"},\"value\":{\"id\":\"http://jsonschema.net/value\",\"type\":\"integer\"},\"timestamp\":{\"id\":\"http://jsonschema.net/timestamp\",\"type\":\"string\"}},\"required\":[\"id\",\"value\",\"timestamp\"]}";
		
		SchemaFactoryWrapper visitor = new SchemaFactoryWrapper();
		String schema = "ERROR";
		try {
			mapper.acceptJsonFormatVisitor(mapper.constructType(Data.class), visitor);
			com.fasterxml.jackson.module.jsonSchema.JsonSchema jsonSchema = visitor.finalSchema();
			schema =  mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonSchema);
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
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