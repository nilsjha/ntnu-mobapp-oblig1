/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.nilsjarh.ntnu.mobapp4.domain;

import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import javax.persistence.CascadeType;
import javax.persistence.Column;
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
	@Column(name = "id")
	@GeneratedValue (strategy = GenerationType.AUTO)
	private Long id;
	
	@NotNull
	@Column(name = "purchase_time")
	private Timestamp purchaseTime;
	
	/** OWNING SIDE **/
	@ManyToOne(fetch = FetchType.LAZY,  cascade = CascadeType.DETACH)
	@JoinColumn(name = "buyer_person_id", referencedColumnName = "id",
		nullable = false)
	@NotNull
	private User buyerPerson;
	
	
	/** REFERNENCING SIDE **/
	@OneToOne(mappedBy = "purchase")
	private Item item;
	
	public Purchase() {
		this.purchaseTime = Timestamp.from(Instant.MIN);
	}
	
	public Purchase(User buyerPerson) {
		this.purchaseTime = Timestamp.from(Instant.MIN);
		this.buyerPerson = buyerPerson;
	}

	public Long getId() {
		return id;
	}

	public Timestamp getPurchaseTime() {
		return purchaseTime;
	}
	

	public User getBuyerPerson() {
		return buyerPerson;
	}

	public void setBuyerPerson(User buyerPerson) {
		this.buyerPerson = buyerPerson;
	}

	public Item getItem() {
		return item;
	}
	
	
	
	
}
