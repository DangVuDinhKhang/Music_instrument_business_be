package com.thesis.business.musicinstrument.orderDetail;

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

@Path("/order-detail")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class OrderDetailResource {

    @Inject
    OrderDetailService orderDetailService;

    @Inject
    JsonWebToken jwt;

    @Inject
    UriInfo uriInfo;

    @POST
    @Path("/")
    @RolesAllowed({"admin", "member"})
    public Response add(OrderDetail orderDetail) {

        Long orderDetailId = orderDetailService.add(orderDetail);
        URI location = this.uriInfo.getAbsolutePathBuilder().path(String.valueOf(orderDetailId)).build();
        return Response.status(Response.Status.CREATED).location(location).build();
    }

    @GET
    @Path("/{orderId}")
    public Response findByOrderId(@PathParam("orderId") Long orderId){
        
        List<OrderDetail> orderDetails = orderDetailService.findByOrderId(orderId);
        return Response.status(Response.Status.OK).entity(orderDetails).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("admin")
    public Response deleteById(@PathParam("id") Long id){

        orderDetailService.deleteById(id);

        return Response.status(Response.Status.OK).build();
    }
}
