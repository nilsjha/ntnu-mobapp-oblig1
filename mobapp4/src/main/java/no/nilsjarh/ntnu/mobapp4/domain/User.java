/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.nilsjarh.ntnu.mobapp4.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.Version;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import no.ntnu.tollefsen.auth.Group;

/**
 *
 * @author nils
 */
@Entity(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false, exclude={"ownedItems","donePurchases"})
@NamedQueries({
	@NamedQuery(name = User.FIND_ALL_USERS,
		query = "SELECT p FROM users p ORDER BY p.firstName"),
	@NamedQuery(name = User.FIND_USER_BY_EMAIL,
		query = "SELECT p FROM users p WHERE p.email LIKE :email")
})
public class User implements Serializable {

	public final static String FIND_ALL_USERS = "User.findAllUsers";
	public final static String FIND_USER_BY_EMAIL = "User.findUserByEmail";

	public enum State {
		ENABLED, DISABLED
	}

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private String id;

	@Email
	@NotEmpty
	private String email;

	@JsonbTransient
	private String password;

	@Version
	Timestamp version;

	@Column(name = "created_date")
	@Temporal(javax.persistence.TemporalType.DATE)
	Date createdDate;

	@PrePersist
	protected void onCreate() {
		createdDate = new Date();
	}

	@Enumerated(EnumType.STRING)
	User.State currentState = User.State.ENABLED;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "mobile_phone")
	private String mobilePhone;

	/**
	 * CROSS-JOIN - OWNER *
	 */
	@ManyToMany
	@JoinTable(name = "user_has_group",
		joinColumns = @JoinColumn(
			name = "id",
			referencedColumnName = "id"),
		inverseJoinColumns = @JoinColumn(
			name = "name",
			referencedColumnName = "name"))
	List<Group> groups;

	/**
	 * REFERENCING SIDE *
	 */
	@JsonbTransient
	@Getter
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "buyerUser")
	private List<Purchase> donePurchases;

	/**
	 * REFERENCING SIDE *
	 */
	@JsonbTransient
	@Getter
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sellerUser")
	private List<Item> ownedItems;

	public List<Group> getGroups() {
		if (this.groups == null) {
			this.groups = new ArrayList<>();
		}
		return this.groups;

	}

}
