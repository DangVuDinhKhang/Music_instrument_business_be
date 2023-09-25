package com.thesis.business.musicinstrument.product;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import com.thesis.business.musicinstrument.MusicInstrumentException;
import com.thesis.business.musicinstrument.cart.Cart;
import com.thesis.business.musicinstrument.cart.CartService;
import com.thesis.business.musicinstrument.cart_product.CartProduct;
import com.thesis.business.musicinstrument.cart_product.CartProductService;
import com.thesis.business.musicinstrument.category.Category;
import com.thesis.business.musicinstrument.category.CategoryService;
import com.thesis.business.musicinstrument.image.ImageService;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import com.fasterxml.jackson.databind.ObjectMapper;

@RequestScoped
public class ProductService {

    @Inject
    ProductRepository productRepository;

    @Inject 
    CategoryService categoryService;

    @Inject 
    ImageService imageService;

    @Inject
    CartService cartService;

    @Inject 
    ProductService productService;

    @Inject
    CartProductService cartProductService;

    @Transactional
    public Long add(MultipartFormDataInput input) {
        String name = null;
        String description = null;
        Integer price = 0;
        Integer amount = 0;
        Category category = new Category();
        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();

        if(uploadForm.containsKey("name")) {
            List<InputPart> nameParts = uploadForm.get("name");
            if (!nameParts.isEmpty()) {
                InputPart namePart = nameParts.get(0); 
                try {
                    name = namePart.getBodyAsString();
                    System.out.println(name);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if(uploadForm.containsKey("description")) {
            List<InputPart> descriptionParts = uploadForm.get("description");
            if (!descriptionParts.isEmpty()) {
                InputPart descriptionPart = descriptionParts.get(0); 
                try {
                    description = descriptionPart.getBodyAsString();
                    System.out.println(description);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if(uploadForm.containsKey("price")) {
            List<InputPart> priceParts = uploadForm.get("price");
            if (!priceParts.isEmpty()) {
                InputPart pricePart = priceParts.get(0); 
                try {
                    price = Integer.parseInt(pricePart.getBodyAsString());
                    System.out.println(price);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if(uploadForm.containsKey("category")) {
            List<InputPart> categoryParts = uploadForm.get("category");
            if (!categoryParts.isEmpty()) {
                InputPart categoryPart = categoryParts.get(0); 
                try {
                    String raw = categoryPart.getBodyAsString();
                    ObjectMapper objectMapper = new ObjectMapper();
                    category = objectMapper.readValue(raw, Category.class);
                    System.out.println(category);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        Product product = new Product(name, description, price, amount, category);
        if(categoryService.findById(product.getCategory().getId()) == null)
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Category does not exist");
        product.setQuantity(0);
        productRepository.persist(product);

        if(uploadForm.containsKey("file"))
            imageService.uploadFile(input, product);
        return product.getId();
    }

    // public List<ProductDTO> findAll() {

    //     List<Product> products =  productRepository.listAll();
    //     List<ProductDTO> productDTOs = new ArrayList<>();
    //     for (Product product : products) {
    //         productDTOs.add(this.convertToDTO(product));
    //     }
    //     return productDTOs;
    // }

    public List<Product> findAll() {

        return productRepository.listAll();
    }

    public List<Product> findByWord(String word) {

        return productRepository.find("name ILIKE ?1", "%" + word + "%").list();
    }

    public Product findById(Long id){

        Product product = productRepository.findById(id);
        if(product == null)
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Product does not exist");
        return product;
    }

    public List<Product> findByCategoryId(Long categoryId){

        List<Product> products = productRepository.find("category.id", categoryId).list();
        if(products.size() == 0)
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Category does not exist");
        return products;
    }

    public void updateQuantity(Long id, Integer hasPurchasedQuantity, Boolean canceled){
        Product productInDB = productRepository.findById(id);
        if(productInDB == null)
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Product does not exist");
        if(!canceled)
            productInDB.setQuantity(productInDB.getQuantity() - hasPurchasedQuantity);
        else
            productInDB.setQuantity(productInDB.getQuantity() + hasPurchasedQuantity);
        productRepository.persist(productInDB);
    }

    @Transactional
    public void updateById(Long id, MultipartFormDataInput input) {

        Product productInDB = productRepository.findById(id);
        if(productInDB == null)
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Product does not exist");

        String name = null;
        String description = null;
        Integer price = -1;
        Integer amount = 0;
        Category category = new Category();
        Boolean checkCategory = false;
        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();

        if(uploadForm.containsKey("name")) {
            List<InputPart> nameParts = uploadForm.get("name");
            if (!nameParts.isEmpty()) {
                InputPart namePart = nameParts.get(0); 
                try {
                    name = namePart.getBodyAsString();
                    System.out.println(name);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if(uploadForm.containsKey("description")) {
            List<InputPart> descriptionParts = uploadForm.get("description");
            if (!descriptionParts.isEmpty()) {
                InputPart descriptionPart = descriptionParts.get(0); 
                try {
                    description = descriptionPart.getBodyAsString();
                    System.out.println(description);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if(uploadForm.containsKey("price")) {
            List<InputPart> priceParts = uploadForm.get("price");
            if (!priceParts.isEmpty()) {
                InputPart pricePart = priceParts.get(0); 
                try {
                    price = Integer.parseInt(pricePart.getBodyAsString());
                    System.out.println(price);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if(uploadForm.containsKey("category")) {
            List<InputPart> categoryParts = uploadForm.get("category");
            if (!categoryParts.isEmpty()) {
                InputPart categoryPart = categoryParts.get(0); 
                try {
                    String raw = categoryPart.getBodyAsString();
                    ObjectMapper objectMapper = new ObjectMapper();
                    category = objectMapper.readValue(raw, Category.class);
                    System.out.println(category);
                    checkCategory = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if(name != null)
            productInDB.setName(name);
        if(description != null)
            productInDB.setDescription(description);
        if(price != -1)
            productInDB.setPrice(price);
        if(amount != 0)
            productInDB.setQuantity(amount);
        if(checkCategory)
            productInDB.setCategory(category);
        if(uploadForm.containsKey("file"))
            imageService.uploadFile(input, productInDB);
        productRepository.persist(productInDB);
    }

    @Transactional
    public void deleteById(Long id){
        
        imageService.deleteByProductId(id);
        if(productRepository.deleteById(id))
            return;
        else
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Product does not exist");
    }

    public List<ProductInCartDTO> findByCartId(Long cartId){
        return cartProductService.findProductsByCartId(cartId);
    }

    @Transactional
    public void addToCart(Long productId, Long cartId){
        cartProductService.addProductToCart(productId, cartId);
    }

    @Transactional
    public void updateInCart(Long productId, Long cartId){
        cartProductService.updateProductInCart(productId, cartId);
    }

    @Transactional
    public void removeFromCart(Long productId, Long cartId){
        cartProductService.removeProductFromCart(productId, cartId);
    }

    // @Transactional
    // public void updateInCart(Long productId, Long cartId, Integer quantity){
    //     cartProductService.updateProductInCart(productId, cartId, quantity);
    // }


    // private ProductDTO convertToDTO(Product product){

    //     ProductDTO productDTO = new ProductDTO();
    //     productDTO.setId(product.getId());
    //     productDTO.setName(product.getName());
    //     productDTO.setDescription(product.getDescription());
    //     productDTO.setPrice(product.getPrice());
    //     productDTO.setAmount(product.getAmount());
    //     productDTO.setCategory(product.getCategory());
    //     return productDTO;

    // }

    // private Product convertToEntity(ProductDTO productDTO){

    //     Product product = new Product();
    //     product.setId(productDTO.getId());
    //     product.setName(productDTO.getName());
    //     product.setDescription(productDTO.getDescription());
    //     product.setPrice(productDTO.getPrice());
    //     product.setAmount(productDTO.getAmount());
    //     product.setCategory(productDTO.getCategory());
    //     return product;
    // }

}
