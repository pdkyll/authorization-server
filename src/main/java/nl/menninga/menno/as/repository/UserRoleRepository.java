package nl.menninga.menno.as.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nl.menninga.menno.as.entity.UserRole;

@Repository
@Transactional
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
	void deleteByUserId(Long userId);
	void deleteByRoleId(Long roleId);
}