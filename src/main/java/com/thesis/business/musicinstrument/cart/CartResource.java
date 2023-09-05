package com.thesis.business.musicinstrument.cart;

import java.net.URI;
import java.util.List;

import org.eclipse.microprofile.jwt.JsonWebToken;

import com.thesis.business.musicinstrument.account.Account;

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
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("/cart")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CartResource {

    @Inject
    CartService cartService;

    @Inject
    JsonWebToken jwt;

    @Inject
    UriInfo uriInfo;

    @POST
    @Path("/")
    @RolesAllowed({"admin", "member"})
    public Response add(Account account) {

        Cart cart = cartService.add(account);
        return Response.status(Response.Status.CREATED).entity(cart).build();
    }

    @GET
    @Path("/")
    public Response findAll(){
        
        List<Cart> carts = cartService.findAll();
        return Response.status(Response.Status.OK).entity(carts).build();
    }

    @GET
    @Path("/account/{id}")
    public Response findByAccountId(@PathParam("id") Long accountId){
        
        Cart cart = cartService.findByAccountId(accountId);
        return Response.status(Response.Status.OK).entity(cart).build();
    }

}
