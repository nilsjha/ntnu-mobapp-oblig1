/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.nilsjarh.ntnu.mobapp4.resources;

import java.math.BigDecimal;
import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.security.enterprise.identitystore.IdentityStoreHandler;
import javax.security.enterprise.identitystore.PasswordHash;
import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import lombok.extern.java.Log;
import no.nilsjarh.ntnu.mobapp4.beans.*;
import no.nilsjarh.ntnu.mobapp4.domain.Item;
import no.nilsjarh.ntnu.mobapp4.domain.User;
import no.ntnu.tollefsen.auth.Group;
import no.ntnu.tollefsen.auth.KeyService;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;

/**
 *
 * @author nils
 */
@Path("marketplace")
@Stateless
@Log
public class MarketplaceService {

	@Inject
	KeyService keyService;

	@Inject
	IdentityStoreHandler identityStoreHandler;

	@Inject
	@ConfigProperty(name = "mp.jwt.verify.issuer", defaultValue = "issuer")
	String issuer;

	/**
	 * The application server will inject a DataSource as a way to
	 * communicate with the database.
	 */
	@Resource(lookup = DatasourceProducer.JNDI_NAME)
	DataSource dataSource;

	/**
	 * The application server will inject a EntityManager as a way to
	 * communicate with the database via JPA.
	 */
	@PersistenceContext
	EntityManager em;

	@Inject
	PasswordHash hasher;

	@Inject
	JsonWebToken principal;

	@Inject
	UserBean ub;

	@Inject
	ItemBean ib;

	@Inject
	PurchaseBean pb;

	@GET
	@Path("list")
	@RolesAllowed(value = {Group.USER})
	public Response listItems(@QueryParam("list-all") boolean listAll) {
		System.out.println("=== INVOKING REST-MARKET: LIST ALL ITEMS ===");
		System.out.print("Query parameters: listall:" + listAll);
		try {
			if (listAll) {
				System.out.print("Mode.....:" + "LISTING ALL+SOLD ITEMS");
				return Response.ok(ib.getAllItems()).build();
			} else {
				// NOT WORKING, EMPTY LIST RESULTS
				System.out.print("Mode.....:" + "LISTING PUBLIC ITEMS");
				return Response.ok(ib.getPublishedItems()).build();
			}
		} catch (Exception e) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	}

	@GET
	@Path("listowned")
	@RolesAllowed(value = {Group.USER})
	public Response listOwnItems(
		@QueryParam("as-seller") boolean soldItems,
		@QueryParam("as-buyer") boolean purchasedItems) {
		System.out.println("=== INVOKING REST-MARKET: LIST OWN ITEMS ===");
		
		Response r = Response.status(Response.Status.BAD_REQUEST).build();
		User u = em.find(User.class, principal.getName());
		
		if (u != null) {
			System.out.print("Query parameters: user:" + u.getId()
				+ "sold:" + soldItems + ", purchased:" + purchasedItems);
			
			if (!(purchasedItems) && (soldItems)) {
				// ONLY SOLD ITEMS RETURNED
				r = Response.ok(ib.getItemListBySellerQuery(u)).build();
			} else if (!(soldItems) && (purchasedItems)) {
				// ONLY PURCHASED ITEMS RETURNED
				r = Response.ok(pb.getPurchaseListByBuyerQuery(u)).build();
			} else if ((soldItems) && (purchasedItems)) {
				// BOTH SOLD AND PURCHASED ITEMS RETURNED
				
				// FIXME: IMPLEMENT CHAINING OF MULTIPLE JSON/
				//        RESPONSES
				
				
				r = Response.status(Response.Status.NOT_IMPLEMENTED).build();
			} else {
				// NONE
				r = Response.ok().build();
				
			}
		}
		return r;
	}

	@POST
	@Path("add")
	@RolesAllowed(value = {Group.USER})
	public Response addItem(@FormParam("title") String title, @FormParam("price") BigDecimal price) {
		System.out.println("=== INVOKING REST-MARKET: CREATE ITEM ===");
		System.out.print("Query parameters: title:" + title + ", price:" + price);
		Item createdItem = ib.addItem(em.find(User.class,
			principal.getName()), title, price);
		if (createdItem != null) {
			return Response.ok(createdItem).build();

		}

		return Response.status(Response.Status.BAD_REQUEST).build();
	}

