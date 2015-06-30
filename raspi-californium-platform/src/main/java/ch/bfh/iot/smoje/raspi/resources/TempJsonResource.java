package ch.bfh.iot.smoje.raspi.resources;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.ResourceAttributes;

public class TempJsonResource extends CoapResource {

    public TempJsonResource(String resourceIdentifier) {
        super(resourceIdentifier);

        ResourceAttributes attributes = getAttributes();
        attributes.setTitle("Temperatur Bern");
        attributes.addResourceType("Temperature");
        attributes.addContentType(MediaTypeRegistry.APPLICATION_JSON);
        attributes.addInterfaceDescription("Hier steht viel Text...");
         attributes.setMaximumSizeEstimate(256 );
    }

    @Override
    public void handleGET(CoapExchange exchange) {
        Integer ct = MediaTypeRegistry.APPLICATION_JSON;
        if (exchange.getRequestOptions().hasAccept()) {
            ct = exchange.getRequestOptions().getAccept();
        }

        Measurement m = new Measurement();
        m.setUnit("° Celsius");
        m.setValue(25.3);

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = null;
        try {
            json = ow.writeValueAsString(m);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // create response
        Response response = new Response(ResponseCode.CONTENT);
        response.setPayload(json);
        response.getOptions().setContentFormat(ct);

        exchange.respond(response);

    }

}

class Measurement {
    private String unit;
    private double value;

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double d) {
        this.value = d;
    }
}