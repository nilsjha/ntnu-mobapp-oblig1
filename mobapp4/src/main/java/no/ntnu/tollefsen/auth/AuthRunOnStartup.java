package no.ntnu.tollefsen.auth;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author mikael
 */

@Singleton
@Startup
public class AuthRunOnStartup {
    @PersistenceContext
    EntityManager em;

    @PostConstruct
    public void init() {
        long groups = (long) em.createQuery("SELECT count(g.name) from Group g").getSingleResult();
        if(groups == 0) {
            em.persist(new Group(Group.USER));
            em.persist(new Group(Group.ADMIN));
	    
        }
	System.out.println(
		"PSQL-RESULT: Found " + 
			em.createQuery("SELECT count(g.name) from Group g")
				.getSingleResult() + " groups in DB");
    }
}
