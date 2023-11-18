package vn.edu.iuh.fit.frontend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.fit.backend.models.Product;
import vn.edu.iuh.fit.backend.repositories.ProductRepository;
import vn.edu.iuh.fit.backend.services.ProductServices;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class ProductController {
    @Autowired
    private ProductServices productServices;
    @Autowired
    private ProductRepository productRepository;
    @GetMapping("/admin")
    public String showAdminUI(){
        return "admin/manager.html";
    }


    @GetMapping("/products")
    public String showCandidateListPaging(Model model,
                                          @RequestParam("page") Optional<Integer> page,
                                          @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);

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
        return "admin/product/listProduct.html";
    }

    @GetMapping("/show-add-form")
    public String add(Model model) {
        Product product =new Product();
        model.addAttribute("product",product);
        return "admin/product/addProductForm";
    }
//    @PostMapping("/products/add")
//    public String addCandidate(
//            @ModelAttribute("product") Product product,
//            BindingResult result, Model model) {
//        productRepository.save(product);
//        return "redirect:/products";
//    }

    //    @DeleteMapping("/products/delete/{id}")
    @GetMapping("/products/delete/{id}")
    public String addCandidate(@PathVariable("id") long id) {
        Product product = productRepository.findById(id).orElse(new Product());
        productRepository.delete(product);
        return "redirect:/products";
    }
    // sửa product
    @GetMapping("/products/edit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        Product product = productRepository.findById(id).orElse(new Product());
        model.addAttribute("product", product);
        return "admin/product/updateProductForm.html";
    }


}
