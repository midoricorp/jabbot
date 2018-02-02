package org.wanna.jabbot.web.services;

import org.wanna.jabbot.BindingManager;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Path("status")
@Produces(MediaType.APPLICATION_JSON)
public class StatusService {

	@GET
	public Response status(){
		final Collection<BindingManager> managers = BindingManager.getManagers();
		final Status[] statuses = new Status[managers.size()];
		int i = 0;
		for (BindingManager manager : managers) {
			Status status = manager.getStatus();
			statuses[i] = status;
			i++;
		}
		GenericEntity<List<Status>> entity = new GenericEntity<List<Status>>(Arrays.asList(statuses)){};
		return Response.ok(entity).build();
	}
}
