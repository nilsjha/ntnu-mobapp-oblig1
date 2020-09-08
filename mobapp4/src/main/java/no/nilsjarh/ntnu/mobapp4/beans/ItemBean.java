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

	public Item addItem(User seller, String title, BigDecimal priceNok) {
		System.out.println("=== INVOKING EJB: CREATE ITEM ===");
		System.out.print("Query parameters: title:" + title);
		Item i = new Item(seller, title, priceNok);
		em.persist(i);
		return i;
	}
	
	
	public Item getItem(Long id) {
		System.out.println("=== INVOKING EJB: GET ITEM ===");
		System.out.print("Query parameters: id:" + id);
		if (id == null) return null;
		return em.find(Item.class, id);
	}

	public Item prepareItemForEdit(Item i) {
		System.out.println("=== INVOKING EJB: PREPARE EDIT ITEM ===");
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
		System.out.println("=== INVOKING EJB: FINISH EDIT ITEM ===");
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


	public Item deleteItem(Item i) {
		if (em.find(Item.class, (i.getId())) != null) {
			em.remove(i);
			return null;
		} else {
			return i;
		}
	}

	public List<Item> getItemListBySellerQuery(User seller) {
		em.flush();
		System.out.println("=== INVOKING EJB: FIND ITEM BY USER QUERY ===");
		Query query = em.createNamedQuery(Item.FIND_ITEMS_BY_USER);
		query.setParameter("seller",seller.getId());
		
		List<Item> foundItems = new ArrayList<Item>(query.getResultList());
		
		System.out.println("- Seller.............: " + seller.getId());
		System.out.println("- Found items........: " + returnItemNames(foundItems));
		return foundItems;
	}
	

	public List<Item> getPublishedItems() {
		/// INSERT LOGIC // 
		return new ArrayList<>();
	}
	
		private String returnItemNames(List<Item> list) {
		if (list.isEmpty()) {
			return "<none>";
		}
		StringBuilder sb = new StringBuilder();
		for (Item element : list) {
			sb.append(element.getTitle());
			sb.append(" ");
		}
		return sb.toString();
	}

}
