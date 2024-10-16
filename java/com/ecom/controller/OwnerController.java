package com.ecom.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Category;
import com.ecom.model.Product;
import com.ecom.model.ProductOrder;
import com.ecom.model.UserDtls;
import com.ecom.service.CartService;
import com.ecom.service.CategoryService;
import com.ecom.service.OrderService;
import com.ecom.service.ProductService;
import com.ecom.service.UserService;
import com.ecom.util.CommonUtil;
import com.ecom.util.OrderStatus;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/owner")
public class OwnerController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CommonUtil commonUtil;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private CartService cartService;
	
	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private OrderService orderService;
	
	
	
//	@ModelAttribute
//	public void getUserDetails(Principal p, Model m) {
//		if (p != null) {
//			String email = p.getName();
//			UserDtls userDtls = userService.getUserByEmail(email);
//			m.addAttribute("user", userDtls);
//			Integer countCart = cartService.getCountCart(userDtls.getId());
//			m.addAttribute("countCart", countCart);
//		}
//
//		List<Category> allActiveCategory = categoryService.getAllActiveCategory();
//		m.addAttribute("categorys", allActiveCategory);
//	}
	
	@ModelAttribute
    public void getUserDetails(Principal p, Model m) {
        if (p != null) {
            String email = p.getName();
            UserDtls userDtls = userService.getUserByEmail(email);
            m.addAttribute("user", userDtls);
            Integer countCart = cartService.getCountCart(userDtls.getId());
            m.addAttribute("countCart", countCart);
        }

        List<Category> allActiveCategory = categoryService.getAllActiveCategory();
        m.addAttribute("categorys", allActiveCategory);
    }
	
	
	
	@GetMapping("/")
	public String index() {
		return "owner/index";
	}
	
	@GetMapping("/loadAddProduct")
	public String loadAddProduct(Model m) {
		List<Category> categories = categoryService.getAllCategory();
		m.addAttribute("categories", categories);
		return "owner/add_product";
	}
	
	@PostMapping("/saveProduct")
	public String saveProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image,
			HttpSession session) throws IOException {

		String imageName = image.isEmpty() ? "default.jpg" : image.getOriginalFilename();

		product.setImage(imageName);
		product.setDiscount(0);
		product.setDiscountPrice(product.getPrice());
		Product saveProduct = productService.saveProduct(product);

		if (!ObjectUtils.isEmpty(saveProduct)) {

			File saveFile = new ClassPathResource("static/img").getFile();

			Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product_img" + File.separator
					+ image.getOriginalFilename());

			// System.out.println(path);
			Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

			session.setAttribute("succMsg", "Product Saved Success");
		} else {
			session.setAttribute("errorMsg", "something wrong on server");
		}

		return "redirect:/owner/loadAddProduct";
	}
	
	
	@GetMapping("/products")
	public String loadViewProduct(Model m, @RequestParam(defaultValue = "") String ch,
			@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {

		List<Product> products = null;
		if (ch != null && ch.length() > 0) {
			products = productService.searchProduct(ch);
		} else {
			products = productService.getAllProducts();
		}
		m.addAttribute("products", products);

		Page<Product> page = null;
		if (ch != null && ch.length() > 0) {
			page = productService.searchProductPagination(pageNo, pageSize, ch);
		} else {
			page = productService.getAllProductsPagination(pageNo, pageSize);
		}
		m.addAttribute("products", page.getContent());

		m.addAttribute("pageNo", page.getNumber());
		m.addAttribute("pageSize", pageSize);
		m.addAttribute("totalElements", page.getTotalElements());
		m.addAttribute("totalPages", page.getTotalPages());
		m.addAttribute("isFirst", page.isFirst());
		m.addAttribute("isLast", page.isLast());

		return "owner/products";
	}
	
	
	
	
	
//	@GetMapping("/products")
//    public String loadViewProduct(Model m, @RequestParam(defaultValue = "") String ch,
//            @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
//            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, Principal principal) {
//
//        UserDtls user = userService.getUserByEmail(principal.getName());
//        List<Product> products;
//
//        if (ch != null && ch.length() > 0) {
//            products = productService.searchProduct(ch);
//        } else {
//            if (user.getRole().equals("ROLE_OWNER")) {
//                // Owner can only view their products
//                products = productService.getProductsByOwner(user.getId());
//            } else {
//                products = productService.getAllProducts();
//            }
//        }
//
//        m.addAttribute("products", products);
//
//        Page<Product> page = null;
//        if (ch != null && ch.length() > 0) {
//            page = productService.searchProductPagination(pageNo, pageSize, ch);
//        } else {
//            if (user.getRole().equals("ROLE_OWNER")) {
//                page = productService.getProductsByOwnerPagination(user.getId(), pageNo, pageSize);
//            } else {
//                page = productService.getAllProductsPagination(pageNo, pageSize);
//            }
//        }
//
//        m.addAttribute("products", page.getContent());
//
//        m.addAttribute("pageNo", page.getNumber());
//        m.addAttribute("pageSize", pageSize);
//        m.addAttribute("totalElements", page.getTotalElements());
//        m.addAttribute("totalPages", page.getTotalPages());
//        m.addAttribute("isFirst", page.isFirst());
//        m.addAttribute("isLast", page.isLast());
//
//        return "owner/products";
//    }

	@GetMapping("/deleteProduct/{id}")
	public String deleteProduct(@PathVariable int id, HttpSession session) {
		Boolean deleteProduct = productService.deleteProduct(id);
		if (deleteProduct) {
			session.setAttribute("succMsg", "Product delete success");
		} else {
			session.setAttribute("errorMsg", "Something wrong on server");
		}
		return "redirect:/owner/products";
	}

	@GetMapping("/editProduct/{id}")
	public String editProduct(@PathVariable int id, Model m) {
		m.addAttribute("product", productService.getProductById(id));
		m.addAttribute("categories", categoryService.getAllCategory());
		return "owner/edit_product";
	}

	@PostMapping("/updateProduct")
	public String updateProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image,
			HttpSession session, Model m) {

		if (product.getDiscount() < 0 || product.getDiscount() > 100) {
			session.setAttribute("errorMsg", "invalid Discount");
		} else {

			Product updateProduct = productService.updateProduct(product, image);
			if (!ObjectUtils.isEmpty(updateProduct)) {
				session.setAttribute("succMsg", "Product update success");
			} else {
				session.setAttribute("errorMsg", "Something wrong on server");
			}
		}

		return "redirect:/owner/editProduct/" + product.getId();
	}

	@GetMapping("/users")
	public String getAllUsers(Model m, @RequestParam Integer type) {
		List<UserDtls> users = null;
		if (type == 1) {
			users = userService.getUsers("ROLE_USER");
		} else {
			users = userService.getUsers("ROLE_ADMIN");
		}
		m.addAttribute("userType", type);
		m.addAttribute("users", users);
		return "/owner/users";
	}
	
	@GetMapping("/orders")
	public String getAllOrders(Model m, @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
		List<ProductOrder> allOrders = orderService.getAllOrders();
		m.addAttribute("orders", allOrders);
		m.addAttribute("srch", false);

		Page<ProductOrder> page = orderService.getAllOrdersPagination(pageNo, pageSize);
		m.addAttribute("orders", page.getContent());
		m.addAttribute("srch", false);

		m.addAttribute("pageNo", page.getNumber());
		m.addAttribute("pageSize", pageSize);
		m.addAttribute("totalElements", page.getTotalElements());
		m.addAttribute("totalPages", page.getTotalPages());
		m.addAttribute("isFirst", page.isFirst());
		m.addAttribute("isLast", page.isLast());

		return "/owner/orders";
	}
	
	//after change this code to use
