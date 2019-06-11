package io.quarkus.azure.functions.resteasy.runtime;

import java.util.Optional;

import javax.ws.rs.ext.RuntimeDelegate;

import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.specimpl.ResteasyUriInfo;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import io.quarkus.runtime.Application;

public class Function {

    public HttpResponseMessage run(
            @HttpTrigger(name = "req") HttpRequestMessage<Optional<byte[]>> request,
            final ExecutionContext context) {
        if (request.getUri().getPath().endsWith("quarkus-status")) {
            HttpResponseMessage.Builder responseBuilder = request
                    .createResponseBuilder(HttpStatus.valueOf(200)).body(deploymentStatus.getBytes());
            return responseBuilder.build();
        }
        return resteasyDispatch(request);
    }

    static String deploymentStatus = "ok";

    /*
     * static {
     * String[] args = {};
     * try {
     * Quarkus.main(args);
     * } catch (Exception e) {
     * e.printStackTrace();
     * deploymentStatus = e.getMessage();
     * }
     * }
     */

    static {
        String[] args = {};
        try {
            Class appClass = Class.forName("io.quarkus.runner.ApplicationImpl1");
            deploymentStatus = "Found App class";
            Application app = (Application) appClass.newInstance();
            app.start(args);
            deploymentStatus = "STARTED";
        } catch (ClassNotFoundException e) {
            deploymentStatus = "App class not found";
        } catch (InstantiationException e) {
            deploymentStatus = "instantiation exceptoin";
        } catch (IllegalAccessException e) {
            deploymentStatus = "illegal access";
        } catch (Exception ex) {
            deploymentStatus = "Unknown exception: " + ex.getMessage();
        }
    }

    protected HttpResponseMessage resteasyDispatch(HttpRequestMessage<Optional<byte[]>> request) {
        MockHttpResponse httpResponse = new MockHttpResponse();
        ResteasyUriInfo uriInfo = new ResteasyUriInfo(request.getUri().toString(), request.getUri().getRawQuery(), "/api");
        AzureHttpRequest httpRequest = new AzureHttpRequest(uriInfo,
                (SynchronousDispatcher) AzureFunctionsResteasyTemplate.deployment.getDispatcher(),
                httpResponse, request);
        AzureFunctionsResteasyTemplate.deployment.getDispatcher().invoke(httpRequest, httpResponse);
        HttpResponseMessage.Builder responseBuilder = request
                .createResponseBuilder(HttpStatus.valueOf(httpResponse.getStatus()));
        httpResponse.getOutputHeaders().forEach((name, values) -> {
            values.forEach(o -> {
                RuntimeDelegate.HeaderDelegate delegate = AzureFunctionsResteasyTemplate.deployment.getProviderFactory()
                        .getHeaderDelegate(o.getClass());
                if (delegate != null) {
                    responseBuilder.header(name, delegate.toString(o));
                } else {
                    responseBuilder.header(name, o.toString());
                }
            });
        });
        responseBuilder.body(httpResponse.getOutput());
        return responseBuilder.build();
    }
}
