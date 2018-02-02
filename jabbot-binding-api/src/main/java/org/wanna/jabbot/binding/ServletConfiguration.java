package org.wanna.jabbot.binding;

public final class ServletConfiguration {
	private Class servletClass;
	private String servletPath;

	public ServletConfiguration(Class servletClass, String servletPath) {
		this.servletClass = servletClass;
		this.servletPath = servletPath;
	}

	public Class getServletClass() {
		return servletClass;
	}

	public String getServletPath() {
		return servletPath;
	}
}
