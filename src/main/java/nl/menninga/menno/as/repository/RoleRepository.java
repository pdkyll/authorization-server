package nl.menninga.menno.as.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nl.menninga.menno.as.entity.Role;
import nl.menninga.menno.as.entity.User;

@Repository
@Transactional
public interface RoleRepository extends JpaRepository<Role, Long> {

	User findByRole(String role);
}