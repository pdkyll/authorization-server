package nl.menninga.menno.as.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import nl.menninga.menno.as.entity.User;

@Component
public class SecurityUtil {


	/**
	 * Method for requesting roles of logged in user.
	 * 
	 * @param role {@link String}
	 * @return {@link boolean}
	 */
	public boolean hasRole(String role) {
		role = "ROLE_" + role;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean hasRole = false;
		for (GrantedAuthority authority : authentication.getAuthorities()) {
			hasRole = authority.getAuthority().equals(role);
			if (hasRole) {
				break;
			}
		}
		return hasRole;
	}
	
	/**
	 * Method for matching user against logged in user.
	 * 
	 * @param user {@link User}
	 * @return {@link boolean}
	 */
	public boolean matchLoggedInUser(User user) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object loggedInUser = authentication.getPrincipal();
		return loggedInUser instanceof User && user.getId().equals(((User)loggedInUser).getId());
	}

}
