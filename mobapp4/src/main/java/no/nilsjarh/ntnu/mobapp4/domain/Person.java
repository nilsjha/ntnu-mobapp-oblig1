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
    @Column(name = "email")
    private String email;
    
    @JsonbTransient
    @Column(name = "password")
    private String password;
    
    @Column(name = "mobile_phone")
    private String mobilePhone;
    
    @Column(name = "first_name")
    private String firstName;
    
    @Column(name = "last_name")
    private String lastName;
    
    /** REFERENCING SIDE **/
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "id")
    private List<Purchase> donePurchases;
    
    /** REFERENCING SIDE **/
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "id")
    private List<Item> ownedItems;


    public Long getId() {
	    return this.id;
    }

    public String getEmail() {
	    return this.email;
    }

    public String getPassword() {
	    return this.password;
    }

    public String getMobile() {
	    return this.mobilePhone;
    }
    public String getFirstName() {
	    return this.firstName;
    }
    public String getLastName() {
	    return this.lastName;
    }
    
    public void setId(Long id) {
	    this.id = id;
    }
    
    public void setEmail(String email) {
	    this.email = email;
    }
    
    public void setPassword(String password) {
	    this.password = password;
    }
    
    public void setMobile(String number) {
	    this.mobilePhone = number;
    }
    
    public void setFirstName(String name) {
	    this.firstName = name;
    }
    
   public void setLastName(String name) {
	  this.lastName = name;
   }
   
}