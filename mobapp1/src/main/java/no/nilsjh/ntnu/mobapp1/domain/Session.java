/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.nilsjh.ntnu.mobapp1.domain;

import java.io.Serializable;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

/**
 *
 * @author nils
 */
public class Session implements Serializable{
    @Id @GeneratedValue
    Long sessionId;
    
    @NotNull
    String establishedTime;
    
    @NotNull
    String expireTime;
    
    
    // Sett inn frammandnokkel for person-tabell her
    
    String authToken;
}
