package com.outsourceowl.repository;

import com.outsourceowl.model.UserRole;
import com.outsourceowl.model.constants.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<UserRole, Long> {
  Optional<UserRole> findByRoleType(RoleType roleType);
}
