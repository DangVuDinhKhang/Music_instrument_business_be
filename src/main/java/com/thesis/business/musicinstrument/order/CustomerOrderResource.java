package com.thesis.business.musicinstrument.order;

import java.net.URI;
import java.util.List;

import org.eclipse.microprofile.jwt.JsonWebToken;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("/order")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CustomerOrderResource {

    @Inject
    CustomerOrderService customerOrderService;

    @Inject
    JsonWebToken jwt;

    @Inject
    UriInfo uriInfo;

    @POST
    @Path("/")
    @RolesAllowed("admin")
    public Response add(CustomerOrder customerOrder) {

        Long customerOrderId = customerOrderService.add(customerOrder, jwt.getName(), jwt.getGroups().stream().findFirst().orElse(null));
        URI location = this.uriInfo.getAbsolutePathBuilder().path(String.valueOf(customerOrderId)).build();
        return Response.status(Response.Status.CREATED).location(location).build();
    }

    @GET
    @Path("/")
    public Response findAll() {

        List<CustomerOrder> customerOrders = customerOrderService.findAll();
        return Response.status(Response.Status.OK).entity(customerOrders).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("admin")
    public Response deleteById(@PathParam("id") Long id) {

        customerOrderService.deleteById(id);

        return Response.status(Response.Status.OK).build();
    }
}
