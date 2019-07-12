package com.axway;


import iaik.security.provider.IAIK;
import org.junit.BeforeClass;
import org.junit.Test;


import com.vordel.api.nm.NodeManagerAPIException;
import com.vordel.ops.TestClientResponse;
import com.vordel.ops.TrafficMonitorClient;

import javax.ws.rs.core.Response;
import java.security.Security;

public class TestEchoService extends TestClientResponse {

	private static TrafficMonitorClient client;
	private static final String SERVER_ID = "instance-1";

	
	@BeforeClass
	public static void setup() throws NodeManagerAPIException {


		//Security.addProvider(new IAIK());
		client = new TrafficMonitorClient("https", "10.129.60.57", "8090", SERVER_ID, "admin", "changeme");
		
	}
	
	@Test
	public void testEchoService(){
		// Execute health check policy
		Response response = get("http://10.129.60.57:8080/healthcheck");
		assertStatusCode(response, 200);

		// Get its correlation id.
		String correlationId = getCorrelationId(response);
		// check HTTP header
		assertContainsHeader(response, "Host");

		// check HTTP header and value
		assertContainsHeaderWithValue(response, "Host", "10.129.60.57:8080");
		// Check that Set Message and Reflect Filters pass.
		client.assertFilterPassed(correlationId, "Set Message", "Reflect");

		// Ensure fault handlers did not fire.
		client.assertFilterOfTypeDidNotExecute(correlationId, "GenericError");
		client.assertFilterOfTypeDidNotExecute(correlationId, "JSONError");
		client.assertFilterOfTypeDidNotExecute(correlationId, "SOAPFault");

		client.assertNFiltersPassed(correlationId, 2);
		client.assertNFiltersFailed(correlationId, 0);

	}
	
	

}
