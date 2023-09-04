package com.thesis.business.musicinstrument.image;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/file")
public class ImageResource {
    @Inject
    ImageService imageService;

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        List<Image> images = imageService.getAll();
        return Response.status(Response.Status.OK).entity(images).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByProductId(@PathParam("id") Long productId) {
        List<Image> images = imageService.getByProductId(productId);
        return Response.status(Response.Status.OK).entity(images).build();
    }
}
