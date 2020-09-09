/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.nilsjarh.ntnu.mobapp4.beans;

import java.util.List;
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
import lombok.Getter;
import lombok.extern.java.Log;
import no.nilsjarh.ntnu.mobapp4.resources.DatasourceProducer;

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

	
	public boolean uploadAttachment() {
		return false;
	}
	
	public boolean attachItemToAttachment() {
		return false;
	}
	
	public boolean removeItemFromAttachment() {
		return false;
	}
	
	

}