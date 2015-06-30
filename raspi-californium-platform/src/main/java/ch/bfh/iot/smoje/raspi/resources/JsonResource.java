/*******************************************************************************
 * Copyright (c) 2014 Institute for Pervasive Computing, ETH Zurich and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 * 
 * The Eclipse Public License is available at
 *    http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 *    http://www.eclipse.org/org/documents/edl-v10.html.
 * 
 * Contributors:
 *    Matthias Kovatsch - creator and main architect
 ******************************************************************************/
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


/**
 * Get a Image from the RaspiCam
 */
public class JsonResource extends CoapResource {
	

	public JsonResource(String resourceIdentifier) {
		super(resourceIdentifier);
		
		ResourceAttributes attributes = getAttributes();
		attributes.setTitle("JSON Objects");
		attributes.addResourceType("Person");
		attributes.addContentType(MediaTypeRegistry.APPLICATION_JSON);
		attributes.addInterfaceDescription("Hier steht viel erklärender Text...");
//		attributes.setMaximumSizeEstimate(18029);
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		Integer ct = MediaTypeRegistry.APPLICATION_JSON;
		if (exchange.getRequestOptions().hasAccept()) {
			ct = exchange.getRequestOptions().getAccept();
		}
		
		Person p = new Person();
		p.setAge(44);
		p.setName("Max Muster");
        
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json = null;
            try {
                json = ow.writeValueAsString(p);
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
class Person {
    private String name;
    private int age;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }
}