/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.nilsjarh.ntnu.mobapp4.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
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
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import jdk.internal.jline.internal.Nullable;

/**
 *
 * @author nils
 */
@Entity(name = "items")
public class Item implements Serializable {
	@Id
	@Column(name = "id", nullable = false)
	@GeneratedValue (strategy = GenerationType.AUTO)
	private Long id;
	
	@NotNull
	@Column(name = "title")
	private String title;
		
	@NotNull
	@Column(name = "description")
	private String description;
	
	@NotNull
	@Column(name = "created_time", nullable = false)
	private Timestamp createdTime;
	
	@Column(name = "publish_time")
	private Timestamp publishTime;
	
	@Future
	@Column(name = "expire_time")
	private Timestamp expireTime;
	
	@Positive
	@Column(name = "price_nok")
	private BigDecimal priceNok;


	/** OWNER SIDE **/
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
	@JoinColumn(name = "seller_person_id", referencedColumnName = "id", 
		nullable = false)
	@NotNull
	private Person sellerPerson;
	
	
	/** OWNER SIDE **/
	@OneToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "purchase_id", referencedColumnName = "id")
	private Purchase purchase;
	
	public Item() {
		this.createdTime = Timestamp.from(Instant.MIN);
		
	}
	
	public Item(String title, String descr, Person seller) {
		this.createdTime = Timestamp.from(Instant.MIN);
		this.title = title;
		this.description = descr;
		this.sellerPerson = seller;
	}

	public Long getId() {
		return id;
	}

	public Timestamp getCreatedTime() {
		return createdTime;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Timestamp getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(Timestamp publishTime) {
		this.publishTime = publishTime;
	}

	public Timestamp getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(Timestamp expireTime) {
		this.expireTime = expireTime;
	}

	public BigDecimal getPriceNok() {
		return priceNok;
	}

	public void setPriceNok(BigDecimal priceNok) {
		this.priceNok = priceNok;
	}

	public Person getSellerPerson() {
		return sellerPerson;
	}

	public void setSellerPerson(Person sellerPerson) {
		this.sellerPerson = sellerPerson;
	}

	public Purchase getPurchase() {
		return purchase;
	}

	public void setPurchase(Purchase purchase) {
		this.purchase = purchase;
	}
	
	
}
