package org.wanna.jabbot.statistics;

import org.wanna.jabbot.binding.Binding;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StatisticsManager {
	private static Map<Binding,StatisticsManager> instances = new HashMap<>();
	private Binding binding;
	private ConcurrentHashMap<String,CommandStats> commandStats;

	public static StatisticsManager getInstance(Binding binding){
		if(instances.containsKey(binding)){
			return instances.get(binding);
		}else{
			StatisticsManager manager = new StatisticsManager(binding);
			instances.put(binding,manager);
			return manager;
		}
	}


	private StatisticsManager(Binding binding){
		this.binding = binding;
		this.commandStats = new ConcurrentHashMap<>();
	}

	public void registerCommandStats(String commandName){
		commandStats.putIfAbsent(commandName,new CommandStats(commandName));
	}

	public void removeCommandStats(String commandName){
		commandStats.remove(commandName);
	}

	public CommandStats getCommandStats(String commandName){
		return commandStats.get(commandName);
	}

	public Binding getBinding() {
		return binding;
	}

	public Collection<CommandStats> getStats(){
		return Collections.unmodifiableCollection(commandStats.values());
	}
}
