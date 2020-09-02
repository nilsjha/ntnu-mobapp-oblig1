/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.nilsjarh.ntnu.mobapp4.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 *
 * @author nils
 */
@Path("generate")
public class GeneratorService {
	@GET
	public Response createTables() {
		
		return Response.ok("Generated tables").build();
	}
}
