/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.nilsjarh.ntnu.mobapp4;

import no.nilsjarh.ntnu.mobapp4.domain.Person;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
public class Demo {
public static void main(String args[]){
Person p = new Person();
p.setFirstName("Donald");
p.setLastName(Trump);


Demo demo = new Demo();
demo.persist(p);
}

public void persist(Object object) {
EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("simple-jpaPU");
EntityManager em = emf.createEntityManager();
em.getTransaction().begin();
try {
em.persist(object);
em.getTransaction().commit();
} catch (Exception e) {
e.printStackTrace();
em.getTransaction().rollback();
} finally {
em.close();
}
}
}