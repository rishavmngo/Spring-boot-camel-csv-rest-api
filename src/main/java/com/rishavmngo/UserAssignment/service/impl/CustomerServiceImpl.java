package com.rishavmngo.UserAssignment.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rishavmngo.UserAssignment.domain.CustomerEntity;
import com.rishavmngo.UserAssignment.exceptions.BadRequestException;
import com.rishavmngo.UserAssignment.exceptions.UniqueConstraintException;
import com.rishavmngo.UserAssignment.repository.CustomerRepository;
import com.rishavmngo.UserAssignment.service.CustomerService;

@Service
public class CustomerServiceImpl implements CustomerService {
	@Autowired
	private CustomerRepository customerRepository;

	@Override
	public void SaveAll(List<CustomerEntity> customers) throws UniqueConstraintException {

		try {

			List<CustomerEntity> customersSaved = customerRepository.saveAll(customers);
			System.out.println(customersSaved.size() + " Customers inserted");
		} catch (Exception e) {

			throw new UniqueConstraintException("file not found", e);
		}

	}

	@Override
	public List<CustomerEntity> getAll() {
		return customerRepository.findAll();

	}

	@Override
	public Optional<CustomerEntity> deleteById(Long id) {
		Optional<CustomerEntity> optionalCustomer = customerRepository.findById(id);
		if (optionalCustomer.isPresent()) {
			customerRepository.deleteById(id);
		}
		return optionalCustomer;
	}

	@Override
	public int deleteByFilename(String filename) {

		List<CustomerEntity> customers = customerRepository.findByFilename(filename);
		customerRepository.deleteByFilename(filename);
		return customers.size();
	}

	@Override
	public void addCustomer(CustomerEntity customer) {
		Boolean emailExist = customerRepository.findByEmail(customer.getEmail()).isPresent();

		if (emailExist) {
			throw new BadRequestException(
					"Email " + customer.getEmail() + " taken");

		} else {

			customerRepository.save(customer);
		}

	}
}
