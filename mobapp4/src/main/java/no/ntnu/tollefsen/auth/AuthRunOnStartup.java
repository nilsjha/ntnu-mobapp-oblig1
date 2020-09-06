package no.ntnu.tollefsen.auth;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import no.nilsjarh.ntnu.mobapp4.beans.UserBean;
import no.nilsjarh.ntnu.mobapp4.domain.User;

/**
 *
 * @author mikael
 */
@Singleton
@Startup
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
		if (userBean.addGrRoup(admin, "admin", true) == null) {
			System.out.println("You should have not seen this, as this shall never be NULL on any DB action taken");

		}
		System.out.println("Adding duplcate group, should be handled by logic and give no error :)");
		userBean.addGrRoup(admin, "admin", true);


	}
}
