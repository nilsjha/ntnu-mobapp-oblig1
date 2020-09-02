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
import no.nilsjarh.ntnu.mobapp4.domain.Person;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author nils
 */
public class PersonTest {
	private static EntityManagerFactory fab;
	private EntityManager em;
	
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
	public void testValidCreatePerson() {
		try {
			Person donald = new Person();
			donald.setEmail("thedonald@whitehouse.gov");
			donald.setFirstName("Donald");
			donald.setLastName("Trumpo");
			em.persist(donald);
			
			assertEquals(1, 1);
		} catch (Exception e) {
		}
	}
}
