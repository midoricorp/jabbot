package org.wanna.jabbot.web.services;

import org.wanna.jabbot.BindingManager;
import org.wanna.jabbot.binding.config.BindingConfiguration;
import org.wanna.jabbot.binding.config.ExtensionConfiguration;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/binding")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BindingService {


	@DELETE
	@Path("/{id}")
	@RolesAllowed({"admin"})
	public Response remove(@PathParam("id") String identifier){
		BindingManager manager = BindingManager.getInstance(identifier);
		if(manager != null){
			BindingManager.remove(identifier);
			return Response.ok().build();
		}else{
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}

	@POST
	@RolesAllowed({"admin"})
	public Response create(BindingConfiguration configuration){
		BindingManager.register(configuration);
		return Response.ok().build();
	}

	@GET
	@Path("/{id}/extensions")
	@RolesAllowed({"admin"})
	public Response getExtensions(@PathParam("id") String identifier){
		BindingManager bindingManager = BindingManager.getInstance(identifier);
		if(bindingManager == null){
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		return Response.ok().build();
	}

	@PUT
	@Path("/{id}/extensions")
	@RolesAllowed({"admin"})
	public Response addExtension(@PathParam("id") String identifier, ExtensionConfiguration extension){
		BindingManager bindingManager = BindingManager.getInstance(identifier);
		if(bindingManager == null){
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		bindingManager.getCommandManager().add(extension);
		return Response.ok().build();
	}
}
