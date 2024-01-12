package com.rishavmngo.UserAssignment.routes;

import java.util.Optional;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rishavmngo.UserAssignment.domain.CustomerEntity;
import com.rishavmngo.UserAssignment.service.CustomerService;

@Component
public class CustomerRoutes extends RouteBuilder {
	@Autowired
	private CustomerService customerService;

	@Override
	public void configure() throws Exception {
		restConfiguration().bindingMode(RestBindingMode.json);
		rest("/customers")
				.get("/getAll")
				.produces("application/json")
				.to("direct:getAll")
				.delete("/deleteById/{id}")
				.to("direct:deleteById")
				.delete("/deleteByFilename/{filename}")
				.to("direct:deleteByFilename");

		from("direct:getAll")
				.bean(customerService, "getAll");

		from("direct:deleteById")
				.process(exchange -> {

					String id = exchange.getIn().getHeader("id", String.class);
					Optional<CustomerEntity> customer = customerService.deleteById(Long.parseLong(id));
					if (customer.isPresent())
						exchange.getIn().setBody(customer.get());
					else {
						exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
						exchange.getIn().setBody("Customer with id " + id + " don't exist");
					}
				});

		from("direct:deleteByFilename")
				.process(exchange -> {

					String filename = exchange.getIn().getHeader("filename", String.class);
					int size = customerService.deleteByFilename(filename);
					exchange.getIn().setBody("Customer with id ");
					if (size > 0)
						exchange.getIn().setBody(size + " items" + " deleted");
					else {
						exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
						exchange.getIn().setBody("No items with filename: " + filename + " exist");
					}
				});
	}

}
