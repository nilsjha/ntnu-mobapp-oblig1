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
import javax.ws.rs.core.Response;
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

	
	public Item uploadAttachment(Long itemid, FormDataMultiPart multiPart, String path, String descr) {
		Item i = ib.getItem(itemid);
		
		List<FormDataBodyPart> images = multiPart.getFields("image");
			if (images != null && i != null) {
				for (FormDataBodyPart part : images) {
					try {
						InputStream is = part.getEntityAs(InputStream.class);
					ContentDisposition meta = part.getContentDisposition();

					String pid = UUID.randomUUID().toString();
					Files.copy(is, Paths.get(path, pid));

					Attachment attachment = new Attachment(pid, meta.getFileName(), meta.getSize(), meta.getType());
					if (descr != null) {
						attachment.setDescription(descr);
					}
					attachment.setAttachedItem(i);
					em.persist(attachment);
					em.flush();
					return i;
					} catch (IOException e) {
						System.err.println(e);
						return null;
					}
				}
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