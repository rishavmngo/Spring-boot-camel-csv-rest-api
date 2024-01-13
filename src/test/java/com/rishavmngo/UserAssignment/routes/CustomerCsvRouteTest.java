package com.rishavmngo.UserAssignment.routes;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import org.apache.camel.Configuration;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.MockEndpoints;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.rishavmngo.UserAssignment.CustomersUtils;
import com.rishavmngo.UserAssignment.domain.CustomerEntity;
import com.rishavmngo.UserAssignment.service.CustomerService;

public class CustomerCsvRouteTest extends CamelTestSupport {

	@Override
	protected RoutesBuilder createRouteBuilder() {
		return new CustomerCsvRoutes();
	}

	public void testProcessCsvFile() throws Exception {

		String file = "customerId,firstName,lastName,email\n1,Rishav,Raj,rishavinmngo@gmail.com\n2,Anurag,Sir,auragsir@gmail.com";

		MockEndpoint mock = getMockEndpoint("mock:result");
		mock.expectedMessageCount(1);

		mock.assertIsSatisfied();

	}
}
