package com.example.customerservice;

import com.example.customerservice.model.Customer;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@RestController
public class CustomerServiceController {

    private final Logger logger = LogManager.getLogger(CustomerServiceController.class);

    private final String creditBaseUrl;

    private static final String LOOKUP_STR = "Looking up vehicle info";
    private static final String RETURNING_STR = "Returning vehicle info to caller";


    private final RestTemplate restTemplate;

    @Autowired
    public CustomerServiceController(@Value("${service.credit.baseUrl}") String creditBaseUrl,
                                     RestTemplate restTemplate) {
        this.creditBaseUrl = creditBaseUrl;
        this.restTemplate = restTemplate;
    }

    @GetMapping(value = "/customers")
    public ResponseEntity<Object> getCustomers() {
        logger.info(LOOKUP_STR);

        List<Customer> customers = getCustomerInfo();

        logger.info(RETURNING_STR);

        return new ResponseEntity<>(customers, HttpStatus.OK);
    }

    @GetMapping(value = "/customers/{customer_id}")
    public ResponseEntity<Object> getCustomerById(@PathVariable String customer_id) {
        logger.info(LOOKUP_STR);

        List<Customer> customers = getCustomerInfo();

        for (Customer customer : customers) {
            if (customer.getCustomerId().trim().equalsIgnoreCase(customer_id.trim())) {
                logger.info(RETURNING_STR + " for customer ID " + customer_id);

                return new ResponseEntity<>(customer, HttpStatus.OK);
            }
        }

        logger.info("No customer found for ID " + customer_id);

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    private Integer getCreditScore(String customerId) {
        String url = creditBaseUrl + "/" + customerId;

        try {
            final JsonNode responseBody = restTemplate.getForObject(url, JsonNode.class);
            return responseBody.get("creditScore").asInt();
        } catch (Exception e) {
            logger.error("Exception looking up the credit score count for customer " + customerId, e);
        }

        return 0;
    }

    private List<Customer> getCustomerInfo() {
        List<Customer> customers = new ArrayList<>();

        Customer customer1 = new Customer();
        customer1.setCustomerId("customer-1");
        customer1.setName("Clarisse Hannond");

        Customer customer2 = new Customer();
        customer2.setCustomerId("customer-2");
        customer2.setName("Bill Giacovelli");

        Customer customer3 = new Customer();
        customer3.setCustomerId("customer-3");
        customer3.setName("Colline Rive");

        Customer customer4 = new Customer();
        customer4.setCustomerId("customer-4");
        customer4.setName("Bernice Heading");

        Customer customer5 = new Customer();
        customer5.setCustomerId("customer-5");
        customer5.setName("Zack Dugan");

        Customer customer6 = new Customer();
        customer6.setCustomerId("customer-6");
        customer6.setName("Rupert Tessington");

        Customer customer7 = new Customer();
        customer7.setCustomerId("customer-7");
        customer7.setName("Allina Clench");

        customers.add(customer1);
        customers.add(customer2);
        customers.add(customer3);
        customers.add(customer4);
        customers.add(customer5);
        customers.add(customer6);
        customers.add(customer7);

        for (Customer customer : customers) {
            customer.setCreditScore(getCreditScore(customer.getCustomerId()));
        }

        return customers;
    }
}
