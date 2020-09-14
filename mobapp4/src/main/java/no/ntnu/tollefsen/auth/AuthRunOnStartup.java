package no.ntnu.tollefsen.auth;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import lombok.extern.java.Log;
import no.nilsjarh.ntnu.mobapp4.beans.UserBean;
import no.nilsjarh.ntnu.mobapp4.domain.User;

/**
 *
 * @author mikael
 */
@Singleton
@Startup
@Log
public class AuthRunOnStartup {

	@PersistenceContext
	EntityManager em;

	@Inject
	UserBean userBean;

	@PostConstruct
	public void init() {
		long groups = (long) em.createQuery("SELECT count(g.name) from Group g").getSingleResult();
		if (groups == 0) {
			em.persist(new Group(Group.USER));
			em.persist(new Group(Group.ADMIN));

		}

		System.out.println(
			"PSQL-RESULT: Found "
			+ em.createQuery("SELECT count(g.name) from Group g")
				.getSingleResult() + " groups in DB");

		User admin = userBean.createUser("admin@admin.ad", "123456");
		// TEST DUP USER
		//userBean.addGroup(admin, "user", true);
		// TEST REMOVE USER
		//userBean.addGroup(admin, "user", false);
		// TEST ADD USER
		//userBean.addGroup(admin, "user", true);
		// TEST ADD ADMIN
		userBean.addGroup(admin, "admin", true);

		userBean.getUserInfo(admin);



	}
}
