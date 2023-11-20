package com.thesis.business.musicinstrument.cart_product;

import java.util.List;
import java.util.stream.Collectors;

import com.thesis.business.musicinstrument.MusicInstrumentException;
import com.thesis.business.musicinstrument.cart.Cart;
import com.thesis.business.musicinstrument.cart.CartService;
import com.thesis.business.musicinstrument.product.Product;
import com.thesis.business.musicinstrument.product.ProductInCartDTO;
import com.thesis.business.musicinstrument.product.ProductService;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

@RequestScoped
public class CartProductService {

    @Inject
    CartProductRepository cartProductRepository;

    @Inject
    ProductService productService;

    @Inject
    CartService cartService;
    
    @Transactional
    public void addProductToCart(Long productId, Long cartId){
        
        Product product = productService.findById(productId);
        Cart cart = cartService.findById(cartId);

        CartProduct cartProduct = findByProductIdAndCartId(productId, cartId);
        if(cartProduct == null){
            cartProduct = new CartProduct();
            cartProduct.setProduct(product);
            cartProduct.setCart(cart);
            cartProduct.setQuantity(1);
        }
        else {
            if(cartProduct.getQuantity() < product.getQuantity())
                cartProduct.setQuantity(cartProduct.getQuantity() + 1);
            else
                throw new MusicInstrumentException(Response.Status.BAD_REQUEST, "Sold out");
        }
        cartProductRepository.persist(cartProduct);

    }

    @Transactional
    public void updateProductInCart(Long productId, Long cartId, Integer quantity){

        CartProduct cartProduct = findByProductIdAndCartId(productId, cartId);
        Product product = productService.findById(productId);
        if(cartProduct == null){
            throw new MusicInstrumentException(Response.Status.BAD_REQUEST, "Product or cart does not exist");
        }
        if(quantity == 1){
            System.out.println("Davao1");
            if(cartProduct.getQuantity() - 1 > 0){
                cartProduct.setQuantity(cartProduct.getQuantity() - 1);
                cartProductRepository.persist(cartProduct);
            }
            else{
                cartProductRepository.deleteById(cartProduct.getId());
            }
        }
        else{
            System.out.println("davao2");
            if(quantity <= product.getQuantity()){
                cartProduct.setQuantity(quantity);
                cartProductRepository.persist(cartProduct);
            }
        }
        

    }

    @Transactional
    public void removeProductFromCart(Long productId, Long cartId){

        CartProduct cartProduct = findByProductIdAndCartId(productId, cartId);
        if(cartProduct == null){
            throw new MusicInstrumentException(Response.Status.BAD_REQUEST, "Product or cart does not exist");
        }

        cartProductRepository.deleteById(cartProduct.getId());

    }

    // @Transactional
    // public void updateProductInCart(Long productId, Long cartId, Integer quantity){
        
    //     Product product = productService.findById(productId);
    //     Cart cart = cartService.findById(cartId);

    //     CartProduct cartProduct = findByProductIdAndCartId(productId, cartId);
    //     if(cartProduct == null){
    //         cartProduct = new CartProduct();
    //         cartProduct.setProduct(product);
    //         cartProduct.setCart(cart);
    //         cartProduct.setQuantity(1);
    //     }
    //     else{
    //         cartProduct.setQuantity(quantity);
    //     }
    //     cartProductRepository.persist(cartProduct);

    // }

    public CartProduct findByProductIdAndCartId(Long productId, Long cartId){
        CartProduct cartProduct = cartProductRepository.find("product.id = ?1 and cart.id = ?2", productId, cartId).firstResult();
        return cartProduct;
    }

    public List<ProductInCartDTO> findProductsByCartId(Long cartId) {
        List<CartProduct> cartProducts =cartProductRepository. find("cart.id", cartId).list();
        return cartProducts.stream()
            .map(cartProduct -> {
                ProductInCartDTO productInCartDTO = new ProductInCartDTO();
                productInCartDTO.setProduct(cartProduct.getProduct());
                productInCartDTO.setQuantity(cartProduct.getQuantity());
                return productInCartDTO;
            })
            .collect(Collectors.toList());
    }
}
