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

import nl.menninga.menno.as.entity.Role;
import nl.menninga.menno.as.service.RoleService;

@RestController
@CrossOrigin
public class RoleController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RoleController.class);

//	@Autowired
//	private SecurityUtil securityUtil;
	
	@Autowired
	private RoleService roleService;
	
	@GetMapping("api/roles")
    @PreAuthorize("hasRole('AS_ADMIN')")
    public ResponseEntity<List<Role>> roles(Principal principal) {
        List<Role> roleList = roleService.getAllRoles();
		return ResponseEntity.ok(roleList);
    }
	
	@GetMapping("api/role/{id}")
    public ResponseEntity<Role> roleGet(@PathVariable Long id) {
		Role role = null;
        try {
        	role = roleService.getRole(id);
		} catch (NotFoundException e) {
			LOGGER.info("Entity could not be found!", e);
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(role);
    }
	
	@PostMapping("api/role")
	@PreAuthorize("hasRole('AS_ADMIN')")
	public ResponseEntity<?> rolePost(Principal principal, @RequestBody Role role) {
		try {
			role = roleService.createRole(role);
		} catch (IllegalArgumentException e) {
			LOGGER.info("Entity could not be created!", e);
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e);
		}
		return ResponseEntity.ok(role);
	}
	
	@PutMapping("api/role")
	@PreAuthorize("hasRole('AS_ADMIN')")
	public ResponseEntity<?> rolePut(Principal principal, @RequestBody Role role) {
		try {
			role = roleService.updateRole(role);
		} catch (NotFoundException e) {
			LOGGER.info("Entity to update could not be found!", e);
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(role);
	}
	
	@DeleteMapping("api/role/{id}")
	@PreAuthorize("hasRole('AS_ADMIN')")
    public ResponseEntity<?> roleDelete(@PathVariable Long id) {
		try {
	        roleService.deleteRole(id);
		} catch (IllegalArgumentException e) {
			LOGGER.info("Entity could not be deleted!", e);
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e);
		} catch (NotFoundException e) {
			LOGGER.info("Entity to delete could not be found!", e);
			return ResponseEntity.notFound().build();
		}
        return ResponseEntity.ok("Role deleted.");
    }
}