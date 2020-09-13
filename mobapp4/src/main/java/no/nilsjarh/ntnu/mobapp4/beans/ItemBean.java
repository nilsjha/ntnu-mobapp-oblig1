/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.nilsjarh.ntnu.mobapp4.beans;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.sql.DataSource;
import javax.ws.rs.core.Response;
import lombok.extern.java.Log;
import no.nilsjarh.ntnu.mobapp4.domain.*;
import no.nilsjarh.ntnu.mobapp4.resources.DatasourceProducer;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 *
 * @author nils
 */
@Log
@Stateless
public class ItemBean {

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
	AttachmentBean ab;

	public Item addItem(User seller, String title, BigDecimal priceNok) {
		System.out.println("=== ITEM EJB: CREATE ITEM ===");
		System.out.print("Query parameters: title:" + title);
		Item i = new Item(seller, title, priceNok);
		Item o = em.merge(i);
		em.flush();
		System.out.println("- Status...........: " + "Created OK");
		System.out.println("- In database as id: " + o.getId());
		System.out.println("- Title............: " + o.getTitle());
		System.out.println("- Seller...........: "
			+ seller.getFirstName() + " "
			+ seller.getLastName() + "("
			+ seller.getId() + ")");

		return o;
	}

	public Item getItem(Long id) {
		System.out.println("=== ITEM EJB: GET ITEM ===");
		System.out.print("Query parameters: id:" + id);
		if (id == null) {
			System.out.println("- Id.............: " + "<null>");
			return null;
		}
		Item found = em.find(Item.class, id);
		if (found == null) {
			System.out.println("- Id.............: " + "<Not Found>");
			return null;
		}
		em.refresh(found);
		System.out.println("- Status.........: " + "In database");
		System.out.println("- Id.............: " + found.getId());
		return found;
	}

	public Item prepareItemForEdit(Item i) {
		System.out.println("=== ITEM EJB: PREPARE EDIT ITEM ===");
		if (i != null) {
			try {
				em.lock(i, LockModeType.PESSIMISTIC_WRITE);
				return i;
			} catch (Exception e) {
				log.log(Level.SEVERE, "Load of item failed with e:{0}", e);
			}
		}
		return null;
	}

	public Item saveItemFromEdit(Item toSave) {
		System.out.println("=== ITEM EJB: FINISH EDIT ITEM ===");
		if (toSave == null) {
			return null;
		} else {
			try {
				em.merge(toSave);
				em.lock(toSave, LockModeType.NONE);
				em.flush();
				return toSave;
			} catch (Exception e) {
				log.log(Level.SEVERE, "Save of item failed with e:{0}", e);
				return null;
			}
		}
	}

	public boolean verifyOwnedItem(Item i, User owner) {
		if (i != null) {
			if (i.getSellerUser().equals(owner)) {
				return true;
			}
		}
		return false;
	}

	public boolean deleteItem(Item i) {
		System.out.println("=== ITEM EJB: DELETE ITEM ===");
		if (i != null) {
			Long id = i.getId();
			if (id == null) {
				return false;
			}
			System.out.println("- Found item id..: " + id);
			em.refresh(i);
			if (i.getPurchase() == null) {
				ab.removeAllFromItem(i);
				em.remove(i);
				em.flush();
				if (getItem(id) == null) {
					System.out.println("=== ITEM EJB: DELETE ITEM ===");
					System.out.println("- Status.........: " + "DELETED");
					return true;
				}
			} else {
				System.out.print("State..........:" + "STOP - PURCHASED");
				return false;
			}

		}
		System.out.print("State..........:" + "ERROR ON DELETE");
		return false;
	}

	public List<Item> getItemListBySeller(User seller) {
		if (seller == null) {
			return new ArrayList<>();
		}
		System.out.println("=== ITEM EJB: FIND ITEM BY USER QUERY ===");
		Query query = em.createNamedQuery(Item.FIND_ITEMS_BY_USER);
		query.setParameter("seller", seller.getId());

		List<Item> foundItems = new ArrayList<Item>(query.getResultList());

		System.out.println("- Seller.............: " + seller.getId());
		System.out.println("- Found items........: " + returnItemNames(foundItems));
		return foundItems;
	}

	public List<Item> getPublishedItems() {
		System.out.println("=== ITEM EJB: FIND PUBLIC ITEMS QUERY ===");
		Query query = em.createNamedQuery(Item.FIND_ALL_ITEMS_UNSOLD);

		// FIXME: INSERT LOGIC/FILTERING FOR AND
		//        PUBLISH/EXPIRE DATES
		List<Item> allItems = new ArrayList<Item>(query.getResultList());
		System.out.println("- Found items........: " + returnItemNames(allItems));

		return allItems;
	}

	public List<Item> getAllItems() {
		System.out.println("=== ITEM EJB: FIND ALL ITEMS QUERY ===");
		Query query = em.createNamedQuery(Item.FIND_ALL_ITEMS);

		List<Item> allItems = new ArrayList<Item>(query.getResultList());
		System.out.println("- Found items........: " + returnItemNames(allItems));

		return allItems;
	}

	private String returnItemNames(List<Item> list) {
		if (list.isEmpty()) {
			return "<none>";
		}
		StringBuilder sb = new StringBuilder();
		for (Item element : list) {
			sb.append("[");
			sb.append(element.getId());
			sb.append(":" + element.getTitle());
			sb.append("], ");
		}
		return sb.toString();
	}

}
