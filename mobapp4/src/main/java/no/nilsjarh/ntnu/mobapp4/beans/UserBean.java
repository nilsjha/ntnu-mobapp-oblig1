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
		System.out.println("Query parameters");
			System.out.println("- Email............: " + email);
		List<User> foundUsers = query.getResultList();
		if (foundUsers.size() == 1) {
			User u = foundUsers.get(0);
			System.out.println("- Status.........: " + "In database");
			System.out.println("- Id.............: " + u.getId());
			//System.out.println("- Password....: " + u.getPassword());	
			System.out.println("=== END EJB: FIND USER ===\n");
			return u;
		} else {

			System.out.println("- Status...........: " + "Not Found");
			System.out.println("=== END EJB: FIND USER ===\n");
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
		System.out.println("Query parameters");
			System.out.println("- Email............: " + email);
			System.out.println("- Password.........: " + password);
		User u = findUserByEmail(email);

		if (!(u == null)) {
			System.out.println("- Id...............: " + u.getId());
			System.out.println("- Status...........: " + "Already Exist");
			//System.out.println("- Password....: " + u.getPassword());
			//log.log(Level.INFO, "User already exists {0}", email);
			System.out.println("=== END EJB: FIND USER ===\n");
			return null;
		} else {
			User newUser = new User();
			newUser.setEmail(email);
			newUser.setPassword(hasher.generate(password.toCharArray()));
			Group usergroup = em.find(Group.class,
				Group.USER);
			newUser.getGroups().add(usergroup);
			newUser = em.merge(newUser);
			System.out.println("- Status...........: " + "Created OK");
			System.out.println("- Id...............: " + newUser.getId());
			System.out.println("=== END EJB: FIND USER ===\n");
			return newUser;
		}
	}

}
