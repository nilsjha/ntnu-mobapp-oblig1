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
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 *
 * @author nils
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "purchases")
public class Purchase implements Serializable {
	@Id
	@Column(name = "id")
	@GeneratedValue (strategy = GenerationType.AUTO)
	private Long id;
	
	@NotEmpty
	@Column(name = "purchase_time")
	private Date purchaseDate;
	
	@PrePersist
	protected void onCreate() {
		this.purchaseDate = new Date();
	}
	
	/** OWNING SIDE **/
	@ManyToOne(fetch = FetchType.LAZY,  cascade = CascadeType.DETACH)
	@JoinColumn(name = "buyer_user_id", referencedColumnName = "id",
		nullable = false)
	@NotEmpty
	private User buyerUser;
	
	
	/** REFERNENCING SIDE **/
	@JsonbTransient
	@Getter
	@OneToOne(mappedBy = "purchase")
	private Item item;	
	
}
