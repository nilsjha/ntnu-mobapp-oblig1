/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.nilsjh.ntnu.mobapp3.domain;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalTime;

/**
 *
 * @author nils
 */
public class Item implements Serializable {
    @Id @GeneratedValue
    Long itemId;

    @NotNull
    LocalTime createdTime;
    LocalTime publishTime;
    LocalTime expireTime;

    Float priceNok;
}
