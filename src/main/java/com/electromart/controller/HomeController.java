package com.electromart.controller;

import com.electromart.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProductService productService;

    @GetMapping({"/", "/products"})
    public String home(@RequestParam(required = false) String category, Model model) {
        model.addAttribute("selectedCategory", category);
        model.addAttribute("featuredProducts", productService.getFeaturedProducts());
        model.addAttribute("products", productService.getAllProducts(category));
        return "index";
    }
}
