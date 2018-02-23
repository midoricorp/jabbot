package org.wanna.jabbot.statistics;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

public class StatisticsManager {
	private ConcurrentHashMap<String,CommandStats> commandStats;
	public StatisticsManager(){
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

	public Collection<CommandStats> getStats(){
		return Collections.unmodifiableCollection(commandStats.values());
	}
}
