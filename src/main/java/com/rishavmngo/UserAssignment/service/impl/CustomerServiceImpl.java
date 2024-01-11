package com.rishavmngo.UserAssignment.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rishavmngo.UserAssignment.domain.CustomerEntity;
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

			List<CustomerEntity> customersSaved = StreamSupport
					.stream(customerRepository.saveAll(customers).spliterator(), false).collect(Collectors.toList());
			System.out.println(customersSaved.size() + " Customers inserted");
		} catch (Exception e) {

			throw new UniqueConstraintException("file not found", e);
		}

	}

	@Override
	public List<CustomerEntity> getAll() {
		List<CustomerEntity> customers = StreamSupport.stream(customerRepository.findAll().spliterator(), false)
				.collect(Collectors.toList());
		return customers;

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

		List<CustomerEntity> customers = StreamSupport
				.stream(customerRepository.findByFilename(filename).spliterator(), false).collect(Collectors.toList());
		customerRepository.deleteByFilename(filename);
		return customers.size();
	}

}
