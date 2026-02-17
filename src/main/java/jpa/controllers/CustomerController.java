package jpa.controllers;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jpa.config.Instance;
import jpa.dao.abstracts.CustomerDao;
import jpa.dao.impl.CustomerDaoImpl;
import jpa.dto.customer.CreateCustomerRequestDto;
import jpa.dto.customer.ResponseCustomerDto;
import jpa.services.impl.CustomerServiceImpl;
import jpa.services.interfaces.CustomerService;

@Path("/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController() {
        customerService = Instance.CUSTOMER_SERVICE;
    }

    @POST
    @Path("/register")
    public Response register(CreateCustomerRequestDto request) {
        ResponseCustomerDto created = customerService.createAccount(request);
        return Response.status(Response.Status.CREATED)
                .entity(created)
                .build();
    }
}
