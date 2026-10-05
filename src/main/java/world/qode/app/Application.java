package world.qode.app;

import java.io.File;
import java.nio.file.Files;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * Plain Spring Framework: an embedded Tomcat with Spring MVC's DispatcherServlet
 * registered programmatically (no web.xml, no Spring Boot).
 */
public final class Application {

	public static void main(String[] args) throws Exception {
		int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));

		// The application context, configured from WebConfig; the DispatcherServlet
		// refreshes it on startup.
		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
		context.register(WebConfig.class);

		Tomcat tomcat = new Tomcat();
		File baseDir = Files.createTempDirectory("tomcat").toFile();
		baseDir.deleteOnExit();
		tomcat.setBaseDir(baseDir.getAbsolutePath());
		tomcat.setPort(port);
		tomcat.getConnector().setProperty("address", "0.0.0.0");

		Context root = tomcat.addContext("", baseDir.getAbsolutePath());
		Tomcat.addServlet(root, "dispatcher", new DispatcherServlet(context)).setLoadOnStartup(1);
		root.addServletMappingDecoded("/", "dispatcher");

		tomcat.start();
		System.out.println("listening on 0.0.0.0:" + port);
		tomcat.getServer().await();
	}

	private Application() {
	}

}
