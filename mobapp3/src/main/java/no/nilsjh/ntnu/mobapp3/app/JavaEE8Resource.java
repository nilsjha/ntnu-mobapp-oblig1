package no.nilsjh.ntnu.mobapp3.app;

import no.nilsjh.ntnu.mobapp3.domain.Person;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author 
 */
@Path("javaee8")
public class JavaEE8Resource {
    
    @PersistenceContext
    EntityManager em;
    
    @GET
    public Response ping(){
        return Response
                .ok("ping")
                .build();
    }
    
    
    @GET
    @Path("new")
    @Produces(MediaType.APPLICATION_JSON)
    public Person create(String name) {
        Person result = new Person();
        result.setName(name);
        em.persist(result);
        return result;
    }
    
    @GET
    @Path("persons")
    public List<Person> getPersons() {
        return em.createQuery("SELECT p FROM Person", Person.class)
                .getResultList();
    }

    @POST
    @Path("register")
    public Response createPerson(
            @FormParam("pid") String email,
            @FormParam("pass") String pass) {
        Person p = em.find(Person.class, email);
        Person p = em.
        if (!(p == null)) {
            return Response.status(Response.Status.BAD_REQUEST).build();;
        } else {
            p = new Person();
        }


    }

    @DELETE
    @Path("delete")
    public void deletePerson() {

    }

    @PUT
    @Path("update")
    public void updatePerson() {

    }

}
