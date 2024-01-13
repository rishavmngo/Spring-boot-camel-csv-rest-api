package com.rishavmngo.UserAssignment.routes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.camel.CamelContext;
import org.apache.camel.Configuration;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.MockEndpoints;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.rishavmngo.UserAssignment.CustomersUtils;
import com.rishavmngo.UserAssignment.domain.CustomerEntity;
import com.rishavmngo.UserAssignment.service.CustomerService;

@CamelSpringBootTest
@SpringBootTest
@EnableAutoConfiguration
@MockEndpoints("direct:*")
@ExtendWith(MockitoExtension.class)
// @DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class CustomerRoutesTest {

	private ProducerTemplate producerTemplate;

	@Autowired
	private CamelContext context;

	@MockBean
	private CustomerService customerService;

	@EndpointInject("mock:direct:getAll")
	private MockEndpoint mockGetAllEndpoints;

	@EndpointInject("mock:direct:deleteById")
	private MockEndpoint mockDeleteByIdEndpoints;

	@EndpointInject("mock:direct:deleteByFilename")
	private MockEndpoint mockDeleteByFilenameEndpoints;

	@Configuration
	static class TestConfig {
		RoutesBuilder route() {
			return new CustomerRoutes();
		}

	}

	@BeforeEach
	public void setup() {

		producerTemplate = context.createProducerTemplate();
		mockDeleteByFilenameEndpoints.reset();
		mockGetAllEndpoints.reset();
		mockDeleteByIdEndpoints.reset();

	}

	@Test
	public void testGetAllDirectRoute() {

		List<CustomerEntity> customers = Arrays.asList(CustomersUtils.createTestCustomerEntityA(),
				CustomersUtils.createTestCustomerEntityB());

		when(customerService.getAll()).thenReturn(customers);

		List<CustomerEntity> ret = producerTemplate.requestBody("direct:getAll", "", List.class);

		verify(customerService, times(1)).getAll();
		assertEquals(customers, ret);

	}

	@Test
	void testGetAllRouteForMessageCount() throws Exception {

		assertNotNull(context.hasEndpoint("mock:direct:getAll"));
		List<CustomerEntity> customers = Arrays.asList(CustomersUtils.createTestCustomerEntityA(),
				CustomersUtils.createTestCustomerEntityB());

		when(customerService.getAll()).thenReturn(customers);
		producerTemplate.sendBody("direct:getAll", null);
		producerTemplate.sendBody("direct:getAll", null);
		verify(customerService, times(2)).getAll();
		mockGetAllEndpoints.expectedMessageCount(2);
		mockGetAllEndpoints.assertIsSatisfied();

	}

	@Test
	void testDeleteByIdDirectRoute() throws Exception {

		CustomerEntity expectedCustomer = CustomersUtils.createTestCustomerEntityA();

		when(customerService.deleteById(expectedCustomer.getId())).thenReturn(Optional.ofNullable(expectedCustomer));

		Exchange response = producerTemplate.request("direct:deleteById", exchange -> {
			exchange.getIn().setHeader("id", expectedCustomer.getId());
		});

		verify(customerService).deleteById(anyLong());

		CustomerEntity customer = response.getIn().getBody(CustomerEntity.class);

		assertEquals(expectedCustomer, customer);
	}

	@Test
	void testDeleteByIdDirectRouteMessageCount() throws Exception {

		assertNotNull(context.hasEndpoint("mock:direct:deleteById"));

		producerTemplate.request("direct:deleteById", exchange -> {
			exchange.getIn().setHeader("id", 1);
		});

		mockDeleteByIdEndpoints.expectedMessageCount(1);
		mockDeleteByIdEndpoints.assertIsSatisfied();
	}

	@Test
	void testDeleteByFilenameDirectRouteMessageCount() throws Exception {

		assertNotNull(context.hasEndpoint("mock:direct:deleteByFilename"));

		producerTemplate.request("direct:deleteByFilename", exchange -> {
			exchange.getIn().setHeader("filename", "first_batch");
		});
		mockDeleteByFilenameEndpoints.expectedMessageCount(1);
		mockDeleteByFilenameEndpoints.assertIsSatisfied();
	}

	@Test
	void testDeleteByFileNameDirectRouteForNoEntityFound() throws Exception {

		CustomerEntity expectedCustomer = CustomersUtils.createTestCustomerEntityA();
		String ExpectedMessage = "No items with filename: " + expectedCustomer.getFileName() + " exist";

		when(customerService.deleteByFilename(expectedCustomer.getFileName())).thenReturn(0);

		Exchange response = producerTemplate.request("direct:deleteByFilename", exchange -> {
			exchange.getIn().setHeader("filename", expectedCustomer.getFileName());
		});

		verify(customerService).deleteByFilename(anyString());

		String message = response.getIn().getBody(String.class);

		assertEquals(ExpectedMessage, message);
	}

	@Test
	void testDeleteByFileNameDirectRouteForEntitiesFound() throws Exception {

		CustomerEntity expectedCustomer = CustomersUtils.createTestCustomerEntityA();
		int size = 1;
		String ExpectedMessage = size + " items" + " deleted";

		when(customerService.deleteByFilename(expectedCustomer.getFileName())).thenReturn(size);

		Exchange response = producerTemplate.request("direct:deleteByFilename", exchange -> {
			exchange.getIn().setHeader("filename", expectedCustomer.getFileName());
		});

		verify(customerService).deleteByFilename(anyString());

		String message = response.getIn().getBody(String.class);

		assertEquals(ExpectedMessage, message);
	}

}
