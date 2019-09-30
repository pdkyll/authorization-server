package nl.menninga.menno.as.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "user_role", schema = "public")
@SequenceGenerator(name = "userroleidgen", sequenceName = "user_role_id_seq", initialValue = 1, allocationSize = 1)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class UserRole implements Serializable{

	private static final long serialVersionUID = 3957150256284237086L;

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userroleidgen")    
    private Long id;
	private Long userId;
	private Long roleId;
	
	public UserRole() {}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}
}