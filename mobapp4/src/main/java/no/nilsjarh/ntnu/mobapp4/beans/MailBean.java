/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.nilsjarh.ntnu.mobapp4.beans;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import lombok.extern.java.Log;
import no.nilsjarh.ntnu.mobapp4.domain.*;
import no.nilsjarh.ntnu.mobapp4.resources.DatasourceProducer;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 *
 * @author nils
 */
@Log
@Stateless
public class MailBean {
	
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
	@ConfigProperty(name = "mail.smtp.host")
	private String smtpHost;

	@Inject
	@ConfigProperty(name = "mail.smtp.username")
	private String smtpUser;

	@Inject
	@ConfigProperty(name = "mail.smtp.sender")
	private String smtpSender;

	@Inject
	@ConfigProperty(name = "mail.smtp.password")
	private String smtpPassword;

	@Inject
	@ConfigProperty(name = "app.contact.brandname")
	private String appBrandname;

	@Inject
	@ConfigProperty(name = "app.contact.telephone")
	private String appBrandTelephone;

	@Inject
	@ConfigProperty(name = "app.contact.website")
	private String appBrandWeb;

	@Inject
	@ConfigProperty(name = "app.contact.address")
	private String appBrandAddress;

	@Inject
	@ConfigProperty(name = "app.contact.gdpr")
	private String appBrandLinkGdpr;

	/**
	 * Send an email
	 *
	 * @param to
	 * @param subject
	 * @param body
	 * @return
	 */
	public void sendEmail(String to, String subject, String body) {
		try {
			Properties props = new Properties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", smtpHost);
			props.put("mail.smtp.port", "587");
			Session mailSession = Session.getInstance(props, new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(smtpUser, smtpPassword);
				}
			});

			Message message = new MimeMessage(mailSession);
			message.setSubject(subject);
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
			message.setFrom(new InternetAddress(smtpSender));
			message.setText(body);

			Transport.send(message);
		} catch (MessagingException ex) {
			Logger.getLogger(MailBean.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * 
	 * @param p Purchase object
	 * @param s Seller object
	 * @param b Buyer Object
	 * @return 
	 */
	public String generateMailBody(Purchase p, User s, User b) {
		if (p == null) {
			return null;
		}
		Item i = p.getItem();
		em.refresh(i);
		StringBuilder sb = new StringBuilder();

		sb.append("Hello from ").append(appBrandname).append("!").append("\n").append("\n");
		sb.append("You are receiving mail because an item has been sold.");
		sb.append("\n").append("\n");
		sb.append("=== ORDER DETAILS ===").append("\n");
		sb.append("- Status...........: " + "Purchase completed").append("\n");
		sb.append("- Seller...........: ").append(insertNames(s)).append("\n");
		sb.append("- Buyer............: ").append(insertNames(b)).append("\n");
		sb.append("- Price ........NOK: ").append(p.getItem().getPriceNok()).append("\n");
		sb.append("- Completed on.....: ").append(p.getPurchaseDate()).append("\n");
		sb.append("- Item.............: ").append(p.getItem().getTitle()).append("\n");
		sb.append("\n");
		sb.append("\n");
		sb.append(appBrandname).append("\n");
		sb.append(appBrandAddress).append("\n");
		sb.append("Tel: ").append(appBrandTelephone).append("\n");
		sb.append("Web: ").append(appBrandWeb).append("\n");
		sb.append("\n");

		if (appBrandLinkGdpr.isBlank() == false) {
			sb.append("Read privacy policy & GDPR info at: ").append(appBrandLinkGdpr).append("\n");
		}
		return sb.toString();

	}

	private String insertNames(User u) {
		String firstname = u.getFirstName();
		String lastname = u.getLastName();

		StringBuilder build = new StringBuilder();
		if (firstname != null) {
			build.append(firstname);

		} else if (lastname != null) {
			build.append(lastname);

		} else if ((lastname != null) && (firstname != null)) {
			build.append(firstname);
			build.append(" ");
			build.append(lastname);
			build.append(" (");
			build.append(u.getEmail());
			build.append(" (");

		} else {
			build.append(u.getEmail());
		}
		return build.toString();
	}
}
