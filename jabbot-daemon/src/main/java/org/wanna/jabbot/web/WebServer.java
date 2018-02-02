package org.wanna.jabbot.web;

import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.security.UserStore;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.security.Credential;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.Binding;
import org.wanna.jabbot.binding.ServletConfiguration;
import org.wanna.jabbot.web.config.User;
import org.wanna.jabbot.web.config.WebServerConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebServer{
	private final static Logger logger = LoggerFactory.getLogger(WebServer.class);
	private Server server;
	private static WebServer instance;
	private Map<String,ServletContextHandler> handlers = new HashMap<>();

	public static WebServer initialize(final WebServerConfig config){
		instance = new WebServer(config);
		return instance;
	}

	public static WebServer getInstance(){
		return instance;
	}

	private WebServer(final WebServerConfig configuration){
		server = new Server(configuration.getPort());
		ResourceConfig config = new ResourceConfig()
				.property("com.sun.jersey.api.json.POJOMappingFeature",true)
				.packages("org.wanna.jabbot.web.services")
				.register(RolesAllowedDynamicFeature.class);

		ServletHolder servlet = new ServletHolder(new ServletContainer(config));
		ServletContextHandler handler = register(servlet,"/rest","/*");
		HashLoginService loginService = new HashLoginService();
		server.addBean(loginService);
		//final UserStore userStore = buildUserStore(configuration.getUsers());
		//loginService.setUserStore(userStore);
		handler.setSecurityHandler(basicAuth(configuration.getUsers(),"jabbot rest service"));
	}

	public ServletContextHandler register(Binding binding, ServletConfiguration servletConfiguration){
		return register(
				new ServletHolder(servletConfiguration.getServletClass()),
				binding.getIdentifier(),
				servletConfiguration.getServletPath()
		);
	}

	private ServletContextHandler register(ServletHolder servlet, String context, String path){
		ServletContextHandler handler = handlers.getOrDefault(context,new ServletContextHandler(server,context));
		handler.addServlet(servlet,path);
		handlers.putIfAbsent(context,handler);

		return handler;
	}

	public void start(){
		try {
			server.start();
		} catch (Exception e) {
			logger.error("unable to start jabbot embedded web server",e);
		}
	}

	public void stop(){
		try {
			server.stop();
		} catch (Exception e) {
		}finally {
			server.destroy();
		}
	}

	private UserStore buildUserStore(List<User> users){
		UserStore store = new UserStore();
		for (User user : users) {
			store.addUser(user.getUsername(),Credential.getCredential(user.getPassword()),user.getRoles());
		}
		return store;
	}

	private SecurityHandler basicAuth(List<User> users, String realm) {

		HashLoginService l = new HashLoginService();
		l.setUserStore(buildUserStore(users));
		l.setName(realm);

		Constraint constraint = new Constraint();
		constraint.setName(Constraint.__BASIC_AUTH);
		constraint.setRoles(new String[]{"user","admin"});
		//constraint.setAuthenticate(true);

		ConstraintMapping cm = new ConstraintMapping();
		cm.setConstraint(constraint);
		cm.setPathSpec("/*");

		ConstraintSecurityHandler csh = new ConstraintSecurityHandler();
		csh.setAuthenticator(new BasicAuthenticator());
		csh.setRealmName("jabbot rest service");
		csh.addConstraintMapping(cm);
		csh.setLoginService(l);

		return csh;
	}
}
