package com.thesis.business.musicinstrument.product;

import java.net.URI;
import java.util.List;

import org.eclipse.microprofile.jwt.JsonWebToken;

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

@Path("product")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductResource {

    @Inject
    ProductService productService;

    @Inject
    JsonWebToken jwt;

    @Inject
    UriInfo uriInfo;

    @POST
    @Path("/")
    @RolesAllowed("admin")
    public Response add(Product product) {

        Long productId = productService.add(product);
        URI location = this.uriInfo.getAbsolutePathBuilder().path(String.valueOf(productId)).build();
        return Response.status(Response.Status.CREATED).location(location).build();
    }

    @GET
    @Path("/")
    public Response findAll(){
        
        List<Product> products = productService.findAll();
        return Response.status(Response.Status.OK).entity(products).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed("admin")
    public Response updateById(@PathParam("id") Long id, Product product){

        productService.updateById(id, product);

        return Response.status(Response.Status.OK).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("admin")
    public Response deleteById(@PathParam("id") Long id){

        productService.deleteById(id);

        return Response.status(Response.Status.OK).build();
    }
}
