package com.rishavmngo.UserAssignment.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.rishavmngo.UserAssignment.domain.CustomerEntity;

import jakarta.transaction.Transactional;

@Repository
public interface CustomerRepository extends CrudRepository<CustomerEntity, Long> {

	@Query("SELECT c FROM CustomerEntity c WHERE c.fileName = :filename")
	Iterable<CustomerEntity> findByFilename(String filename);

	@Modifying
	@Transactional
	@Query("DELETE FROM CustomerEntity c WHERE c.fileName = :filename")
	void deleteByFilename(String filename);
}
