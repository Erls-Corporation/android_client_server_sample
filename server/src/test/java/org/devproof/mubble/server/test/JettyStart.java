package org.devproof.mubble.server.test;

import org.devproof.mubble.server.jndi.JndiConfiguration;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.plus.naming.Resource;
import org.mortbay.jetty.webapp.WebAppContext;


/**
 * Test jetty start class for development
 *
 * @author Carsten Hufe
 */
public class JettyStart {

    public static void main(final String[] args) throws Exception {
        int port = 8080;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        System.setProperty("catalina.base", ".");

        new Resource(JndiConfiguration.MONGODB_HOST, "flame.mongohq.com");
        new Resource(JndiConfiguration.MONGODB_PORT, 27035);
        new Resource(JndiConfiguration.MONGODB_DBNAME, "testdata");
        new Resource(JndiConfiguration.MONGODB_DBUSER, "devproof");
        new Resource(JndiConfiguration.MONGODB_DBPASSWORD, "<yourpassword>");

        Server server = new Server(port);
        WebAppContext context = new WebAppContext();
        context.setContextPath("/");
        context.setResourceBase("src/main/java/");
        context.setDescriptor("src/main/webapp/WEB-INF/web.xml");
        context.setParentLoaderPriority(true);
        server.setHandler(context);

        try {
            System.out.println(">>> STARTING MUBBLE SERVER, PRESS ANY KEY TO STOP");
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(100);
        }
    }
}
