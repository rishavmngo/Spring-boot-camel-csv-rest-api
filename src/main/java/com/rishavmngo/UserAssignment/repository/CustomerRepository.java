package com.rishavmngo.UserAssignment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.rishavmngo.UserAssignment.domain.CustomerEntity;

import jakarta.transaction.Transactional;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {

	@Query("SELECT c FROM CustomerEntity c WHERE c.fileName = :filename")
	List<CustomerEntity> findByFilename(String filename);

	@Modifying
	@Transactional
	@Query("DELETE FROM CustomerEntity c WHERE c.fileName = :filename")
	void deleteByFilename(String filename);

	Optional<CustomerEntity> findByEmail(String email);
}
