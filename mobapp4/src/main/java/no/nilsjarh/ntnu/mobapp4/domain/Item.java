/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.nilsjarh.ntnu.mobapp4.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
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
	@Column(name = "created_time", nullable = false)
	private Date createdTime;
	
	@Column(name = "publish_time")
	private Date publishTime;
	
	@Future
	@Column(name = "expire_time")
	private Date expireTime;
	
	@Positive
	@Column(name = "price_nok")
	private BigDecimal priceNok;
	
	@NotNull
	@Column(name = "description")
	private String description;

	/** REFERENCING SIDE **/
	@OneToOne(mappedBy = "item")
	@JoinColumn(name = "purchase_id", referencedColumnName = "id")
	private Purchase purchase;
	
	/** OWNER SIDE **/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "person_id")
	private Person sellerPerson;
	
	
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
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
	
	
	
	
}
