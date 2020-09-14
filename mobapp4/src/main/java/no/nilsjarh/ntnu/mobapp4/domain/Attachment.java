/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.nilsjarh.ntnu.mobapp4.domain;

import java.io.Serializable;
import javax.activation.MimeType;
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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 *
 * @author nils
 */
@Entity(name = "attachments")
@Data
@AllArgsConstructor
@NoArgsConstructor
@NamedQueries({
	@NamedQuery(name = Attachment.FIND_ATTACHMENTS_BY_ITEM,
		query = "SELECT a FROM attachments a WHERE a.attachedItem.id = ?1")
})
public class Attachment implements Serializable{
	public final static String FIND_ATTACHMENTS_BY_ITEM = "findAttachmentsByItem";

	@Id
	String id;

	@JsonbTransient
	String path;
	
	String description;


	@JsonbTransient
	long filesize;
	
	
	@JsonbTransient
	String mimeType;

	/**
	 * OWNING SIDE *
	 */
	@JsonbTransient
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
	@JoinColumn(name = "attached_item_id", referencedColumnName = "id",
		nullable = true)
	private Item attachedItem;
	
	public Attachment(String id, String filename, long size, String type) {
		this.id = id;
		this.path = filename;
		this.filesize = size;
		this.mimeType = type;
	}

}
