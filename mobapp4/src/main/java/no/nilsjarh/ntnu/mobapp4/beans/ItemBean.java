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
		Item i = new Item(seller, title, priceNok);
		em.persist(i);
		return i;
	}
	
	
	public Item getItem(Long id) {
		return em.find(Item.class, id);
	}

	public Item prepareItemForEdit(Item i) {
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
		Query query = em.createNamedQuery(Item.FIND_ITEMS_BY_USER);
		query.setParameter("seller",seller.getId());
		System.out.println("QUERY:" + query.toString());
		return query.getResultList();
	}
	

	public List<Item> getPublishedItems() {
		/// INSERT LOGIC // 
		return new ArrayList<>();
	}

}
