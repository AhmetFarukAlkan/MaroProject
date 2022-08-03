package com.MaroTekTimesheets.MaroProject.Service;

import com.MaroTekTimesheets.MaroProject.Dto.CustomerDto;
import com.MaroTekTimesheets.MaroProject.Entity.Customer;
import com.MaroTekTimesheets.MaroProject.Entity.Timesheet;

import java.util.List;

public interface CustomerService {
     CustomerDto CreateCustomer(CustomerDto customerDto);

     Customer saveCustomer(Customer customer);

     List<CustomerDto> GetCustomers() ;

     void UpdateCustomer(long id, Customer customer1);

     Customer getCustomerById(long id);

     Customer getCustomerByName(String Name);

     void Delete(Customer customer);

     Timesheet CustomerControl(Long id);
}
