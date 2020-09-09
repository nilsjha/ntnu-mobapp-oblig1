/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.nilsjarh.ntnu.mobapp4.beans;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import lombok.Getter;
import lombok.extern.java.Log;
import no.nilsjarh.ntnu.mobapp4.domain.*;
import no.nilsjarh.ntnu.mobapp4.resources.DatasourceProducer;
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

/**
 *
 * @author nils
 */
@Log
@Stateless
public class AttachmentBean {
	
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
	UserBean ub;

	@Inject
	ItemBean ib;

	@Context
	SecurityContext sc;

	/**
	 * 
	 * @param i The Item to attach the attachment to
	 * @param multiPart MultiPartForm object to retreive the attachment
	 * @param path Folder path to store the attachment
	 * @param description A description of the item (optional)
	 * @return 
	 */
	public Item uploadAttachment(Item i, FormDataMultiPart multiPart, String path, String description) {
		try {
		System.out.println("=== INVOKING EJB-ATTACHMENT: UPLOAD ===");
			List<FormDataBodyPart> images = multiPart.getFields("image");
			if (images != null && i != null) {
				for (FormDataBodyPart part : images) {
					InputStream is = part.getEntityAs(InputStream.class);
					ContentDisposition meta = part.getContentDisposition();

					String pid = UUID.randomUUID().toString();
					if (!(Files.exists(Paths.get(path)))) {
						Files.createDirectory(Paths.get(path));
					}
					Files.copy(is, Paths.get(path, pid));

					Attachment attachment = new Attachment(pid, meta.getFileName(), meta.getSize(), meta.getType());
					attachment.setAttachedItem(i);
					if (description != null) {
						attachment.setDescription(description);
					}
					em.persist(attachment);
					em.persist(i);
					em.flush();
					em.refresh(i);
					return i;
				}
			}
		} catch (IOException ex) {
			System.err.println("UNABLE TO SAVE PATH: " + Paths.get(path).getFileName());
			System.err.println("UNABLE TO SAVE ROOT: " + Paths.get(path).getRoot());
			System.err.println("UNABLE TO SAVE PARENT: " + Paths.get(path).getParent());
			Logger.getLogger(AttachmentBean.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}
	
	
	
	public boolean deleteAttachment() {
		return false;
	}
	
	
	
	public boolean attachToItem(Long itemid, Attachment a) {
		return false;
	}
	
	public boolean removeFromItem(Long itemid, Attachment a) {
		return false;
	}
	
	

}