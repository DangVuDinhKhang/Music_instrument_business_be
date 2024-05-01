package com.thesis.business.musicinstrument.product;

import java.net.URI;
import java.util.List;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import com.thesis.business.musicinstrument.import_order_detail.ImportOrderDetail;

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
    @Consumes({MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_JSON})
    public Response add(@MultipartForm MultipartFormDataInput input){
        Long productId = productService.add(input);
        URI location = this.uriInfo.getAbsolutePathBuilder().path(String.valueOf(productId)).build();
        return Response.status(Response.Status.CREATED).location(location).build();
    }

    @GET
    @Path("/")
    public Response findAll(@QueryParam("page") Integer page, @QueryParam("pageSize") Integer pageSize){
        
        List<Product> products = productService.findAll(page, pageSize);
        return Response.status(Response.Status.OK).entity(products).build();
    }

    @GET
    @Path("/search/{key-word}")
    public Response findByWord(@PathParam("key-word") String word){
        
        List<Product> products = productService.findByWord(word);
        return Response.status(Response.Status.OK).entity(products).build();
    }

    @GET
    @Path("/popular")
    public Response findPopular(){
        
        List<ImportOrderDetail> products = productService.findPopular();
        return Response.status(Response.Status.OK).entity(products).build();
    }

    @PUT
    @Path("/add-to-cart/")
    public Response addToCart(AddToCartRequest addToCartRequest){
        productService.addToCart(addToCartRequest.getProductId(), addToCartRequest.getCartId());
        return Response.status(Response.Status.OK).build();
    }

    @PUT
    @Path("/update-in-cart/")
    public Response updateInCart(AddToCartRequest addToCartRequest){
        productService.updateInCart(addToCartRequest.getProductId(), addToCartRequest.getCartId(), addToCartRequest.getQuantity());
        return Response.status(Response.Status.OK).build();
    }

    @DELETE
    @Path("/remove-from-cart/{productId}/{cartId}")
    public Response removeFromCart(@PathParam("productId") Long productId, @PathParam("cartId") Long cartId){
        productService.removeFromCart(productId, cartId);
        return Response.status(Response.Status.OK).build();
    }

    // @POST
    // @Path("/add-to-cart/{quantity}")
    // public Response updateInCart(@PathParam("quantity") Integer quantity, AddToCartRequest addToCartRequest){
    //     productService.updateInCart(addToCartRequest.getProductId(), addToCartRequest.getCartId(), quantity);
    //     return Response.status(Response.Status.OK).build();
    // }

    @GET
    @Path("/cart/{id}")
    public Response findByCartId(@PathParam("id") Long cartId){

        List<ProductInCartDTO> products = productService.findByCartId(cartId);
        return Response.status(Response.Status.OK).entity(products).build();
    }

    @GET
    @Path("/category/{id}")
    public Response findByCategoryId(@PathParam("id") Long categoryId){

        List<Product> products = productService.findByCategoryId(categoryId);
        return Response.status(Response.Status.OK).entity(products).build();
    }

    @GET
    @Path("/{productId}/category/{categoryId}")
    public Response findRelated(@PathParam("categoryId") Long categoryId, @PathParam("productId") Long productId){

        List<Product> products = productService.findRelated(categoryId, productId);

        return Response.status(Response.Status.OK).entity(products).build();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id){

        Product product = productService.findById(id);

        return Response.status(Response.Status.OK).entity(product).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed("admin")
    @Consumes({MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_JSON})
    public Response updateById(@PathParam("id") Long id, @MultipartForm MultipartFormDataInput input){
        productService.updateById(id, input);
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
