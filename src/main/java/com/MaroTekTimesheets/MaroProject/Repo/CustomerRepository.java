package com.MaroTekTimesheets.MaroProject.Repo;

import com.MaroTekTimesheets.MaroProject.Entity.Customer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

}
