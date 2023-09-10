package com.thesis.business.musicinstrument.rating;

import java.net.URI;
import java.util.List;

import org.eclipse.microprofile.jwt.JsonWebToken;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("/rating")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RatingResource {

    @Inject
    RatingService ratingService;

    @Inject
    JsonWebToken jwt;

    @Inject
    UriInfo uriInfo;

    @POST
    @Path("/")
    @RolesAllowed("member")
    public Response add(Rating rating) {

        Long ratingId = ratingService.add(rating, jwt.getName(), jwt.getGroups().stream().findFirst().orElse(null));
        URI location = this.uriInfo.getAbsolutePathBuilder().path(String.valueOf(ratingId)).build();
        return Response.status(Response.Status.CREATED).location(location).build();
    }

    @GET
    @Path("/product/{id}")
    public Response findByProductId(@PathParam("id") Long productId){
        
        List<Rating> ratings = ratingService.findByProductId(productId);
        return Response.status(Response.Status.OK).entity(ratings).build();
    }

    @GET
    @Path("/account/{id}")
    @RolesAllowed("member")
    public Response findByAccountId(@PathParam("id") Long accountId){
        
        List<Rating> ratings = ratingService.findByAccountId(accountId, jwt.getName(), jwt.getGroups().stream().findFirst().orElse(null));
        return Response.status(Response.Status.OK).entity(ratings).build();
    }

}
