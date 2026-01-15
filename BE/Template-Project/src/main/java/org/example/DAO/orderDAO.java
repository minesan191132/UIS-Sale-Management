package org.example.DAO;

import org.example.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface orderDAO extends JpaRepository<Product,String> {
}
