package org.wanna.jabbot.extension;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2016-06-01
 */
public class ExtensionLoader {
	private final Logger LOG = LoggerFactory.getLogger(ExtensionLoader.class);
	private static ExtensionLoader instance = new ExtensionLoader();

	private final Map<String, Class> registry;
	private final ObjectMapper mapper;

	public static ExtensionLoader getInstance(){
		return instance;
	}

	private ExtensionLoader(){
		registry = new HashMap<>();
		mapper = new ObjectMapper();
	}

	public void load(ExtensionPoint extension){
		List<String> classes = getPluginClass(extension.getConfigFile());
		if(classes == null){
			return;
		}
		ClassLoader classLoader = createPluginClassLoader(extension.getLibFolder());
		for (String className : classes) {
			LOG.debug("plugin class: {}",className);
			try {
				Class commandClass = classLoader.loadClass(className);
				registry.put(className,commandClass);
			} catch (ClassNotFoundException e) {
				LOG.error("error while trying to load {} from custom classloader",className);
			}
		}
	}

	public <T> T getExtension(String className, Class<T> target, Object... parameters){
		try {
			Class<T> clazz = getExtensionClass(className);
			if(clazz == null){
				LOG.warn("no extension found for {}",className);
				return null;
			}

			if(parameters != null && parameters.length > 0){
				Class[] paramType = new Class[parameters.length];
				for(int i = 0; i < parameters.length; i++){
					paramType[i] = parameters[i].getClass();
				}
				return clazz.getDeclaredConstructor(paramType).newInstance(parameters);
			}else{
				return clazz.newInstance();
			}
		} catch (Exception e) {
			LOG.error("failed to create extension for {}",className,e);
			return null;
		}
	}

	private Class getExtensionClass(String className){
		if(!registry.containsKey(className)){
			LOG.debug("no plugin found for {}, trying to resolve using jabbot classloader",className);
			try {
				return Class.forName(className, true, ClassLoader.getSystemClassLoader());
			} catch (ClassNotFoundException e) {
				return null;
			}
		}else {
			return registry.get(className);
		}
	}

	private ClassLoader createPluginClassLoader(File libFolder){
		List<URL> urls = new ArrayList<>();
		if(libFolder.exists() && libFolder.isDirectory()){
			File[] files = libFolder.listFiles();
			if(files == null){
				return null;
			}
			for (File file : files) {
				try {
					urls.add(file.toURI().toURL());
				} catch (MalformedURLException e) {
					LOG.warn("unable to add {} to classpath", file.getPath());
				}
			}
		}
		return new URLClassLoader(urls.toArray(new URL[urls.size()]));
	}

	private List<String> getPluginClass(File config) {
		List<String> classes = new ArrayList<>();
		if(config.exists() && config.isFile()){
			try {
				Map[] maps = mapper.readValue(config,Map[].class);
				for (Map map : maps) {
					classes.add((String)map.get("className"));
				}
				return classes;
			} catch (IOException e) {
				LOG.error("unable to extract classes from config for {}",config.getPath(),e);
			}
		}
		return null;
	}
}
