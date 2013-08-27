package jersey;

@Grab('org.eclipse.jetty.aggregate:jetty-all-server:8.1.0.v20120127')
@Grab('com.sun.jersey:jersey-client')
@Grab('com.sun.jersey:jersey-core')
@Grab('com.sun.jersey:jersey-json')
@Grab('com.sun.jersey:jersey-server')
@Grab('com.sun.jersey:jersey-servlet')
@Grab('javax.ws.rs:jsr311-api')

import javax.ws.rs.*;
import com.sun.jersey.api.container.ContainerFactory
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import org.eclipse.jetty.*;
import org.eclipse.jetty.servlet.*
import org.eclipse.jetty.server.*
import org.eclipse.jetty.server.handler.*



class JersyServer {
    @Path("/")
    public static class TestResource {

        @GET
        public String get() {
            return "GET";
        }
    }

    @Path("/hello")
    public static class HelloWorldService {
 
        @GET
        @Path("{name}")
        def String hello(@PathParam("name") String name) {
            "Hello $name"
        }
 
    }
    
    static void main(args) {
        ServletHolder sh = new ServletHolder(ServletContainer.class);
        sh.setInitParameter("com.sun.jersey.config.property.resourceConfigClass", "com.sun.jersey.api.core.PackagesResourceConfig");
        sh.setInitParameter("com.sun.jersey.config.property.packages", "jersey");

        Server server = new Server(9999);

        ServletContextHandler jerseyHandler = new ServletContextHandler(ServletContextHandler.SESSIONS)

        jerseyHandler.with {
            classLoader = Thread.currentThread().getContextClassLoader()
            contextPath = '/'
            addServlet(sh, '/*')
        }
        server.setHandler(jerseyHandler)

        server.start();

        Client c = Client.create();
        WebResource r = c.resource("http://localhost:9999/hello/jamie");
        System.out.println(r.get(String.class));

        server.stop();

    }


}
