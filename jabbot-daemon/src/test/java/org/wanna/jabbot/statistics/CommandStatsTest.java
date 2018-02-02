package org.wanna.jabbot.statistics;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;


public class CommandStatsTest {
	private CommandStats stat;

	@Before
	public void before(){
		stat = new CommandStats("test");
	}

	@Test
	public void initializatonTest(){
		CommandStats stat = new CommandStats("test");
		Assert.assertThat(stat.getInvocationCount(),is(0L));
		Assert.assertThat(stat.getInvokers().isEmpty(),is(true));
	}

	@Test
	public void incrementSingleInvoker(){
		//Incrementing it should create invoker entry
		stat.increment("user");
		Assert.assertThat(stat.getInvocationCount(),is(1L));
		Assert.assertThat(stat.getInvokers().containsKey("user"),is(true));
		Assert.assertThat(stat.getInvokers().get("user").getInvocationCount(),is(1L));
		//Incrementing same user should bump both counter
		stat.increment("user");
		Assert.assertThat(stat.getInvocationCount(),is(2L));
		Assert.assertThat(stat.getInvokers().get("user").getInvocationCount(),is(2L));
	}

	@Test
	public void incrementMultipleInvokers(){
		//Incrementing it should create invoker entry
		stat.increment("user");
		Assert.assertThat(stat.getInvocationCount(),is(1L));
		Assert.assertThat(stat.getInvokers().containsKey("user"),is(true));
		Assert.assertThat(stat.getInvokers().get("user").getInvocationCount(),is(1L));
		//Incrementing second user should bump global counter and create new entry
		stat.increment("user2");
		Assert.assertThat(stat.getInvocationCount(),is(2L));
		Assert.assertThat(stat.getInvokers().get("user").getInvocationCount(),is(1L));
		Assert.assertThat(stat.getInvokers().get("user2").getInvocationCount(),is(1L));
	}
}
