/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.nilsjarh.ntnu.mobapp4.beans;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.sql.DataSource;
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
		Item i = new Item(seller, title, priceNok);
		em.persist(i);
		return i;
	}

	public Item getItem(Long id) {
		return em.find(Item.class, id);
	}
	
	public Item updateItem(Item i) {
		if (getItem(i.getId()) != null) {
			return em.merge(i);
		} else {
			return null;
		}
	}
	
	public Item deleteItem(Item i) {
		if (getItem(i.getId()) != null) {
			em.remove(i);
			return null;
		} else {
			return i;
		}
	}


	public Item getItemBySeller(User u, Long id) {
		Item found = em.find(Item.class, id);
		if (verifyOwnedItem(u, found) != null) {
			return found;
		} else {
			return null;
		}
	}
	private Item verifyOwnedItem(User seller,Item item) {
		if (item.getSellerUser().equals(seller)) {
			return item;
		} else {
			return null;
		}
	}

	public Item deleteOwnedItem(User owner, Item toDelete) {
		if (verifyOwnedItem(owner, toDelete) != null) {
			try {
				em.remove(toDelete);
				return null;

			} catch (Exception e) {
				System.err.println("Unable to delete " + toDelete.getId());
				System.err.print(e);
				return toDelete;
			}

		} else {
			return toDelete;
		}
	}
	
	public List<Item> getItemListBySeller(User seller) {
		Query query = em.createNamedQuery(Item.FIND_ITEMS_BY_USER);
		return query.getResultList();
	}
	

}
