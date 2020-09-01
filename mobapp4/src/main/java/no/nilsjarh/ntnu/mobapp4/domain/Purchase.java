/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.nilsjarh.ntnu.mobapp4.domain;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

/**
 *
 * @author nils
 */
@Entity(name = "purchases")
public class Purchase implements Serializable {
	@Id
	@GeneratedValue (strategy = GenerationType.AUTO)
	private Long id;
	
	@NotNull
	private Date purchaseTime;
	
	/** OWNING SIDE **/
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY,  cascade = CascadeType.DETACH)
	@JoinColumn(name = "buyer_person_id", referencedColumnName = "id")
	private Person buyerPerson;
	
	
	/** REFERNENCING SIDE **/
	@OneToOne(mappedBy = "purchases")
	private Item item;
}
