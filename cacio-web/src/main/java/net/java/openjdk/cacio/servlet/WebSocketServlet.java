package net.java.openjdk.cacio.servlet;

import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class WebSocketServlet extends org.eclipse.jetty.websocket.servlet.WebSocketServlet {

    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.register(WebSocketStreamThread.class);
    }
}
