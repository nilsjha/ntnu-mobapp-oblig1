/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.nilsjarh.ntnu.mobapp4.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author nils
 */
@Path("welcome")
public class FantService {
	private String testMessage;
	
	@Context
	private UriInfo context;
	
	public FantService() {
		
	}
	
	@GET
	@Produces("text/html")
	public String getHtml() {
		        return "<html lang=\"en\">"
				+ "<body>"
				+ "<h1>"
				+ "Welcome to the FantService API"
				+ "</h1>"
				+ "<p>Posted message:"
				+ "<strong>"
				+ this.testMessage
				+ "</strong>"
				+ "</p>"
				+ "</body>"
				+ "</html>";
	}
	
	@POST
	@Consumes("text/plain")
	public void postHtml(String message) {
		this.testMessage = message;
	}
	
}
