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
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.sql.DataSource;
import no.nilsjarh.ntnu.mobapp4.domain.*;
import no.nilsjarh.ntnu.mobapp4.resources.DatasourceProducer;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 *
 * @author nils
 */
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

	public List<Item> getOwnedItems(User seller) {
		Query query = em.createNamedQuery(Item.FIND_ITEMS_BY_USER);
		return query.getResultList();
	}

	public Item addItem(User seller, String title, BigDecimal priceNok) {
		Item i = new Item(seller, title, priceNok);
		em.persist(i);
		return i;
	}
	
	public Item findItem(User u, Long id) {
		Item found = em.find(Item.class, id);
		if (Objects.equals(found.getSellerUser().getId(), id)) {
			return found;
		} else {
			return null;
		}
	}

	public String deleteItem(Long id) {
		Item i = em.find(Item.class, id);
		String status;
		try {
			em.getTransaction().begin();
			em.remove(i);
			em.getTransaction().commit();
			i.setSellerUser(null);
			status = "Deleted item " + i.getId();
			
		} catch (Exception e) {
			System.err.println("Unable to delete " + i.getId());
			System.err.print(e);
			status = "Unable to remove item " + i.getId();
		}
		return status;
	}

}
