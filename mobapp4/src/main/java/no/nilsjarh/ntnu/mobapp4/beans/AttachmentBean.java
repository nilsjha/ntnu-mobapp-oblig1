/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.nilsjarh.ntnu.mobapp4.beans;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.sql.DataSource;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.StreamingOutput;
import lombok.extern.java.Log;
import net.coobird.thumbnailator.Thumbnails;
import no.nilsjarh.ntnu.mobapp4.domain.*;
import no.nilsjarh.ntnu.mobapp4.resources.DatasourceProducer;
import org.eclipse.microprofile.config.inject.ConfigProperty;
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

	/**
	 * path to store photos
	 */
	@Inject
	@ConfigProperty(name = "photo.storage.path", defaultValue = "photos")
	String photoPath;

	private String getPhotoPath() {
		return photoPath;
	}

	@Inject
	UserBean ub;

	@Inject
	ItemBean ib;

	@Context
	SecurityContext sc;

	public Attachment getAttachment(String uid) {
		System.out.println("=== EJB-ATTACHMENT: GET ATTACHMENT ===");
		System.out.print("Query parameters: id:" + uid);
		if (uid == null) {
			return null;
		}
		Attachment found = em.find(Attachment.class, uid);
		if (found == null) {
			return null;
		}
		em.refresh(found);
		System.out.println("- Status.........: " + "In database");
		System.out.println("- Id.............: " + found.getId());
		return found;
	}

	/**
	 *
	 * @param i The Item to attach the attachment to
	 * @param multiPart MultiPartForm object to retreive the attachment
	 * @param path Folder path to store the attachment
	 * @param description A description of the item (optional)
	 * @return
	 */
	public Item uploadAttachment(Item i, FormDataMultiPart multiPart, String description) {
		String path = photoPath;
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
					long size = Files.copy(is, Paths.get(path, pid));

					Attachment attachment = new Attachment(pid, meta.getFileName(), size, meta.getType());
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

	public StreamingOutput streamAttachment(String id, int width) {
		System.out.println("=== EJB-ATTACHMENT: STREAM ===");
		String path = photoPath;
		try {
			if (getAttachment(id) != null) {
				StreamingOutput result = (OutputStream os) -> {
					Path image = Paths.get(path, id);
					if (width == 0) {
						Files.copy(image, os);
						os.flush();
						System.out.println("- Status.........: " + "Streamed native");
					} else {
						Thumbnails.of(image.toFile())
							.size(width, width)
							.outputFormat("jpeg")
							.toOutputStream(os);

						System.out.println("- Status.........: " + "Streamed thumb " + width + " px");
					}
				};

				return result;
			} else {
				System.out.println("- Status.........: " + "Not found, returning null");
				return null;
			}

		} catch (Exception e) {

			System.out.println("- Status.........: " + "Exception, returning null");
			System.err.print(e);
			return null;
		}
	}

	private boolean deleteFromDisk(String id) {
		System.out.println("=== EJB-ATTACHMENT: DELETE FROM DISK ===");
		Attachment toDelete = getAttachment(id);
		try {
			if (toDelete != null) {

				Path storedPath = Paths.get(photoPath, id);
				if (Files.deleteIfExists(storedPath)) {
					System.out.println("=== EJB-ATTACHMENT: DELETE FROM DISK ===");
					System.out.println("- Status.........: " + "DELETED");
					return true;
				} else {
					System.out.println("=== EJB-ATTACHMENT: DELETE FROM DISK ===");
					System.out.println("- Status.........: " + "PATH DOESNT EXSIST");
					System.out.println("- Path:.........: " + storedPath);

				}
			} else {
				System.out.println("=== EJB-ATTACHMENT: DELETE FROM DISK ===");
				System.out.println("- Status.........: " + "ERROR, TODELETE IS NULL");

			}
		} catch (Exception e) {
			System.out.println("=== EJB-ATTACHMENT: DELETE FROM DISK ===");
			System.out.println("- Status.........: " + "ERROR");
			System.err.println(e);
		}
		return false;
	}

	public boolean removeAllFromItem(Item i) {
		System.out.println("=== EJB-ATTACHMENT: REMOVE-ALL FROM ITEM ===");
		try {
			if (i != null) {
				em.refresh(i);
				Query q = em.createNamedQuery(Attachment.FIND_ATTACHMENTS_BY_ITEM);
				q.setParameter(1, i.getId());
				List<String> remlist = new ArrayList<>();
				List<Attachment> attList = q.getResultList();

				if (attList.isEmpty()) {
					System.out.println("- Status.........: " + "SKIPPED, NO ATTACHMEMTS");
					return true;
				}


				for (Attachment a : attList) {
					em.refresh(a);
					remlist.add(a.getId());
					System.out.println("- Found ID.......: " + a.getId());
					if (deleteFromDisk(a.getId())) {
						System.out.println("- Status.........: " + "REMOVED FROM DISK");
						em.remove(a);
					} else {

						System.out.println("- Status.........: " + "UNABLE TO REMOVE FROM DISK");
					}
				}
				System.out.println("- Removed........: " + remlist.size() + " attachment(s)");
				em.refresh(i);
				return true;

			}
		} catch (Exception e) {
			System.out.println("=== EJB-ATTACHMENT: REMOVE-ALL FROM ITEM ===");
			System.out.println("- Status.........: " + "ERROR, EXCEPTION");
			System.err.println(e);
		}
		return false;
	}

}
