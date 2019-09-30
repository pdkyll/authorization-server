package nl.menninga.menno.as.service;

import java.util.List;
import java.util.Objects;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

import nl.menninga.menno.as.entity.Role;
import nl.menninga.menno.as.repository.RoleRepository;
import nl.menninga.menno.as.repository.UserRoleRepository;

@Service
public class RoleService {
	
	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserRoleRepository userRoleRepository;
	
	public List<Role> getAllRoles(){
		return roleRepository.findAll();
	}
	
	public Role getRole(Long id) throws NotFoundException {
		Role role = roleRepository.getOne(id);
		if(Objects.isNull(role)) {
			throw new NotFoundException();
		}
		return role;
	}

	@Transactional
	public void deleteRole(Long id) throws NotFoundException {
		Role role = roleRepository.getOne(id);
		if(Objects.isNull(role)) {
			throw new NotFoundException();
		}
		userRoleRepository.deleteByRoleId(role.getId());
		roleRepository.delete(role);
	}

	public Role createRole(Role role) throws IllegalArgumentException {
		return roleRepository.saveAndFlush(role);
	}

	public Role updateRole(Role role) throws NotFoundException {
		Role origRole = roleRepository.getOne(role.getId());
		if(Objects.isNull(origRole)) {
			throw new NotFoundException();
		}
		return roleRepository.saveAndFlush(role);
	}
}
