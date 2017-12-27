package org.wanna.jabbot.config;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.wanna.jabbot.binding.config.BindingConfiguration;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2016-03-03
 */
public class FileConfigurationDaoTest {
	private FileConfigurationDao dao;

	@Before
	public void before(){
		URL url = ClassLoader.getSystemClassLoader().getResource("jabbot.json");
		File file = new File(url.getFile());
		dao = new FileConfigurationDao(file);
	}

	@Test
	public void getBinding(){
		BindingConfiguration[] bindings = dao.getBindings();
		Assert.assertThat(bindings,not(nullValue()));
		Assert.assertThat(bindings.length,is(1));
	}

	@Test
	public void addBinding(){
		BindingConfiguration configuration = new BindingConfiguration();
		configuration.setUrl("junit-test");

		dao.addBinding(configuration);
	}
}
