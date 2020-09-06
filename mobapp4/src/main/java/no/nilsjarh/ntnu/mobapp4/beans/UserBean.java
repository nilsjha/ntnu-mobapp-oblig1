/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.nilsjarh.ntnu.mobapp4.beans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
import javax.ws.rs.QueryParam;
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

	//private static final String INSERT_USERGROUP = "INSERT INTO user_has_group(name,id) VALUES (?,?)";
	//private static final String DELETE_USERGROUP = "DELETE FROM user_has_group WHERE name LIKE ? AND id LIKE ?";
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

	public User findUserById(String id) {
		System.out.println("=== INVOKING EJB: FIND USER ===");
		System.out.println("Query parameters - id: " + id);
		User found = em.find(User.class, id);

		if (found == null) {
			System.out.println("- Status...........: " + "Not in database");
			System.out.println();
			return null;
		} else {
			System.out.println("- Status.........: " + "In database");
			System.out.println("- Id.............: " + found.getId());
			System.out.println();
			return found;
		}
	}

	public User findUserByEmail(String email) {
		System.out.println("=== INVOKING EJB: FIND USER ===");
		Query query = em.createNamedQuery(User.FIND_USER_BY_EMAIL);
		query.setParameter("email", email);
		System.out.println("Query parameters - email: " + email);
		List<User> foundUsers = query.getResultList();
		if (foundUsers.size() == 1) {
			User u = foundUsers.get(0);
			System.out.println("- Status.........: " + "In database");
			System.out.println("- Id.............: " + u.getId());
			//System.out.println("- Password....: " + u.getPassword());	
			System.out.println();
			return u;
		} else {

			System.out.println("- Status...........: " + "Not in database");
			System.out.println();
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
			System.out.println();
			return null;
		} else {
			User newUser = new User();
			newUser.setEmail(email);
			newUser.setPassword(hasher.generate(password.toCharArray()));
			Group usergroup = em.find(Group.class,
				Group.USER);
			newUser.getGroups().add(usergroup);
			em.persist(newUser);
			System.out.println("- Status...........: " + "Created OK");
			System.out.println("- In database as id: " + newUser.getId());
			System.out.println("- Group(s).........: " + newUser.getGroups().toString());
			System.out.println();
			return newUser;
		}
	}

	public User addGrRoup(User user, String role, boolean add) {
	
		System.err.println("%%%%%%%%%%%%%%%%%%% HAUNTED METHOD BEGIN %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		System.err.println("%%%%%%%%%%%%%%%%%%% HAUNTED METHOD END %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		System.err.println("%%%%%%%%%%%%%%%%%%% DATABASE LOGIC IS REMOVED, YET IT UPDATES THE DATABASE GROUPS%%%%%%%%%%%%%%%%%%%%%");
		System.out.println("=== INVOKING EJB: GROUP MGMT ===");
		Group groupToChange = findGroupByName(role);
		if (groupToChange == null) {
			System.out.println("- Status...........: " + "Invalid group");
			return null;
		} else {
			if (user == null) {
				System.out.println("- Status...........: " + "Invalid (null) user");
				return null;
			}

			List<Group> currentGroups = user.getGroups();
			List<Group> predictedGroups = currentGroups;
			String action = "add";
			if (add) {
				predictedGroups.add(groupToChange);
			} else {

				predictedGroups.remove(groupToChange);
				action = "remove";
			}

			System.out.println("- User.............: " + user.getId());
			System.out.println("- Current groups...: " + currentGroups);
			System.out.println("- Groups to " + action + ": " + groupToChange.getName());
			System.out.println("- Predicted update...: " + predictedGroups);

			if (user.getGroups().equals(predictedGroups)) {
				// NO UPDATE NESSECARY
				System.out.println("- Status...........: " + "NO CHANGE, SKIPPING");
			} 
//			else {
//				try (Connection c = dataSource.getConnection()) {
//					PreparedStatement psg;
//					if (add) {
//						System.out.println("- Action...........: " + "ADD");
//						psg = c.prepareStatement(INSERT_USERGROUP);
//					} else {
//						psg = c.prepareStatement(DELETE_USERGROUP);
//						System.out.println("- Action...........: " + "REVOKE");
//					}
//					psg.setString(1, role);
//					psg.setString(2, user.getId());
//					psg.executeUpdate();
//					System.out.println("- Status...........: " + "COMPLETE");
//				} catch (SQLException ex) {
//					log.log(Level.SEVERE, null, ex);
//					System.out.println("- SQL..............: " + "FAILED\n");
//					return null;
//				}
//				System.out.println("- Updated groups...: " + user.getGroups().toString());
//				return user;
//
//			}

		}
		System.err.println("%%%%%%%%%%%%%%%%%%% HAUNTED METHOD END %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		return null;
	}

	private Group findGroupByName(String name) {
		if (roleExists(name)) {
			return em.find(Group.class, name);
		} else {
			return null;
		}
	}

	/**
	 *
	 * @param role
	 * @return
	 */
	private boolean roleExists(String role) {
		boolean result = false;
		if (role != null) {
			switch (role) {
				case Group.ADMIN:
					result = true;
				case Group.USER:
					result = true;
					break;
				default:
					break;
			}
		}
		return result;
	}
}
