package com.thesis.business.musicinstrument.order;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.eclipse.microprofile.jwt.JsonWebToken;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("/order")
@Consumes(MediaType.APPLICATION_JSON)
@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
public class CustomerOrderResource {

    @Inject
    CustomerOrderService customerOrderService;

    @Inject
    JsonWebToken jwt;

    @Inject
    UriInfo uriInfo;

    @POST
    @Path("/")
    @RolesAllowed({"admin", "member"})
    public Response add(CustomerOrderRequest customerOrderRequest) {

        // Long customerOrderId = customerOrderService.add(customerOrderRequest, jwt.getName(), jwt.getGroups().stream().findFirst().orElse(null));
        // URI location = this.uriInfo.getAbsolutePathBuilder().path(String.valueOf(customerOrderId)).build();
        // return Response.status(Response.Status.CREATED).location(location).build();
        String result = customerOrderService.add(customerOrderRequest, jwt.getName(), jwt.getGroups().stream().findFirst().orElse(null));
        PaymentUrl paymentUrl = new PaymentUrl(result);
        return Response.status(Response.Status.CREATED).entity(paymentUrl).build();
    }

    @GET
    @Path("/")
    @RolesAllowed("admin")
    public Response findAll() {

        List<CustomerOrder> customerOrders = customerOrderService.findAll();
        return Response.status(Response.Status.OK).entity(customerOrders).build();
    }

    @GET
    @Path("/account/{id}")
    //@RolesAllowed({"admin", "member"})
    public Response findByAccountId(@PathParam("id") Long accountId) {

        List<CustomerOrder> customerOrders = customerOrderService.findByAccountId(accountId);
        return Response.status(Response.Status.OK).entity(customerOrders).build();
    }

    // @GET
    // @Path("/{id}")
    // @RolesAllowed({"admin", "member"})
    // public Response findByIdAndAccountId(@PathParam("id") Long id) {

    //     CustomerOrder customerOrder = customerOrderService.findById(id);
    //     return Response.status(Response.Status.OK).entity(customerOrder).build();
    // }

    @PUT
    @Path("/{id}")
    @RolesAllowed("admin")
    public Response updateById(@PathParam("id") Long id, UpdateCustomerOrderRequest updateCustomerOrderRequest){
        customerOrderService.updateById(id, updateCustomerOrderRequest.getStatus());
        return Response.status(Response.Status.OK).build();
    }

    @PUT
    @Path("/cancel/{id}")
    @RolesAllowed("member")
    public Response cancelById(@PathParam("id") Long id, UpdateCustomerOrderRequest updateCustomerOrderRequest){
        customerOrderService.cancelById(id, updateCustomerOrderRequest.getStatus());
        return Response.status(Response.Status.OK).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("admin")
    public Response deleteById(@PathParam("id") Long id) {

        customerOrderService.deleteById(id);

        return Response.status(Response.Status.OK).build();
    }

    @GET
    @Path("/statistic")
    @RolesAllowed("admin")
    public Response statistic(@QueryParam("type") String type) {

        List<CustomerOrder> customerOrders = customerOrderService.statistic(type);

        return Response.status(Response.Status.OK).entity(customerOrders).build();
    }

    @GET
    @Path("/statistic-total")
    @RolesAllowed("admin")
    public Response statisticTotal(@QueryParam("type") String type) {

        Long totalOrders = customerOrderService.statisticTotal();

        return Response.status(Response.Status.OK).entity(totalOrders).build();
    }

    @GET
    @Path("/statistic-total-revenue")
    @RolesAllowed("admin")
    public Response statisticTotalRevenue() {

        Long totalRevenues = customerOrderService.statisticTotalRevenue();

        return Response.status(Response.Status.OK).entity(totalRevenues).build();
    }

    @GET
    @Path("/statistic-profit/{id}")

    @RolesAllowed("admin")
    public Response statisticProfit(@PathParam("id") Long id) {

        Long totalRevenues = customerOrderService.statisticProfit(id);

        return Response.status(Response.Status.OK).entity(totalRevenues).build();
    }

    @PUT
    @Path("vnpay/{id}")
    public Response updateVNPayStatus(@PathParam("id") Long customerOrderId, @QueryParam("status") Integer status,  CustomerOrder customerOrder) {
        
        customerOrderService.updateVNPayStatus(customerOrderId, status);

        return Response.status(Response.Status.OK).build();

    }

    @GET
    @Path("/vnpay/return")
    public Response getReturn(@Context UriInfo uriInfo) {

        
        return Response.status(Response.Status.OK).entity(uriInfo.getQueryParameters()).build();
    }
}
