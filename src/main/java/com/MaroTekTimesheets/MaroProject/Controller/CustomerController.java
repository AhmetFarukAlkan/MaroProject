package com.MaroTekTimesheets.MaroProject.Controller;

import com.MaroTekTimesheets.MaroProject.Dto.CustomerDto;
import com.MaroTekTimesheets.MaroProject.Entity.Customer;
import com.MaroTekTimesheets.MaroProject.Entity.User;
import com.MaroTekTimesheets.MaroProject.Repo.CustomerRepository;
import com.MaroTekTimesheets.MaroProject.Repo.UserRepository;
import com.MaroTekTimesheets.MaroProject.Service.CustomerService;
import com.MaroTekTimesheets.MaroProject.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/home/customers")
@RequiredArgsConstructor
public class CustomerController {
    @Autowired
    private final CustomerService customerService;

    private final UserService userService;

    @Autowired
    private final com.MaroTekTimesheets.MaroProject.oauth2.Controller controller;

    @GetMapping("")
    public String showCustomerList(Model model, @AuthenticationPrincipal OAuth2User user){
        if (userService.isActive(user)){
            if (userService.ControlUser(user).getUser_role().getName().equals("ADMIN")){
                model.addAttribute("picture", user.getAttributes().get("picture"));
                model.addAttribute("user", userService.ControlUser(user));
                model.addAttribute("listCustomers", customerService.GetCustomers());
                return "customers";
            }else if (userService.ControlUser(user).getUser_role().getName().equals("USER")){
                model.addAttribute("picture", user.getAttributes().get("picture"));
                model.addAttribute("user", userService.ControlUser(user));
                return "redirect:/home";
            }
        }
        else {
            return "redirect:/passive-user";
        }
        return "redirect:/";
    }

    @GetMapping("/create")
    public String newCustomerForm(Model model, @AuthenticationPrincipal OAuth2User user){
        if (userService.isActive(user)){
            if (userService.ControlUser(user).getUser_role().getName().equals("ADMIN")){
                model.addAttribute("picture", user.getAttributes().get("picture"));
                model.addAttribute("user", userService.ControlUser(user));
                model.addAttribute("customer", new Customer());
                model.addAttribute("pagetitle", "Add New Customer");
                return "customer_form";
            }
            else if (userService.ControlUser(user).getUser_role().getName().equals("USER")){
                return "redirect:/home";
            }
        }
        else {
            return "redirect:/passive-user";
        }
        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    public String editCustomerForm(@PathVariable("id") Long id, Model model, @AuthenticationPrincipal OAuth2User user){
        if (userService.isActive(user)){
            if (userService.ControlUser(user).getUser_role().getName().equals("ADMIN")){
                model.addAttribute("picture", user.getAttributes().get("picture"));
                model.addAttribute("user", userService.ControlUser(user));
                Customer customer = customerService.getCustomerById(id);
                model.addAttribute("customer", customer);
                model.addAttribute("pagetitle", "Edit Customer");
                return "customer_form";
            }
            else if (userService.ControlUser(user).getUser_role().getName().equals("USER")){
                return "redirect:/home";
            }
        }
        else {
            return "redirect:/passive-user";
        }
        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id,Model model,RedirectAttributes ra, @AuthenticationPrincipal OAuth2User user){
        if (userService.isActive(user)){
            if (userService.ControlUser(user).getUser_role().getName().equals("ADMIN")){
                if (customerService.CustomerControl(id) != null){
                    ra.addFlashAttribute("error","This customer is currently in use.");
                    return "redirect:/home/customers";
                }
                Customer customer = customerService.getCustomerById(id);
                customerService.Delete(customer);
                model.addAttribute("listCustomers", customerService.GetCustomers());
                return "redirect:/home/customers";
            }
            else if (userService.ControlUser(user).getUser_role().getName().equals("USER")){
                return "redirect:/home";
            }
        }
        else {
            return "redirect:/passive-user";
        }
        return "redirect:/";
    }

//    @GetMapping("/get")
//    public ResponseEntity<List<CustomerDto>> GetCustomers(){
//        return ResponseEntity.ok(customerService.GetCustomers());
//    }

    @PostMapping
    public ResponseEntity<CustomerDto> CreateCustomer(@RequestBody CustomerDto customerDto){
        return ResponseEntity.ok(customerService.CreateCustomer(customerDto));
    }

    @PostMapping({"/save"})
    public String saveCustomer(Customer customer, RedirectAttributes ra, @AuthenticationPrincipal OAuth2User user){
        if(customer.getName().equals("") || customer.getLocation().equals("")){
            if (customer.getId() == null){
                ra.addFlashAttribute("error","All fields are required.");
                return "redirect:/home/customers/create";
            }else {
                ra.addFlashAttribute("error","All fields are required.");
                return "redirect:/home/customers/edit/"+customer.getId();
            }
        }
        else{
            if (customer.getId() == null){
                customer.setStatus("A");
                customer.setCreateDate(new Date());
                customer.setCreateUser(String.valueOf(userService.ControlUser(user).getId()));
            }
            else {
                //for error
                customer.setCreateDate(customerService.getCustomerById(customer.getId()).getCreateDate());
                customer.setCreateUser(String.valueOf(userService.ControlUser(user).getId()));

            }

            customerService.saveCustomer(customer);
            return "redirect:/home/customers";
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> UpdateCustomer(@PathVariable(value = "id") long id, @RequestBody Customer customerDto){
        customerService.UpdateCustomer(id, customerDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
