package no.ntnu.tollefsen.auth;

import java.io.Serializable;
import java.util.List;
import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import no.nilsjarh.ntnu.mobapp4.domain.User;

/**
 *
 * @author mikael
 */
@Entity
@Table(name = "groups")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = "users")
public class Group implements Serializable {

	public static final String USER = "user";
	public static final String ADMIN = "admin";
	public static final String[] GROUPS = {USER, ADMIN};

	@Id
	String name;

	String project;

	/**
	 * CROSS-JOIN - REFERENCE *
	 */
	
	@JsonbTransient
	@Getter
	@ManyToMany
	@JoinTable(name = "user_has_group",
		joinColumns = @JoinColumn(
			name = "name",
			referencedColumnName = "name"),
		inverseJoinColumns = @JoinColumn(
			name = "id",
			referencedColumnName = "id"))
	List<User> users;

	public Group(String name) {
		this.name = name;
	}
}
