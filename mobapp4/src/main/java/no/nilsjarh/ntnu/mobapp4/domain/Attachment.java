/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.nilsjarh.ntnu.mobapp4.domain;

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
import jdk.internal.jline.internal.Nullable;
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
public class Attachment {

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
	@Nullable
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
