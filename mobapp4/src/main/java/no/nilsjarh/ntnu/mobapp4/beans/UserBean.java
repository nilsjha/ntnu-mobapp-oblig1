/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.nilsjarh.ntnu.mobapp4.beans;

import java.util.List;
import java.util.logging.Level;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.security.enterprise.identitystore.PasswordHash;
import javax.sql.DataSource;
import javax.ws.rs.core.Response;
import no.nilsjarh.ntnu.mobapp4.domain.*;
import no.nilsjarh.ntnu.mobapp4.resources.DatasourceProducer;
import no.ntnu.tollefsen.auth.Group;
import lombok.extern.java.Log;
/**
 *
 * @author nils
 */
@Stateless
@Log
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
	
	@Inject
	PasswordHash hasher;
	
	
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
	
		/**
	 * Does an insert into the users and user_has_group tables. It creates a
	 * SHA-256 hash of the password and Base64 encodes it before the u is
	 * created in the database. The authentication system will read the
	 * AUSER table when doing an authentication.
	 *
	 * @param email
	 * @param password
	 * @return
	 */
	public User createUser(String email, String password) {
		System.out.println("=== INVOKING EJB: CREATE USER ===");
		System.out.print("Params: '" + email + "','" + password + "'\n");
		User u = findUserByEmail(email);

		if (!(u == null)) {
			System.out.println("**ERR: USER EXSISTS**");
			System.out.println("Id: " + u.getId().getClass() + ":" + u.getId());
			System.out.println("Email: " + u.getEmail().getClass() + ":" + u.getEmail());
			log.log(Level.INFO, "User already exists {0}",
				email);
			return null;
		} else {
			System.out.println("**OK: CREATING USER**");
			User newUser = new User();
			newUser.setEmail(email);
			newUser.setPassword(hasher.generate(password.toCharArray()));
			Group usergroup = em.find(Group.class,
				 Group.USER);
			newUser.getGroups().add(usergroup);
			return em.merge(newUser);
		}
	}
	
}

