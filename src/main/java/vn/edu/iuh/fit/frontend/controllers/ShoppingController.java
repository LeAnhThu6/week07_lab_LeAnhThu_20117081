package vn.edu.iuh.fit.frontend.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.fit.backend.models.Product;
import vn.edu.iuh.fit.backend.repositories.ProductRepository;
import vn.edu.iuh.fit.backend.services.ProductServices;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@SessionAttributes("cartItems")
public class ShoppingController {
    @Autowired
    private ProductServices productServices;
    @Autowired
    private ProductRepository productRepository;

    private List<Map<String, Object>> getOrCreateCartItems(Model model) {
        List<Map<String, Object>> cartItems = (List<Map<String, Object>>) model.getAttribute("cartItems");

        // Nếu cartItems là null, tạo mới một danh sách trống
        if (cartItems == null) {
            cartItems = new ArrayList<>();
            model.addAttribute("cartItems", cartItems);
        }

        return cartItems;
    }
    @GetMapping("/shopping")
    public String showCandidateListPaging(Model model,
                                          @RequestParam("page") Optional<Integer> page,
                                          @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(12);

        Page<Product> productPage = productServices.findPaginated(currentPage - 1,
                pageSize, "name", "asc");

        model.addAttribute("productPage", productPage);

        int totalPages = productPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        List<Map<String, Object>> cartItems = getOrCreateCartItems(model);
        int cartTotal = calculateCartTotal(cartItems);
        model.addAttribute("cartTotal", cartTotal);

        return "shopping/home.html";
    }
    //chi tiết sản phẩm, lấy id từ url
    @GetMapping("/product-detail")
    public String showProductDetail(Model model, @RequestParam("id") long id) {
        Optional<Product> product = productRepository.findById(id);
        model.addAttribute("product", product.get());
        List<Map<String, Object>> cartItems = getOrCreateCartItems(model);
        int cartTotal = calculateCartTotal(cartItems);
        model.addAttribute("cartTotal", cartTotal);
        return "shopping/productDetail.html";
    }
    @PostMapping("/addToCart")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addToCart(@RequestParam("productId") long productId, @RequestParam("quantity") int quantity, Model model) {
        // Lấy danh sách sản phẩm từ model
        Product product= productRepository.findById(productId).orElse(new Product());
        List<Map<String, Object>> cartItems = getOrCreateCartItems(model);
        // Kiểm tra xem sản phẩm đã có trong giỏ hàng chưa
        boolean productExists = false;
        for (Map<String, Object> cartItem : cartItems) {
            if ((long) cartItem.get("productId") == product.getProduct_id()) {
                int currentQuantity = (int) cartItem.get("quantity");
                cartItem.put("quantity", currentQuantity + quantity);
                productExists = true;
                break;
            }
        }

        // Nếu sản phẩm chưa tồn tại trong giỏ hàng, thêm mới
        if (!productExists) {
            Map<String, Object> newCartItem = new HashMap<>();
            newCartItem.put("productId", product.getProduct_id());
            newCartItem.put("name", product.getName());
            newCartItem.put("price", product.getProductPrices().get(0).getPrice());
            newCartItem.put("quantity", quantity);
            cartItems.add(newCartItem);
        }

        // Tính tổng số lượng trong giỏ hàng
        int cartTotal = calculateCartTotal(cartItems);

        // Tạo phản hồi
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("quantity", quantity); // Trả về số lượng vừa thêm vào giỏ hàng
        responseData.put("cartTotal", cartTotal); // Trả về tổng số lượng trong giỏ hàng

        return ResponseEntity.ok(responseData);
    }

    private int calculateCartTotal(List<Map<String, Object>> cartItems) {
        // Tính tổng số lượng trong giỏ hàng
        int cartTotal = 0;
        for (Map<String, Object> cartItem : cartItems) {
            cartTotal += (int) cartItem.get("quantity");
        }
        return cartTotal;
    }

    private double calculateCartTotalPrice(List<Map<String, Object>> cartItems) {
        // Tính tổng số lượng trong giỏ hàng
        double totalPrice = 0;
        for (Map<String, Object> cartItem : cartItems) {
            totalPrice += (int) cartItem.get("quantity")*(double) cartItem.get("price");
        }
        return totalPrice;
    }
    @GetMapping("/cart")
    public String showCart(Model model) {
        List<Map<String, Object>> cartItems = getOrCreateCartItems(model);
        int cartTotal = calculateCartTotal(cartItems);
        double cartTotalPrice = calculateCartTotalPrice(cartItems);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartTotal", cartTotal);
        model.addAttribute("cartTotalPrice", cartTotalPrice);
        return "shopping/cart.html";
    }
}
