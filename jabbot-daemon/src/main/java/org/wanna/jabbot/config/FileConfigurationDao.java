package org.wanna.jabbot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.config.BindingConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;

import java.util.HashSet;
import java.util.UUID;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2016-03-03
 */
public class FileConfigurationDao implements ConfigurationDao{
	private final Logger logger = LoggerFactory.getLogger(FileConfigurationDao.class);
	private File resource;
	private ObjectMapper mapper = new ObjectMapper();


	public FileConfigurationDao(File configFile){
		this.resource = configFile;
		this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
		this.mapper.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
	}

	public JabbotConfiguration getConfiguration(){
		try {
			return mapper.readValue(resource,JabbotConfiguration.class);
		} catch (IOException e) {
			return null;
		}
	}

	public JabbotConfiguration saveConfiguration(JabbotConfiguration configuration){
		try {
			write(configuration);
			return configuration;
		} catch (IOException e) {
			return null;
		}
	}

	public BindingConfiguration[] getBindings(){
		JabbotConfiguration jabbotConfiguration;
		try {
			jabbotConfiguration = mapper.readValue(resource,JabbotConfiguration.class);
		} catch (IOException e) {
			return null;
		}
		if(jabbotConfiguration == null || jabbotConfiguration.getServerList() == null){
			return null;
		}

		return jabbotConfiguration.getServerList().toArray(new BindingConfiguration[jabbotConfiguration.getServerList().size()]);
	}

	public BindingConfiguration getBinding(String identifier){
		JabbotConfiguration jabbotConfiguration;
		try {
			jabbotConfiguration = mapper.readValue(resource,JabbotConfiguration.class);
		} catch (IOException e) {
			return null;
		}

		if(jabbotConfiguration == null || jabbotConfiguration.getServerList() == null){
			return null;
		}

		for (BindingConfiguration bindingConfiguration : jabbotConfiguration.getServerList()) {
			if(bindingConfiguration.getId().equals(identifier)){
				return bindingConfiguration;
			}
		}
		return null;
	}

	public BindingConfiguration addBinding(BindingConfiguration configuration){
		JabbotConfiguration jabbotConfiguration;
		try {
			jabbotConfiguration = mapper.readValue(resource,JabbotConfiguration.class);
		} catch (IOException e) {
			logger.warn("unable to read source config, creating a new one");
			jabbotConfiguration = new JabbotConfiguration();
			//logger.error("failed to add binding %",configuration,e);
		}

		if(configuration.getId() == null){
			logger.debug("generating a new binding identifier");
			//TODO generate a more human readable name here
			String identifier = UUID.randomUUID().toString();
			configuration.setId(identifier);
		}

		if(jabbotConfiguration.getServerList() == null){
			jabbotConfiguration.setServerList(new HashSet<BindingConfiguration>());
		}else{
			for (BindingConfiguration bindingConfiguration : jabbotConfiguration.getServerList()) {
				if(bindingConfiguration.equals(configuration)){
					logger.info("a similar binding already exists");
					//TODO throw an error here
				}
			}
		}

		jabbotConfiguration.getServerList().add(configuration);
		try {
			write(jabbotConfiguration);
		} catch (IOException e) {
			logger.error("failed to add binding %",configuration,e);
		}
		return configuration;
	}

	private void write(JabbotConfiguration jabbotConfiguration) throws IOException{
		FileOutputStream fos = new FileOutputStream(resource);
		FileLock lock = null;
		try {
			lock = fos.getChannel().lock();
			mapper.writeValue(fos,jabbotConfiguration);
		}finally {
			try{
				if(lock != null && lock.isValid() ) { lock.release(); };
			}catch (IOException e){
				logger.error("failed to release the lock",e);
			}
			try{
				fos.close();
			}catch (IOException e){
				logger.error("failed to write configuration file",e);
			}

		}
	}
}
