package no.ntnu.tollefsen.auth;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;
import java.util.logging.Level;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStoreHandler;
import javax.security.enterprise.identitystore.PasswordHash;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import javax.validation.constraints.NotBlank;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import lombok.extern.java.Log;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.InvalidKeyException;
import java.util.List;
import javax.annotation.Resource;
import javax.persistence.Query;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import no.nilsjarh.ntnu.mobapp4.domain.User;
import no.nilsjarh.ntnu.mobapp4.resources.DatasourceProducer;
import no.nilsjarh.ntnu.mobapp4.beans.*;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.Claims;

import org.eclipse.microprofile.jwt.JsonWebToken;

/**
 * Authentication REST service used for login, logout and to register new users
 *
 * @Path("auth) makes this class into a JAX-RS REST service. "auth" specifies
 * that the URL of this service would begin with "domainname/chat/api/auth"
 * depending on the domain, context path of project and the JAX-RS base
 * configuration
 * @Produces(MediaType.APPLICATION_JSON) instructs JAX-RS that the default
 * result of a method is to be marshalled as JSON
 *
 * @Stateless makes this class into a transactional stateless EJB, which is a
 * requirement of using the JPA EntityManager to communicate with the database.
 *
 * @DeclareRoles({UserGroup.ADMIN,UserGroup.USER}) specifies the roles used in
 * this EJB.
 *
 * @author mikael
 */
@Path("auth")
@Stateless
@Log
public class AuthenticationService {

	@Inject
	KeyService keyService;

	@Inject
	IdentityStoreHandler identityStoreHandler;

	@Inject
	@ConfigProperty(name = "mp.jwt.verify.issuer", defaultValue = "issuer")
	String issuer;

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

	@Inject
	JsonWebToken principal;

	@Inject
	UserBean userBean;

	/**
	 *
	 * @param email
	 * @param pwd
	 * @param request
	 * @return
	 */
	@POST
	@Path("login")
	public Response login(
		@FormParam("email") @NotBlank String email,
		@FormParam("pwd") @NotBlank String pwd,
		@Context HttpServletRequest request) {
		System.out.println("=== INVOKING REST-AUTH: LOGON ===");
		System.out.println("Query parameters");
		System.out.println("- Email.............................: " + email);
		System.out.println("- Password..........................: " + pwd);

		User exsistingUser = userBean.findUserByEmail(email);

		if (!(exsistingUser == null)) {
			UsernamePasswordCredential ucred
				= new UsernamePasswordCredential(exsistingUser.getId(), pwd);

			CredentialValidationResult result
				= identityStoreHandler.validate(ucred);

			if (result.getStatus() == CredentialValidationResult.Status.VALID) {
				String token = issueToken(result.getCallerPrincipal().getName(),
					result.getCallerGroups(), request);

				System.out.println("- Logged on with ID...............: " + exsistingUser.getId());
				System.out.println();
				return Response
					.ok(token)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
					.build();
			}
		}
		System.out.println("- Unable to logon..................: " + email);
		System.out.println();

		return Response.status(Response.Status.UNAUTHORIZED)
			.build();
	}

	/**
	 *
	 * @param name
	 * @param groups
	 * @param request
	 * @return
	 */
	private String issueToken(String name, Set<String> groups, HttpServletRequest request) {
		try {
			Date now = new Date();
			Date expiration = Date.from(LocalDateTime.now().plusDays(1L).atZone(ZoneId.systemDefault()).toInstant());
			JwtBuilder jb = Jwts.builder()
				.setHeaderParam("typ", "JWT")
				.setHeaderParam("kid", "abc-1234567890")
				.setSubject(name)
				.setId("a-123")
				//.setIssuer(issuer)
				.claim("iss", issuer)
				.setIssuedAt(now)
				.setExpiration(expiration)
				.claim("upn", name)
				.claim("groups", groups)
				.claim("aud", "aud")
				.claim("auth_time", now)
				.signWith(keyService.getPrivate());
			return jb.compact();
		} catch (InvalidKeyException t) {
			log.log(Level.SEVERE, "Failed to create token", t);
			throw new RuntimeException("Failed to create token", t);
		}
	}

