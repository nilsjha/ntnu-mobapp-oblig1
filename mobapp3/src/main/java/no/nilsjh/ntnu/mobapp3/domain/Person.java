/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.nilsjh.ntnu.mobapp3.domain;

import java.io.Serializable;
import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

/**
 *
 * @author nils
 */

@Entity
public class Person implements Serializable{
    @Id @GeneratedValue
    Long personId;
    
    @NotNull
    String userName;
    
    @NotNull
    String email;
    
    @JsonbTransient
    String password;
    
    String mobilePhone;
    String firstName;
    String lastName;
    
    public void setName(String fullName) {
        String[] name;
        name = fullName.split(" ");
        
        if ((name[0].isEmpty()) || (name[1].isEmpty())) {
            // DO NOTHING
        } else {          
            this.firstName = name[0];
            this.lastName = name[1]; 
        }  
    }
}