//	@GetMapping("/orders")
//    public String getAllOrders(Model m, @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
//            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, Principal principal) {
//
//        UserDtls user = userService.getUserByEmail(principal.getName());
//        List<ProductOrder> allOrders;
//        Page<ProductOrder> page;
//
//        if (user.getRole().equals("ROLE_OWNER")) {
//            // Owner can view all orders of their products
//            allOrders = orderService.getAllOrdersByOwner(user.getId());
//            page = orderService.getAllOrdersByOwnerPagination(user.getId(), pageNo, pageSize);
//        } else {
//            // Users can only view their own orders
//            allOrders = orderService.getOrdersByUser(user.getId());
//            page = orderService.getOrdersByUserPagination(user.getId(), pageNo, pageSize);
//        }
//
//        m.addAttribute("orders", allOrders);
//        m.addAttribute("srch", false);
//
//        m.addAttribute("orders", page.getContent());
//        m.addAttribute("srch", false);
//
//        m.addAttribute("pageNo", page.getNumber());
//        m.addAttribute("pageSize", pageSize);
//        m.addAttribute("totalElements", page.getTotalElements());
//        m.addAttribute("totalPages", page.getTotalPages());
//        m.addAttribute("isFirst", page.isFirst());
//        m.addAttribute("isLast", page.isLast());
//
//        return "/owner/orders";
//    }
//	
	@PostMapping("/update-order-status")
	public String updateOrderStatus(@RequestParam Integer id, @RequestParam Integer st, HttpSession session) {

		OrderStatus[] values = OrderStatus.values();
		String status = null;

		for (OrderStatus orderSt : values) {
			if (orderSt.getId().equals(st)) {
				status = orderSt.getName();
			}
		}

		ProductOrder updateOrder = orderService.updateOrderStatus(id, status);

		try {
			commonUtil.sendMailForProductOrder(updateOrder, status);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!ObjectUtils.isEmpty(updateOrder)) {
			session.setAttribute("succMsg", "Status Updated");
		} else {
			session.setAttribute("errorMsg", "status not updated");
		}
		return "redirect:/owner/orders";
	}
	
	
