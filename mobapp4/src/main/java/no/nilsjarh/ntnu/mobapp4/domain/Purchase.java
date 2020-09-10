/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.nilsjarh.ntnu.mobapp4.domain;

import java.io.Serializable;
import java.util.Date;
import javax.json.bind.annotation.JsonbTransient;
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
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 *
 * @author nils
 */
@Entity(name = "purchases")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode( exclude="item")
@NamedQueries({
	@NamedQuery(name = Purchase.FIND_ALL_PURCHASES, query = "SELECT p FROM purchases p"),
	@NamedQuery(name = Purchase.FIND_PURCHASES_BY_USER,
		query = "SELECT p FROM purchases p WHERE p.buyerUser.id LIKE :buyer")
})
public class Purchase implements Serializable {

	public final static String FIND_ALL_PURCHASES = "findAllPurchases";
	public final static String FIND_PURCHASES_BY_USER = "findPurchasesByUser";

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "purchase_time")
	private Date purchaseDate;

	@PrePersist
	protected void onCreate() {
		this.purchaseDate = new Date();
	}

	/**
	 * OWNING SIDE *
	 */
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
	@JoinColumn(name = "buyer_user_id", referencedColumnName = "id",
		nullable = false)

	private User buyerUser;

	/**
	 * REFERNENCING SIDE *
	 */
	@JsonbTransient
	@Getter
	@OneToOne(mappedBy = "purchase")
	private Item item;
	
	
	public Purchase(User buyer) {
		this.buyerUser = buyer;
	}

}
