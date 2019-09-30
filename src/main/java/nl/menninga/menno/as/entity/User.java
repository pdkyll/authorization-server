package nl.menninga.menno.as.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.PreRemove;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

@Entity
@Table(name = "user", schema = "public")
@SequenceGenerator(name = "idgen", sequenceName = "user_id_seq", initialValue = 1, allocationSize = 1)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User extends AuditingBaseEntity implements UserDetails{

	private static final long serialVersionUID = 6515703392118060645L;
	
	@Column(unique=true)
	private String email;
	@Column(unique=true)
	private String username;
	@JsonProperty(access = Access.WRITE_ONLY)
	private String password;
	private boolean accountNonExpired;
	private boolean accountNonLocked;
	private boolean credentialsNonExpired;
	private boolean enabled;
	
	@ManyToMany(cascade = {CascadeType.REMOVE}, fetch = FetchType.EAGER)
	@JoinTable(
			name = "user_role",
			joinColumns = {@JoinColumn(name = "user_id")},
			inverseJoinColumns = {@JoinColumn(name = "role_id")}
	)
	private List<Role> roles;

	@Transient
	private Set<GrantedAuthority> authorities;
	
	public User() {}
	
	public User(String email, String username, String password, boolean accountNonExpired, boolean accountNonLocked, boolean credentialsNonExpired, boolean enabled) {
		this.email = email;
		this.username = username;
		this.password = password;
		this.accountNonExpired = accountNonExpired;
		this.accountNonLocked = accountNonLocked;
		this.credentialsNonExpired = credentialsNonExpired;
		this.enabled = enabled;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	@Override
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	@Override
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Override
	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}
	
	public void setAccountNonExpired(boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
	}
	
	@Override
	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}
	
	public void setAccountNonLocked(boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}
	
	@Override
	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}
	
	public void setCredentialsNonExpired(boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
	}
	
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
	
	public void addRoles(Role ... roles) {
		if(this.roles == null) {
			this.roles = new ArrayList<>();
		}
		this.roles.addAll(Arrays.asList(roles));
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		if(authorities == null) {
			authorities = new HashSet<GrantedAuthority>();
		}
		roles.forEach(r -> {
			authorities.add(new SimpleGrantedAuthority(r.getRole()));
		});
		return authorities;
	}
	
	public void setAuthorities(Collection<?> authorities) {
		this.authorities = new HashSet<GrantedAuthority>();
		authorities.forEach(r -> {
			if(r instanceof String) {
				this.authorities.add(new SimpleGrantedAuthority((String)r));
			}else if(r instanceof Role){
				this.authorities.add(new SimpleGrantedAuthority(((Role)r).getRole()));
			}else if(r instanceof GrantedAuthority) {
				this.authorities.add((GrantedAuthority)r);
			}
		});
	}
	
	@PreRemove
	public void clearRoles() {
		this.roles.clear();
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder
			.append("User object: \n")
			.append("id: ")
			.append(getId())
			.append(" | email: ")
			.append(getEmail())
			.append(" | username: ")
			.append(getUsername())
			.append(" | password: ")
			.append(getPassword())
			.append(" | accountNonExpired ")
			.append(isAccountNonExpired())
			.append(" | accountNonLocked ")
			.append(isAccountNonLocked())
			.append(" | credentialsNonExpired ")
			.append(isCredentialsNonExpired())
			.append(" | enabled ")
			.append(isEnabled());
		if(getRoles() != null && !getRoles().isEmpty()) {
			builder.append(" | roles: [");
			builder.append(String.join(", ", getRoles().stream().map(role -> role.getRole()).collect(Collectors.toList())));
			builder.append("]");
		}
		return builder.toString();
	}
}