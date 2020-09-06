/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.nilsjarh.ntnu.mobapp4.beans;

import java.util.List;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.sql.DataSource;
import no.nilsjarh.ntnu.mobapp4.domain.*;
import no.nilsjarh.ntnu.mobapp4.resources.DatasourceProducer;
/**
 *
 * @author nils
 */
@Stateless
public class UserBean {

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
	public User findUserByEmail(String email) {
		
			System.out.println("=== INVOKING EJB: FIND USER ===");
		Query query = em.createNamedQuery(User.FIND_USER_BY_EMAIL);
		query.setParameter("email", email);
		List<User> foundUsers = query.getResultList();
		if (foundUsers.size() == 1) {
			User u = foundUsers.get(0);
			System.out.println("**FOUND - RETURN USER**");
			System.out.println("Id: " + u.getId().getClass() + ":" + u.getId());
			System.out.println("Email: " + u.getEmail().getClass() + ":" + u.getEmail());
			System.out.println("Password: " + u.getPassword().getClass() + ":" + u.getPassword());
			return u;
		} else {
			
			System.out.println("**NO HIT - RETURN NULL**");
			return null;
		}
	}
	
}

