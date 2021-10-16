package org.example.helloworld.jetty;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.example.helloworld.HelloReply;
import org.example.helloworld.HelloRequest;
import org.example.helloworld.common.RequestProcessor;
import org.slf4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

public class JettyServer {
    private static final Logger logger = getLogger(JettyServer.class);

    public static class Handler extends HttpServlet {
        @Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            byte[] bytes = IOUtils.readFully(request.getInputStream(), request.getContentLength());
            HelloRequest helloRequest = HelloRequest.parseFrom(bytes);

            HelloReply reply = RequestProcessor.process(helloRequest);
            logger.info("sayHello replied");

            response.setContentType("application/octet-stream");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getOutputStream().write(reply.toByteArray());
        }
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8090);
        server.setConnectors(new Connector[]{connector});
        ServletContextHandler servletContextHandler = new ServletContextHandler(null, "/");

        ServletHolder servletHolder = new ServletHolder(new Handler());
        servletContextHandler.addServlet(servletHolder, "/");

        server.setHandler(servletContextHandler);
        server.start();
    }
}
