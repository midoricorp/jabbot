package org.wanna.jabbot.web.services;

import org.wanna.jabbot.BindingContainer;
import org.wanna.jabbot.binding.config.BindingConfiguration;
import org.wanna.jabbot.binding.config.ExtensionConfiguration;

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
		BindingContainer manager = BindingContainer.getInstance(identifier);
		if(manager != null){
			BindingContainer.remove(identifier);
			return Response.ok().build();
		}else{
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}

	@POST
	@RolesAllowed({"admin"})
	public Response create(BindingConfiguration configuration){
		BindingContainer.create(configuration);
		return Response.ok().build();
	}

	@GET
	@Path("/{id}/extensions")
	@RolesAllowed({"admin"})
	public Response getExtensions(@PathParam("id") String identifier){
		BindingContainer bindingContainer = BindingContainer.getInstance(identifier);
		if(bindingContainer == null){
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		return Response.ok().build();
	}


	@PUT
	@Path("/{id}/extensions")
	@RolesAllowed({"admin"})
	public Response addExtension(@PathParam("id") String identifier, ExtensionConfiguration extension){
		BindingContainer bindingContainer = BindingContainer.getInstance(identifier);
		if(bindingContainer == null){
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		bindingContainer.addCommand(extension);
		return Response.ok().build();
	}
}