//	search orders
	
	
	
	@PostMapping("/save-owner")
	public String saveowner(@ModelAttribute UserDtls user, @RequestParam("img") MultipartFile file, HttpSession session)
			throws IOException {

		String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
		user.setProfileImage(imageName);
		UserDtls saveUser = userService.saveOwner(user);

		if (!ObjectUtils.isEmpty(saveUser)) {
			if (!file.isEmpty()) {
				File saveFile = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator
						+ file.getOriginalFilename());

				System.out.println(path);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}
			session.setAttribute("succMsg", "Register successfully");
		} else {
			session.setAttribute("errorMsg", "something wrong on server");
		}

		return "redirect:/owner/add-owner";
	}

	@GetMapping("/profile")
	public String profile() {
		return "/owner/profile";
	}

	@PostMapping("/update-profile")
	public String updateProfile(@ModelAttribute UserDtls user, @RequestParam MultipartFile img, HttpSession session) {
		UserDtls updateUserProfile = userService.updateUserProfile(user, img);
		if (ObjectUtils.isEmpty(updateUserProfile)) {
			session.setAttribute("errorMsg", "Profile not updated");
		} else {
			session.setAttribute("succMsg", "Profile Updated");
		}
		return "redirect:/owner/profile";
	}

	@PostMapping("/change-password")
	public String changePassword(@RequestParam String newPassword, @RequestParam String currentPassword, Principal p,
			HttpSession session) {
		UserDtls loggedInUserDetails = commonUtil.getLoggedInUserDetails(p);

		boolean matches = passwordEncoder.matches(currentPassword, loggedInUserDetails.getPassword());

		if (matches) {
			String encodePassword = passwordEncoder.encode(newPassword);
			loggedInUserDetails.setPassword(encodePassword);
			UserDtls updateUser = userService.updateUser(loggedInUserDetails);
			if (ObjectUtils.isEmpty(updateUser)) {
				session.setAttribute("errorMsg", "Password not updated !! Error in server");
			} else {
				session.setAttribute("succMsg", "Password Updated sucessfully");
			}
		} else {
			session.setAttribute("errorMsg", "Current Password incorrect");
		}

		return "redirect:/owner/profile";
	}

}














