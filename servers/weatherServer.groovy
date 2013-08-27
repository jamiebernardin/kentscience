package jetty;

@Grab('org.eclipse.jetty.aggregate:jetty-all-server:8.1.0.v20120127')

import org.eclipse.jetty.*;
import org.eclipse.jetty.servlet.*
import org.eclipse.jetty.server.*
import org.eclipse.jetty.server.handler.*
import groovy.servlet.GroovyServlet

Server server = new Server(5050);

ResourceHandler resourceHandler = new ResourceHandler()
resourceHandler.with {
    contextPath = '/'
    welcomeFiles = ["index.html"] as String[]
    resourceBase = "weather"
}

ServletContextHandler groovletHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS)
groovletHandler.with {
    contextPath = '/'
    resourceBase = 'weather/groovy'
    addServlet(GroovyServlet, '*.groovy')
}
 
HandlerList handlers = new HandlerList();
handlers.setHandlers([resourceHandler, groovletHandler, new DefaultHandler()] as Handler[]);
server.setHandler(handlers);
 
server.start();
server.join();

