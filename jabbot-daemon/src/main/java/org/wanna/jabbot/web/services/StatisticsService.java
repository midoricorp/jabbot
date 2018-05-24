package org.wanna.jabbot.web.services;

import org.wanna.jabbot.BindingContainer;
import org.wanna.jabbot.ConnectionInfo;
import org.wanna.jabbot.statistics.CommandStats;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Path("stats")
@Produces(MediaType.APPLICATION_JSON)
public class StatisticsService {

	@GET
	public Response status(){
		final Collection<BindingContainer> managers = BindingContainer.getRegistry();
		final ConnectionInfo[] statuses = new ConnectionInfo[managers.size()];
		int i = 0;
		for (BindingContainer manager : managers) {
			ConnectionInfo status = manager.getConnectionInfo();
			statuses[i] = status;
			i++;
		}
		GenericEntity<List<ConnectionInfo>> entity = new GenericEntity<List<ConnectionInfo>>(Arrays.asList(statuses)){};
		return Response.ok(entity).build();
	}

	@GET
	@Path("/{binding}/")
	public Response status(@PathParam("binding") String bindingIndentifier){
		BindingContainer container = BindingContainer.getInstance(bindingIndentifier);
		if( container == null ){
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		return Response.ok(container.getConnectionInfo()).build();
	}

	@GET
	@Path("/{binding}/commands")
	public Response commandsUsage(@PathParam("binding") String bindingIdentifier){
		BindingContainer manager  = BindingContainer.getInstance(bindingIdentifier);
		if(manager == null){
			return Response.status(Response.Status.NOT_FOUND).build();
		}else{
			Collection<CommandStats> stats = manager.getStatisticsManager().getStats();
			GenericEntity<Collection<CommandStats>> entity = new GenericEntity<Collection<CommandStats>>(stats){};
			return Response.ok(entity).build();
		}
	}
}
