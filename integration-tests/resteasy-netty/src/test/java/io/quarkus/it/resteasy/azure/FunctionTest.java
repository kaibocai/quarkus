package io.quarkus.it.resteasy.azure;

import java.net.URI;
import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;

import io.quarkus.azure.functions.resteasy.runtime.Function;
import io.quarkus.test.junit.QuarkusTest;

/**
 * Unit test for Function class.
 */
@QuarkusTest
public class FunctionTest {

    @Test
    public void testAzureFunctionGet() throws Exception {
        // Setup
        @SuppressWarnings("unchecked")
        final HttpRequestMessageMock req = new HttpRequestMessageMock();
        req.setUri(URI.create("https://foo.com/hello?name=Bill"));
        req.setHttpMethod(HttpMethod.GET);

        // Invoke
        final HttpResponseMessage ret = new Function().run(req, new ExecutionContext() {
            @Override
            public Logger getLogger() {
                return null;
            }

            @Override
            public String getInvocationId() {
                return null;
            }

            @Override
            public String getFunctionName() {
                return null;
            }
        });

        // Verify
        Assertions.assertEquals(ret.getStatus(), HttpStatus.OK);
        Assertions.assertEquals("hello world", new String((byte[]) ret.getBody()));
        String contentType = ret.getHeader("Content-Type");
        Assertions.assertNotNull(contentType);
        Assertions.assertTrue(MediaType.valueOf(contentType).isCompatible(MediaType.TEXT_PLAIN_TYPE));
    }

    @Test
    public void testAzureFunctionPost() throws Exception {
        // Setup
        @SuppressWarnings("unchecked")
        final HttpRequestMessageMock req = new HttpRequestMessageMock();
        req.setUri(URI.create("https://foo.com/hello"));
        req.setHttpMethod(HttpMethod.POST);
        req.setBody("Bill".getBytes());
        req.getHeaders().put("Content-Type", "text/plain");

        // Invoke
        final HttpResponseMessage ret = new Function().run(req, new ExecutionContext() {
            @Override
            public Logger getLogger() {
                return null;
            }

            @Override
            public String getInvocationId() {
                return null;
            }

            @Override
            public String getFunctionName() {
                return null;
            }
        });

        // Verify
        Assertions.assertEquals(ret.getStatus(), HttpStatus.OK);
        Assertions.assertEquals("Hello Bill", new String((byte[]) ret.getBody()));
        String contentType = ret.getHeader("Content-Type");
        Assertions.assertNotNull(contentType);
        Assertions.assertTrue(MediaType.valueOf(contentType).isCompatible(MediaType.TEXT_PLAIN_TYPE));
    }

}
