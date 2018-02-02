package org.wanna.jabbot.statistics;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CommandStats {
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private String name;
	private long invocationCount;
	private Date lastUsed;
	private ConcurrentHashMap<String,CommandInvoker> invokers;

	CommandStats(String name) {
		this.name = name;
		invokers = new ConcurrentHashMap<>();
	}

	public long getInvocationCount() {
		return invocationCount;
	}

	public String getName() {
		return name;
	}

	public String getLastUsed() {
		if(lastUsed == null )  return null;
		return sdf.format(this.lastUsed);
	}

	public void increment(String invokerName){
		this.invocationCount++;
		this.lastUsed = new Date();
		CommandInvoker local = invokers.putIfAbsent(invokerName,new CommandInvoker());
		if(local != null ) local.increment();
	}

	public Map<String, CommandInvoker> getInvokers() {
		return Collections.unmodifiableMap(invokers);
	}

	protected class CommandInvoker{

		private Date lastUsed;
		private long invocationCount;

		public CommandInvoker() {
			invocationCount = 1L;
			lastUsed = new Date();
		}

		public long getInvocationCount() {
			return invocationCount;
		}

		public String getLastUsed() {
			return sdf.format(lastUsed);
		}

		public void increment(){
			invocationCount++;
			lastUsed = new Date();
		}
	}
}
