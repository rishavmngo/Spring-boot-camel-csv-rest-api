package com.rishavmngo.UserAssignment.service;

import java.util.List;
import java.util.Optional;

import com.rishavmngo.UserAssignment.domain.CustomerEntity;
import com.rishavmngo.UserAssignment.exceptions.UniqueConstraintException;

public interface CustomerService {

	void SaveAll(List<CustomerEntity> customers) throws UniqueConstraintException;

	List<CustomerEntity> getAll();

	Optional<CustomerEntity> deleteById(Long id);

	int deleteByFilename(String filename);

}
