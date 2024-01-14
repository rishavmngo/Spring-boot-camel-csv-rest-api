package com.rishavmngo.UserAssignment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rishavmngo.UserAssignment.CustomersUtils;
import com.rishavmngo.UserAssignment.domain.CustomerEntity;
import com.rishavmngo.UserAssignment.exceptions.BadRequestException;
import com.rishavmngo.UserAssignment.repository.CustomerRepository;
import com.rishavmngo.UserAssignment.service.impl.CustomerServiceImpl;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

	@Mock
	private CustomerRepository customerRepository;
	private CustomerService underTest;

	@BeforeEach
	void setup() {
		underTest = new CustomerServiceImpl(customerRepository);

	}

	@Test
	void canGetAllCustomers() {

		underTest.getAll();
		verify(customerRepository).findAll();

	}

	@Test
	void canAddCustomers() {
		CustomerEntity customer = CustomersUtils.createTestCustomerEntityA();

		// when
		underTest.addCustomer(customer);

		ArgumentCaptor<CustomerEntity> customerArgumentCaptor = ArgumentCaptor.forClass(CustomerEntity.class);

		verify(customerRepository).save(customerArgumentCaptor.capture());

		CustomerEntity capturedCustomer = customerArgumentCaptor.getValue();
		assertThat(capturedCustomer).isEqualTo(customer);

	}

	@Test
	void willThrowErrorWhenEmailIsTaken() {

		CustomerEntity customer = CustomersUtils.createTestCustomerEntityA();

		// make sure that the findByEmail return a customer
		given(customerRepository.findByEmail(anyString())).willReturn(Optional.ofNullable(customer));

		assertThatThrownBy(() -> underTest.addCustomer(customer)).isInstanceOf(BadRequestException.class)
				.hasMessageContaining("Email " + customer.getEmail() + " taken");

		verify(customerRepository, never()).save(any());

	}

	@Test
	void canDeleteCustomersById() {

		CustomerEntity customer = CustomersUtils.createTestCustomerEntityA();
		long id = customer.getId();
		given(customerRepository.findById(id)).willReturn(Optional.ofNullable((customer)));

		underTest.deleteById(id);

		verify(customerRepository).deleteById(id);
	}

	@Test
	void canDeleteCustomersByIdIfTest() {

		CustomerEntity customer = CustomersUtils.createTestCustomerEntityA();
		long id = customer.getId();
		given(customerRepository.findById(id)).willReturn(Optional.ofNullable((null)));

		underTest.deleteById(id);

		verify(customerRepository, times(0)).deleteById(id);
	}

	@Test
	void canDeleteCustomersByFileName() {

		CustomerEntity customer = CustomersUtils.createTestCustomerEntityA();
		String filename = customer.getFileName();

		int size = underTest.deleteByFilename(filename);

		verify(customerRepository).deleteByFilename(filename);

		assertThat(size).isEqualTo(0);

	}

}
