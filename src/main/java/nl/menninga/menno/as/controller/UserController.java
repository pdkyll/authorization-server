package nl.menninga.menno.as.controller;

import java.security.Principal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import nl.menninga.menno.as.entity.User;
import nl.menninga.menno.as.service.ASUserDetailService;
import nl.menninga.menno.as.util.SecurityUtil;

@RestController
@CrossOrigin
public class UserController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private SecurityUtil securityUtil;
	
	@Autowired
	private ASUserDetailService userDetailsService;
	
	@GetMapping("api/users")
    @PreAuthorize("hasRole('AS_ADMIN')")
    public ResponseEntity<List<User>> users(Principal principal) {
        List<User> userList = userDetailsService.getAllUsers();
		return ResponseEntity.ok(userList);
    }
	
	@GetMapping("api/user/{id}")
    public ResponseEntity<User> userGet(@PathVariable Long id) {
		User user = null;
        try {
        	user = userDetailsService.getUser(id);
		} catch (NotFoundException e) {
			LOGGER.info("Entity could not be found!", e);
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(user);
    }
	
	@PostMapping("api/user")
	@PreAuthorize("hasRole('AS_ADMIN')")
	public ResponseEntity<?> userPost(Principal principal, @RequestBody User user) {
		try {
			user = userDetailsService.createUser(user);
		} catch (IllegalArgumentException e) {
			LOGGER.info("Entity could not be created!", e);
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e);
		}
		return ResponseEntity.ok(user);
	}
	
	@PutMapping("api/user")
	@PreAuthorize("hasAnyRole('AS_ADMIN', 'AS_USER')")
	public ResponseEntity<?> userPut(Principal principal, @RequestBody User user) {
		try {
			if(securityUtil.hasRole("AS_ADMIN") || (securityUtil.hasRole("AS_USER") && securityUtil.matchLoggedInUser(user))) {
				user = userDetailsService.updateUser(user);
			}
		} catch (NotFoundException e) {
			LOGGER.info("Entity to update could not be found!", e);
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(user);
	}
	
	@DeleteMapping("api/user/{id}")
	@PreAuthorize("hasRole('AS_ADMIN')")
    public ResponseEntity<?> userDelete(@PathVariable Long id) {
		try {
	        userDetailsService.deleteUser(id);
		} catch (IllegalArgumentException e) {
			LOGGER.info("Entity could not be deleted!", e);
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e);
		} catch (NotFoundException e) {
			LOGGER.info("Entity to delete could not be found!", e);
			return ResponseEntity.notFound().build();
		}
        return ResponseEntity.ok("User deleted.");
    }
}