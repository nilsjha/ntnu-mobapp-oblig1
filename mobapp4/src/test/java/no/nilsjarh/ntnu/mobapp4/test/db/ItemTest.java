/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.nilsjarh.ntnu.mobapp4.test.db;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import no.nilsjarh.ntnu.mobapp4.domain.*;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author nils
 */
@Ignore
public class ItemTest {
	private static EntityManagerFactory fab;
	private EntityManager em;
	
	private User seller;
	private User buyer;
	private Item item;

	@BeforeClass
	public static void createEMfactory() {
		fab = Persistence
			.createEntityManagerFactory("Test");

	}

	@AfterClass
	public static void closeEMfactory() {
		fab.close();
	}

	@Before
	public void beginTransaction() {
		em = fab.createEntityManager();
		em.getTransaction().begin();
		



	}

	@After
	public void rollbackTransaction() {
		if (em.getTransaction().isActive()) {
			em.getTransaction().rollback();
		}
		if (em.isOpen()) {
			em.close();
		}
	}

	
	@Test
	public void createItem() {
		seller = new User();
		seller.setEmail("thedonald@whitehouse.gov");
		seller.setFirstName("Donald J");
		seller.setLastName("Trump");

		em.persist(seller);
		
		item = new Item();
		item.setTitle("Russian emails");
		item.setDescription("Some secretz...");
		item.setSellerPerson(seller);
		
		em.persist(item);
		
		Query query = em.createNamedQuery(Item.FIND_ALL_ITEMS);
		assertEquals(1, query.getResultList().size());	
	}
	
	@Test
	public void createItemWithoutOwner() {
		
	}
	
	
}