	private Response buildCreatedUserResponse(String email, String pwd) {
		User createdUser = userBean.createUser(email, pwd);
		if (createdUser == null) {

			return Response.status(Response.Status.BAD_REQUEST).build();
		} else {

			return Response.ok(createdUser).build();
		}
	}

	@POST
	@Path("create")
	@Produces(MediaType.APPLICATION_JSON)
	public Response createUserService(@HeaderParam("email") String email, @HeaderParam("pwd") String pwd) {
		return buildCreatedUserResponse(email, pwd);
	}

	@POST
	@Path("form-create")
	@Produces(MediaType.APPLICATION_JSON)
	public Response createUserFormService(@FormParam("email") String email, @FormParam("pwd") String pwd) {
		return buildCreatedUserResponse(email, pwd);
	}

	/**
	 *
	 * @return
	 */
	@GET
	@Path("currentuser")
	@RolesAllowed(value = {Group.USER})
	@Produces(MediaType.APPLICATION_JSON)
	public User getCurrentUser() {
		return em.find(User.class,
			principal.getName());
	}

	/**
	 *
	 * @param email
	 * @param role
	 * @return
	 */
	@PUT
	@Path("addrole")
	@RolesAllowed(value = {Group.ADMIN})
	public Response addRole(@QueryParam("email") String email, @QueryParam("role") String role) {
		User foundUser = userBean.findUserByEmail(email);
		if (foundUser != null) {
			if (!(userBean.addGrRoup(foundUser, role, false) == null)) {
				return Response.ok().build();
			}
		}
		return Response.status(Response.Status.BAD_REQUEST).build();
	}

	/**
	 *
	 * @param email
	 * @param role
	 * @return
	 */
	@PUT
	@Path("removerole")
	@RolesAllowed(value = {Group.ADMIN})
	public Response removeRole(@QueryParam("email") String email, @QueryParam("role") String role) {
		User foundUser = userBean.findUserByEmail(email);
		if (foundUser != null) {
			if (!(userBean.addGrRoup(foundUser, role, true) == null)) {
				return Response.ok().build();
			}
		}
		return Response.status(Response.Status.BAD_REQUEST).build();
	}

	/**
	 *
	 * @param emailAccess
	 * @param password
	 * @param sc
	 * @return
	 */
	@PUT
	@Path("changepassword")
	@RolesAllowed(value = {Group.USER})
	public Response changePassword(
		@QueryParam("email") String emailAccess,
		@QueryParam("pwd") String password,
		@Context SecurityContext sc) {
		System.out.println("=== INVOKING REST-AUTH: CHANGE PASSWORD ===");
		System.out.println("Query parameters");
		System.out.println("- Email.............................: " + emailAccess);
		System.out.println("- Password..........................: " + password);

		User accessUser = userBean.findUserByEmail(emailAccess);
		if (accessUser == null) {
			System.out.println("- Access User.......................: " + "<No User>");
			System.out.println();
			return Response.status(Response.Status.BAD_REQUEST).build();
		}

		String id = accessUser.getId();
		System.out.println("- Access User.......................: " + id);

		String authuser = sc.getUserPrincipal() != null ? sc.getUserPrincipal().getName() : null;

		if ((password == null || password.length() < 3)) {
			log.log(Level.SEVERE, " #1 Failed to change password on u {0}", id);
			System.out.println("- Password unsatisfied..............: " + password);
			System.out.println();
			return Response.status(Response.Status.BAD_REQUEST).build();
		}

		if (authuser.compareToIgnoreCase(id) != 0 && !sc.isUserInRole(Group.ADMIN)) {
			log.log(Level.SEVERE,
				"#2 No admin access for {0}. Failed to change password on u {1}",
				new Object[]{authuser, id});
			System.out.println("- GroupMembership unsatisfied.......: " + accessUser.getGroups().toString());
			System.out.println();
			return Response.status(Response.Status.BAD_REQUEST).build();
		} else {
			accessUser.setPassword(hasher.generate(password.toCharArray()));
			em.merge(accessUser);
			System.out.println("- Password updated..................: " + password);
			System.out.println();
			return Response.ok().build();
		}
	}
}
