package com.thesis.business.musicinstrument.import_order;

import java.net.URI;
import java.util.List;

import org.eclipse.microprofile.jwt.JsonWebToken;

import com.thesis.business.musicinstrument.order.CustomerOrder;
import com.thesis.business.musicinstrument.order.CustomerOrderRequest;
import com.thesis.business.musicinstrument.order.CustomerOrderService;
import com.thesis.business.musicinstrument.order.UpdateCustomerOrderRequest;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.core.MediaType;

@Path("/import-orders")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ImportOrderResource {
    @Inject
    ImportOrderService importOrderService;

    @Inject
    JsonWebToken jwt;

    @Inject
    UriInfo uriInfo;

    @POST
    @Path("/")
    @RolesAllowed("admin")
    public Response add(ImportOrderRequest importOrderRequest) {

        System.out.println(importOrderRequest.getSupplier());

        Long importOrderId = importOrderService.add(importOrderRequest, jwt.getName(), jwt.getGroups().stream().findFirst().orElse(null));
        URI location = this.uriInfo.getAbsolutePathBuilder().path(String.valueOf(importOrderId)).build();
        return Response.status(Response.Status.CREATED).location(location).build();
    }

    // @GET
    // @Path("/")
    // @RolesAllowed("admin")
    // public Response findAll() {

    //     List<CustomerOrder> customerOrders = customerOrderService.findAll();
    //     return Response.status(Response.Status.OK).entity(customerOrders).build();
    // }

    // @GET
    // @Path("/account/{id}")
    // //@RolesAllowed({"admin", "member"})
    // public Response findByAccountId(@PathParam("id") Long accountId) {

    //     List<CustomerOrder> customerOrders = customerOrderService.findByAccountId(accountId);
    //     return Response.status(Response.Status.OK).entity(customerOrders).build();
    // }

    // @PUT
    // @Path("/{id}")
    // @RolesAllowed("admin")
    // public Response updateById(@PathParam("id") Long id, UpdateCustomerOrderRequest updateCustomerOrderRequest){
    //     customerOrderService.updateById(id, updateCustomerOrderRequest.getStatus());
    //     return Response.status(Response.Status.OK).build();
    // }

    // @PUT
    // @Path("/cancel/{id}")
    // @RolesAllowed("member")
    // public Response cancelById(@PathParam("id") Long id, UpdateCustomerOrderRequest updateCustomerOrderRequest){
    //     customerOrderService.cancelById(id, updateCustomerOrderRequest.getStatus());
    //     return Response.status(Response.Status.OK).build();
    // }

    // @DELETE
    // @Path("/{id}")
    // @RolesAllowed("admin")
    // public Response deleteById(@PathParam("id") Long id) {

    //     customerOrderService.deleteById(id);

    //     return Response.status(Response.Status.OK).build();
    // }

    // @GET
    // @Path("/statistic")
    // @RolesAllowed("admin")
    // public Response statistic(@QueryParam("type") String type) {

    //     List<CustomerOrder> customerOrders = customerOrderService.statistic(type);

    //     return Response.status(Response.Status.OK).entity(customerOrders).build();
    // }

    // @GET
    // @Path("/statistic-total")
    // @RolesAllowed("admin")
    // public Response statisticTotal(@QueryParam("type") String type) {

    //     Long totalOrders = customerOrderService.statisticTotal();

    //     return Response.status(Response.Status.OK).entity(totalOrders).build();
    // }
}
