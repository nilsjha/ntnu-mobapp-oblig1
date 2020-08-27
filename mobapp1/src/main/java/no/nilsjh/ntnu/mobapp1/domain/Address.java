/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.nilsjh.ntnu.mobapp1.domain;

import java.io.Serializable;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 *
 * @author nils
 */
public class Address implements Serializable {
    @Id @GeneratedValue
    Long addressId;
    
    String streetName;
    int streetNumber;
    int postalCode;
    String country;
    
}
