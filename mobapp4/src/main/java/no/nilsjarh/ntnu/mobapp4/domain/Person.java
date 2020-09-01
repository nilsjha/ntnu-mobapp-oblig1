/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.nilsjarh.ntnu.mobapp4.domain;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author nils
 */
public class Person {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String password;
    private String address;
    private String mobilePhone;
    private String firstName;
    private String lastName;


    public int getId() {
	    return this.id;
    }

    public String getEmail() {
	    return this.email;
    }

    public String getPassword() {
	    return this.password;
    }
    public String getAddress() {
	    return this.address;
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
    
    public void setId(int id) {
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