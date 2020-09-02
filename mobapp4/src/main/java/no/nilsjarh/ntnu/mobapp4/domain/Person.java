/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.nilsjarh.ntnu.mobapp4.domain;

import java.io.Serializable;
import java.util.List;
import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

/**
 *
 * @author nils
 */
@Entity(name = "persons")
public class Person implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue (strategy = GenerationType.AUTO)
    private Long id;
    
    @Email
    @NotNull
    @Column(name = "email")
    private String email;
    
    @JsonbTransient
    @Column(name = "password")
    private String password;
    
    @Column(name = "first_name")
    private String firstName;
    
    @Column(name = "last_name")
    private String lastName;
    
    @Column(name = "mobile_phone")
    private String mobilePhone;
    
    /** REFERENCING SIDE **/
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "buyerPerson")
    private List<Purchase> donePurchases;
    
    /** REFERENCING SIDE **/
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "sellerPerson")
    private List<Item> ownedItems;

    public Person() {
	    
    }

    public Person(String email, String firstName, String lastName, String phone) {
	    this.email = email;
	    this.firstName = firstName;
	    this.lastName = lastName;
	    this.mobilePhone = phone;
    }

	public Long getId() {
		return id;
	}
    

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}
    
    
}