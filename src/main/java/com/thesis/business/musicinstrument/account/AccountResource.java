package com.thesis.business.musicinstrument.account;

import java.util.List;

import org.eclipse.microprofile.jwt.JsonWebToken;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/account")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AccountResource {

    @Inject
    AccountService accountService;

    @Inject
    JsonWebToken jwt;

    @POST
    @Path("/register")
    public Response register(Account account) {
        accountService.register(account);
        return Response.status(Response.Status.CREATED).build();
    }

    @POST
    @Path("/login")
    public Response login(Account account) {
        String token = accountService.login(account);
        return Response.ok(token).build();
    }

    
    @GET
    @Path("/{id}")
    @RolesAllowed({"admin", "member"})
    public Response findById(@PathParam("id") Long id) {
        Account account = accountService.findById(id, jwt.getName(), jwt.getGroups().stream().findFirst().orElse(null));
        return Response.status(Response.Status.OK).entity(account).build();
    }

    @GET
    @Path("/")
    @RolesAllowed("admin")
    public Response findAll() {
        List<Account> accounts = accountService.findAll();
        return Response.status(Response.Status.OK).entity(accounts).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"admin", "member"})
    public Response updateById(@PathParam("id") Long id, Account account) {
        accountService.updateById(id, account, jwt.getName(), jwt.getGroups().stream().findFirst().orElse(null));
        return Response.status(Response.Status.OK).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("admin")
    public Response deleteById(@PathParam("id") Long id) {
        accountService.deleteById(id);
        return Response.status(Response.Status.OK).build();
    }
}