//
//@Controller
//@RequestMapping("/owner")
//public class OwnerController {
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private CommonUtil commonUtil;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Autowired
//    private CartService cartService;
//
//    @Autowired
//    private CategoryService categoryService;
//
//    @Autowired
//    private ProductService productService;
//
//    @Autowired
//    private OrderService orderService;
//
//    @ModelAttribute
//    public void getUserDetails(Principal p, Model m) {
//        if (p != null) {
//            String email = p.getName();
//            UserDtls userDtls = userService.getUserByEmail(email);
//            m.addAttribute("user", userDtls);
//            Integer countCart = cartService.getCountCart(userDtls.getId());
//            m.addAttribute("countCart", countCart);
//        }
//
//        List<Category> allActiveCategory = categoryService.getAllActiveCategory();
//        m.addAttribute("categorys", allActiveCategory);
//    }
//
//    @GetMapping("/products")
//    public String loadViewProduct(Model m, @RequestParam(defaultValue = "") String ch,
//            @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
//            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, Principal principal) {
//
//        UserDtls user = userService.getUserByEmail(principal.getName());
//        List<Product> products;
//
//        if (ch != null && ch.length() > 0) {
//            products = productService.searchProduct(ch);
//        } else {
//            if (user.getRole().equals("ROLE_OWNER")) {
//                // Owner can only view their products
//                products = productService.getProductsByOwner(user.getId());
//            } else {
//                products = productService.getAllProducts();
//            }
//        }
//
//        m.addAttribute("products", products);
//
//        Page<Product> page = null;
//        if (ch != null && ch.length() > 0) {
//            page = productService.searchProductPagination(pageNo, pageSize, ch);
//        } else {
//            if (user.getRole().equals("ROLE_OWNER")) {
//                page = productService.getProductsByOwnerPagination(user.getId(), pageNo, pageSize);
//            } else {
//                page = productService.getAllProductsPagination(pageNo, pageSize);
//            }
//        }
//
//        m.addAttribute("products", page.getContent());
//
//        m.addAttribute("pageNo", page.getNumber());
//        m.addAttribute("pageSize", pageSize);
//        m.addAttribute("totalElements", page.getTotalElements());
//        m.addAttribute("totalPages", page.getTotalPages());
//        m.addAttribute("isFirst", page.isFirst());
//        m.addAttribute("isLast", page.isLast());
//
//        return "owner/products";
//    }
//
//    @GetMapping("/orders")
//    public String getAllOrders(Model m, @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
//            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, Principal principal) {
//
//        UserDtls user = userService.getUserByEmail(principal.getName());
//        List<ProductOrder> allOrders;
//        Page<ProductOrder> page;
//
//        if (user.getRole().equals("ROLE_OWNER")) {
//            // Owner can view all orders of their products
//            allOrders = orderService.getAllOrdersByOwner(user.getId());
//            page = orderService.getAllOrdersByOwnerPagination(user.getId(), pageNo, pageSize);
//        } else {
//            // Users can only view their own orders
//            allOrders = orderService.getOrdersByUser(user.getId());
//            page = orderService.getOrdersByUserPagination(user.getId(), pageNo, pageSize);
//        }
//
//        m.addAttribute("orders", allOrders);
//        m.addAttribute("srch", false);
//
//        m.addAttribute("orders", page.getContent());
//        m.addAttribute("srch", false);
//
//        m.addAttribute("pageNo", page.getNumber());
//        m.addAttribute("pageSize", pageSize);
//        m.addAttribute("totalElements", page.getTotalElements());
//        m.addAttribute("totalPages", page.getTotalPages());
//        m.addAttribute("isFirst", page.isFirst());
//        m.addAttribute("isLast", page.isLast());
//
//        return "/owner/orders";
//    }
//}
//
