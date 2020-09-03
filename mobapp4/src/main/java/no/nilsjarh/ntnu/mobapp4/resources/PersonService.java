/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.nilsjarh.ntnu.mobapp4.resources;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import no.nilsjarh.ntnu.mobapp4.domain.*;

/**
 *
 * @author nils
 */
@Path("person")
public class PersonService {
	
	@PersistenceContext
	EntityManager em;
	
	@GET
	@Path("test")
	public Response ping() {
		return Response.ok().entity("PersonService OK").build();
		
	}
	
	@GET
	@Path("get/{id}")
	public Response getUser(@PathParam("id") int id) {
		Person p = em.find(Person.class, id);
		if(p == null) {
			
		}
		return Response.ok().entity("blalba").build();
	}
}
