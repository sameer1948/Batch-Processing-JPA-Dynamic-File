package com.spring.batch.jpa.dynamic.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.batch.jpa.dynamic.entity.Customer;

public interface Customer_Repository extends JpaRepository<Customer, Integer>{

}
