package com.rishavmngo.UserAssignment.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.rishavmngo.UserAssignment.domain.CustomerEntity;
import com.rishavmngo.UserAssignment.exceptions.UniqueConstraintException;

public interface CustomerService {

	void SaveAll(List<CustomerEntity> customers) throws UniqueConstraintException;

	List<CustomerEntity> getAll();

}
