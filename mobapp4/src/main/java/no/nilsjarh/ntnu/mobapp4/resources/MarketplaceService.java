/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.nilsjarh.ntnu.mobapp4.resources;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;
import lombok.extern.java.Log;
import no.nilsjarh.ntnu.mobapp4.beans.*;
import no.nilsjarh.ntnu.mobapp4.domain.Attachment;
import no.nilsjarh.ntnu.mobapp4.domain.Item;
import no.nilsjarh.ntnu.mobapp4.domain.Purchase;
import no.nilsjarh.ntnu.mobapp4.domain.User;
import no.ntnu.tollefsen.auth.Group;
import no.ntnu.tollefsen.auth.KeyService;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import net.coobird.thumbnailator.Thumbnails;

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

	@Inject
	MailBean mb;

	@Inject
	AttachmentBean ab;

	@Context
	SecurityContext sc;

	@GET
	@Path("list")
	//@RolesAllowed(value = {Group.USER})
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
				r = Response.ok(ib.getItemListBySeller(u)).build();
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
			Purchase p = pb.addPurchase(buyer, itemId);
			if (p != null) {
				r = Response.ok(p).build();
				mb.sendEmail(buyer.getEmail(), "Item purchased", mb.generateMailBody(p, p.getItem().getSellerUser(), buyer));
				mb.sendEmail(p.getItem().getSellerUser().getEmail(), "Your item was sold", mb.generateMailBody(p, p.getItem().getSellerUser(), buyer));
			}
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
			System.out.println(title + "," + descr + "," + pNok);

		} else {
			if (ib.verifyOwnedItem(toEdit, seller)) {
				System.out.println("=== INVOKING REST-MARKET: UPDATE ITEM ===");
				System.out.println("Status:.............: Verified seller " + seller.getId());
				if (toEdit.getPurchase() == null) {
					ib.prepareItemForEdit(toEdit);
					if (descr != null) {
						toEdit.setDescription(descr);
					}
					if (title != null) {
						toEdit.setTitle(title);
					}
					if (pNok != null) {
						toEdit.setPriceNok(pNok);
					}
					Item edited = ib.saveItemFromEdit(toEdit);
					if (edited != null) {
						System.out.println("DB write:..........: Success");
					}
					r = Response.ok(edited).build();
				} else {

					System.out.println("DB Write:.............: Abort, already sold");
					return r;
				}
				System.out.println("DB Write:.............: Abort, not owner");
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
				}

			}
			System.out.println("=== INVOKING REST-MARKET: DELETE ITEM ===");
			System.out.print("State..........:" + "NO ACCESS");
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}

		System.out.print("State..........:" + "NO ITEM");
		return Response.status(Response.Status.BAD_REQUEST).build();
	}

	/**
	 * Accepts a multipart POST image
	 *
	 * @param itemid id of item to attach image to
	 * @param description text description of image
	 * @param multiPart used to extract the image data
	 * @return
	 */
	@POST
	@Path("attach")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({Group.USER})
	public Response sendMessage(@FormDataParam("itemid") Long itemid,
		@FormDataParam("description") String description,
		FormDataMultiPart multiPart) {
		System.out.println("=== INVOKING REST-MARKET: ATTACH TO ITEM ===");
		Response r = Response.notModified().build();

		User user = em.find(User.class, sc.getUserPrincipal().getName());
		Item toAttach = ib.getItem(itemid);
		if (toAttach != null && (user.equals(toAttach.getSellerUser()))) {
			System.out.print("Item UID.............:" + toAttach.getId());
			System.out.print("Owner................:" + toAttach.getSellerUser());
			toAttach = ab.uploadAttachment(toAttach, multiPart, description);

			if (toAttach != null) {
				System.out.println("=== INVOKING REST-MARKET: ATTACH TO ITEM ===");
				System.out.print("State................:" + "ADDED ATTACHMENT");
				return Response.ok(toAttach).build();
			}
			System.out.println("=== INVOKING REST-MARKET: ATTACH TO ITEM ===");
			System.out.print("State................:" + "ERROR, CHECK LOGS");
			return r;
		} else {
			System.out.println("=== INVOKING REST-MARKET: ATTACH TO ITEM ===");
			System.out.print("State................:" + "NO ACCESS, NOT MODIFIED");
		}
		return r;
	}

	/**
	 * Streams an image to the browser(the actual compressed pixels). The
	 * image will be scaled to the appropriate width if the with parameter
	 * is provided.
	 *
	 * @param id the filename of the image
	 * @param width the required scaled with of the image
	 *
	 * @return the image in original format or in jpeg if scaled
	 */
	@GET
	@Path("image/{id}")
	@Produces("image/jpeg")
	public Response getImage(@PathParam("id") String id,
		@QueryParam("width") int width) {
		System.out.println("=== INVOKING REST-MARKET: GET ATTACHMENT ===");

		StreamingOutput result = ab.streamAttachment(id, width);

		if (result != null) {

			// Ask the browser to cache the image for 24 hours
			CacheControl cc = new CacheControl();

			cc.setMaxAge(
				86400);
			cc.setPrivate(
				true);

			return Response.ok(result)
				.cacheControl(cc).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}

}
