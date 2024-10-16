package com.ecom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecom.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer>{

	Boolean existsByName(String name);

	public List<Category> findByIsActiveTrue();
	
	

}