	@GET
	@Path("view")
	@RolesAllowed(value = {Group.USER})
	public Response viewItem(@QueryParam("id") Long id) {
		System.out.println("=== INVOKING REST-MARKET: VIEW ITEM ===");
		System.out.print("Query parameters: id:" + id);
		Item itemToView = ib.getItem(id);

		if (itemToView == null) {
			System.out.print("Found item.....:" + "<none>");
			return Response.status(Response.Status.BAD_REQUEST).build();
		}

		System.out.print("Found item.....:" + itemToView.getId());
		return Response.ok(itemToView).build();

	}

	@GET
	@Path("purchase")
	@RolesAllowed(value = {Group.USER})
	public Response purchaseItem(@QueryParam("item") Long itemId) {
		System.out.println("=== INVOKING REST-MARKET: PURCHASE ITEM ===");
		System.out.print("Query parameters: id:" + itemId);
		Response r = Response.status(Response.Status.BAD_REQUEST).build();
		User buyer = em.find(User.class, principal.getName());
		if ((buyer == null) || (itemId == null)) {
			return r;
		} else {
			r = Response.ok(pb.addPurchase(buyer, itemId)).build();
		}

		return r;
	}

	@PATCH
	@Path("edit")
	@RolesAllowed(value = {Group.USER})
	public Response editItem() {
		// TO IMPL A DIFF/MERGING EDIT METHOD IN THE FUTURE
		return Response.status(Response.Status.NOT_IMPLEMENTED).build();
	}

	@POST
	@Path("update")
	@RolesAllowed(value = {Group.USER})
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateItem(
		@QueryParam("id") Long id,
		@FormParam("title") String title,
		@FormParam("description") String descr,
		@FormParam("price") BigDecimal pNok) {
		System.out.println("=== INVOKING REST-MARKET: UPDATE ITEM ===");
		System.out.print("Query parameters: id:" + id + " title:" 
			+ title + " descr:" + descr + " price:" + pNok);
		Response r = Response.status(Response.Status.BAD_REQUEST).build();
		Item toEdit = ib.getItem(id);
		User seller = em.find(User.class, principal.getName());
		if ((toEdit == null) || (seller == null)) {
			System.out.println("=== INVOKING REST-MARKET: UPDATE ITEM ===");
			System.out.println("Status:.............: STOP(invalid args)");
			System.out.println(title + "," + descr + ","+ pNok);
			
		} else {
			if (ib.verifyOwnedItem(toEdit, seller)) {
			System.out.println("=== INVOKING REST-MARKET: UPDATE ITEM ===");
			System.out.println("Status:.............: Valid input");
				ib.prepareItemForEdit(toEdit);
				if (descr != null) toEdit.setDescription(descr);
				if (title != null) toEdit.setTitle(title);
				if (pNok != null) toEdit.setPriceNok(pNok);
				Item edited = ib.saveItemFromEdit(toEdit);
				if(edited != null) {	
					System.out.println("DB write:..........: Success");
				}
				r = Response.ok(edited).build();
			}
		}
		return r;
	}

	@DELETE
	@Path("remove")
	@RolesAllowed(value = {Group.USER})
	public Response deleteItem(@QueryParam("id") Long id) {
		System.out.println("=== INVOKING REST-MARKET: DELETE ITEM ===");
		System.out.print("Query parameters: id:" + id);
		Item itemToDelete = ib.getItem(id);
		if (itemToDelete != null) {
			System.out.print("Found item.....:" + itemToDelete.getId());
			if (ib.verifyOwnedItem(itemToDelete, em.find(User.class,
				principal.getName()))) {

				if (ib.deleteItem(itemToDelete)) {
					return Response.ok("").build();
				} else {

				}

			}
			System.out.print("State..........:" + "NO ACCESS");
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}

		System.out.print("State..........:" + "NO ITEM");
		return Response.status(Response.Status.BAD_REQUEST).build();
	}

}