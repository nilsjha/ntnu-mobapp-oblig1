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
import no.nilsjarh.ntnu.mobapp4.domain.User;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
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
		User donald = new User();
		donald.setEmail("thedonald@whitehouse.gov");
		donald.setFirstName("Donald");
		donald.setLastName("Trumpo");

		em.persist(donald);

		Query query = em.createNamedQuery(User.FIND_ALL_USERS);
		User ret = (User) query.getResultList().get(0);
		System.out.println("Returned tuple id "
			+ ret.getId()
			+ "\nName:"
			+ ret.getFirstName() + " "
			+ ret.getLastName() + "\n"
			+ ret.getEmail() + "\n"
			+ ret.getMobilePhone() + "\n"
		);
		assertEquals(1, query.getResultList().size());
	}
}
