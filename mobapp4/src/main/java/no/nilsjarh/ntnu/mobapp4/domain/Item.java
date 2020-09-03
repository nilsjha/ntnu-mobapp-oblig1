/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.nilsjarh.ntnu.mobapp4.domain;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import jdk.internal.jline.internal.Nullable;

/**
 *
 * @author nils
 */
@Entity(name = "items")
@NamedQueries({
	@NamedQuery(name = Item.FIND_ALL_ITEMS, query = "SELECT i FROM items i")})
public class Item implements Serializable {
	
	public final static String FIND_ALL_ITEMS = "findAllItems";
	
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
	@Temporal(javax.persistence.TemporalType.DATE)
	private Date createdTime;
	
	@Column(name = "publish_time")
	@Temporal(javax.persistence.TemporalType.DATE)
	private Date publishTime;
	
	@Future
	@Column(name = "expire_time")
	@Temporal(javax.persistence.TemporalType.DATE)
	private Date expireTime;
	
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
		this.createdTime = new Date();
		
	}
	
	public Item(String title, String descr, Person seller) {
		this.createdTime = new Date();
		this.title = title;
		this.description = descr;
		this.sellerPerson = seller;
	}

	public Long getId() {
		return id;
	}

	public Date getCreatedTime() {
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

	public Date getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(Date publishTime) {
		this.publishTime = publishTime;
	}

	public Date getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(Date expireTime) {
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
