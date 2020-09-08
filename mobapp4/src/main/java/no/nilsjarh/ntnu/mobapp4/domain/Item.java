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
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 *
 * @author nils
 */
@Entity(name = "items")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false, exclude={"sellerUser"})
@NamedQueries({
	@NamedQuery(name = Item.FIND_ALL_ITEMS, query = "SELECT i FROM items i"),
	@NamedQuery(name = Item.FIND_ITEMS_BY_USER,
		query = "SELECT i FROM items i WHERE i.sellerUser.id LIKE :seller")
})
public class Item implements Serializable {

	public final static String FIND_ALL_ITEMS = "findAllItems";
	public final static String FIND_ITEMS_BY_USER = "findItemsByUser";

	@Id
	@Column(name = "id", nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotEmpty
	@Column(name = "title")
	private String title;

	@NotNull
	@Positive
	@Column(name = "price_nok")
	private BigDecimal priceNok;

	@Column(name = "description")
	private String description;

	@Column(name = "created_time", nullable = false)
	@Temporal(javax.persistence.TemporalType.DATE)
	private Date createdDate;

	@PrePersist
	protected void onCreate() {
		this.createdDate = new Date();
	}

	@Column(name = "publish_date")
	@Temporal(javax.persistence.TemporalType.DATE)
	private Date publishDate;

	@Future
	@Column(name = "expire_date")
	@Temporal(javax.persistence.TemporalType.DATE)
	private Date expireDate;

	/**
	 * OWNER SIDE *
	 */
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
	@JoinColumn(name = "seller_user_id", referencedColumnName = "id",
		nullable = false)
	private User sellerUser;

	/**
	 * OWNER SIDE *
	 */
	@OneToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "purchase_id", referencedColumnName = "id")
	private Purchase purchase;

	public Item(User owner, String title, BigDecimal priceNok) {
		this.sellerUser = owner;
		this.title = title;
		this.priceNok = priceNok;
	}
	 
}
