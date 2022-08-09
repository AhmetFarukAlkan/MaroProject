package com.MaroTekTimesheets.MaroProject.Service.Impl;

import com.MaroTekTimesheets.MaroProject.Dto.CustomerDto;
import com.MaroTekTimesheets.MaroProject.Entity.Customer;
import com.MaroTekTimesheets.MaroProject.Entity.Timesheet;
import com.MaroTekTimesheets.MaroProject.Repo.CustomerRepository;
import com.MaroTekTimesheets.MaroProject.Repo.TimesheetRepository;
import com.MaroTekTimesheets.MaroProject.Service.CustomerService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
@RequiredArgsConstructor
public class CustomerImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    private final TimesheetRepository timesheetRepository;

    @Override
    @Transactional
    public CustomerDto CreateCustomer(CustomerDto customerDto) {
        final Customer customer = new Customer();
        customer.setName(customerDto.getName());
        customer.setLocation(customerDto.getLocation());
        customer.setCreateUser(customerDto.getCreateUser());
        customer.setCreateDate(new Date());
        customer.setStatus(customerDto.getStatus());
        final Customer customerDb = customerRepository.save(customer);
        customerDto.setId(customerDb.getId());
        return customerDto;
    }

    @Override
    public Customer saveCustomer(Customer customer){
        final Customer customerDb = customerRepository.save(customer);
        return customerDb;
    }

    @Override
    public List<CustomerDto> GetCustomers() {
        List<Customer> customers = customerRepository.findAll();
        Collections.sort(customers);
        List<CustomerDto> customerDtos = new ArrayList<>();
        customers.forEach(it ->{
            CustomerDto customerDto = new CustomerDto();
            customerDto.setId(it.getId());
            customerDto.setLocation(it.getLocation());
            String pattern = "dd-MM-yyyy";
            SimpleDateFormat simpleDateFormat =new SimpleDateFormat(pattern, new Locale("tr", "TR"));
            customerDto.setCreateDate(simpleDateFormat.format(it.getCreateDate()));
            customerDto.setName(it.getName());
            customerDto.setStatus(it.getStatus());
            customerDto.setCreateUser(it.getCreateUser());
            customerDtos.add(customerDto);
        });
        return customerDtos;
    }
    
    @Override
    public List<CustomerDto> GetActiveCustomers() {
        List<Customer> customers = customerRepository.findAll();
        Collections.sort(customers);
        List<CustomerDto> customerDtos = new ArrayList<>();
        customers.forEach(it ->{
            if (it.getStatus().equals("A")){
                CustomerDto customerDto = new CustomerDto();
                customerDto.setId(it.getId());
                customerDto.setLocation(it.getLocation());
                String pattern = "dd-MM-yyyy";
                SimpleDateFormat simpleDateFormat =new SimpleDateFormat(pattern, new Locale("tr", "TR"));
                customerDto.setCreateDate(simpleDateFormat.format(it.getCreateDate()));
                customerDto.setName(it.getName());
                customerDto.setStatus(it.getStatus());
                customerDto.setCreateUser(it.getCreateUser());
                customerDtos.add(customerDto);
            }
        });
        return customerDtos;
    }
    
    @Override
    public void UpdateCustomer(long id, Customer customer1){
        Customer customer = getCustomerById(id);
        //customer.setCreateDate(new Date());
        customer.setCreateUser(customer1.getCreateUser());
        customer.setLocation(customer1.getLocation());
        customer.setName(customer1.getName());
        customerRepository.save(customer);
    }

    @Override
    public void Delete(Customer customer) {
        customerRepository.delete(customer);
    }

    @Override
    public Customer getCustomerById(long id){
        return   customerRepository.findAll().stream()
                .filter(customer -> customer.getId() == id)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    @Override
    public Customer getCustomerByName(String Name){
        return   customerRepository.findAll().stream()
                .filter(c -> c.getName().equals(Name))
                .findFirst().orElse(null);
    }

    @Override
    public Timesheet CustomerControl(Long id){
        return timesheetRepository.findAll().stream()
                .filter(timesheet -> timesheet.getCustomer().getId() == id)
                .findFirst().orElse(null);
    }

}
