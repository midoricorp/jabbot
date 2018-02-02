package org.wanna.jabbot.web.services;

import org.wanna.jabbot.BindingManager;
import org.wanna.jabbot.statistics.CommandStats;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

@Path("stats")
@Produces(MediaType.APPLICATION_JSON)
public class StatisticsService {
	@GET
	@Path("/{binding}/commands")
	public Response commandsUsage(@PathParam("binding") String bindingIdentifier){
		BindingManager manager  = BindingManager.getInstance(bindingIdentifier);
		if(manager == null){
			return Response.status(Response.Status.NOT_FOUND).build();
		}else{
			Collection<CommandStats> stats = manager.getStatisticsManager().getStats();
			GenericEntity<Collection<CommandStats>> entity = new GenericEntity<Collection<CommandStats>>(stats){};
			return Response.ok(entity).build();
		}
	}
}
