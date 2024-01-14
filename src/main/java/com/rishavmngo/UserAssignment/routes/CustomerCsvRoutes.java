package com.rishavmngo.UserAssignment.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.spi.DataFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rishavmngo.UserAssignment.domain.CustomerEntity;
import com.rishavmngo.UserAssignment.service.CustomerService;

@Component
public class CustomerCsvRoutes extends RouteBuilder {

	@Autowired
	private CustomerService customerService;

	@Override
	public void configure() throws Exception {
		DataFormat bind = new BindyCsvDataFormat(CustomerEntity.class);

		// from("file:data/inbox?move=.done&moveFailed=.failed")
		// .setHeader("CamelFileName", simple("${file:name.noext}"))
		// .unmarshal(bind)
		// .process(exchange -> {
		// // Access the unmarshalled data
		// List<CustomerEntity> customers = exchange.getIn().getBody(List.class);
		//
		// // Set the filename for each customer
		// String filename = exchange.getIn().getHeader("CamelFileName", String.class);
		// for (CustomerEntity customer : customers) {
		// customer.setFileName(filename);
		// }
		//
		// // Replace the body with the modified data
		// exchange.getIn().setBody(customers);
		// })
		// .bean(customerService, "SaveAll")
		// .end();

		// from("file:data/inbox?move=.done&moveFailed=.failed")
		// .setHeader("CamelFileName", simple("${file:name.noext}"))
		// .unmarshal(bind)
		// .process(exchange -> {
		// // Access the unmarshalled data
		// List<CustomerEntity> customers = exchange.getIn().getBody(List.class);
		//
		// // Set the filename for each customer
		// String filename = exchange.getIn().getHeader("CamelFileName", String.class);
		// for (CustomerEntity customer : customers) {
		// customer.setFileName(filename);
		// }
		//
		// // Replace the body with the modified data
		// exchange.getIn().setBody(customers);
		// })
		// .bean(customerService, "SaveAll")
		// .end();

		// onException(BadRequestException.class)
		// .process(exhange -> {
		// System.out.println("" +
		// exhange.getIn().getBody(CustomerEntity.class).toString());
		// })
		// .handled(true)
		// .end();

		// from("file:data/inbox?move=.done&moveFailed=.failed")
		// .setHeader("CamelFileName", simple("${file:name.noext}"))
		// .unmarshal(bind)
		// .split(body()) // Split the list of customers
		// .process(exchange -> {
		// CustomerEntity customer = exchange.getIn().getBody(CustomerEntity.class);
		// String filename = exchange.getIn().getHeader("CamelFileName", String.class);
		// customer.setFileName(filename);
		// exchange.getIn().setBody(customer);
		// })
		// .bean(customerService, "addCustomer")
		// .end();

		from("file:data/inbox?move=.done&moveFailed=.failed")
				.routeId("csv-input")
				.setHeader("CamelFileName", simple("${file:name.noext}"))
				.unmarshal(bind)
				.split(body()) // Split the list of customers
				.to("direct:csvFileProcessor");

		from("direct:csvFileProcessor")
				.process(exchange -> {
					CustomerEntity customer = exchange.getIn().getBody(CustomerEntity.class);
					String filename = exchange.getIn().getHeader("CamelFileName", String.class);
					customer.setFileName(filename);
					try {
						customerService.addCustomer(customer);
						System.out.println("Customer with email " + customer.getEmail() + " added successfully");

					} catch (Exception e) {

						System.out.println("Customer with email " + customer.getEmail() + " added unsucessfully");

						throw e;
					}
				})
				.end();

	}

}
