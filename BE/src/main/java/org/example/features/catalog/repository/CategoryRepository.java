package org.example.features.catalog.repository;

import org.example.features.catalog.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Category entity
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Find category by slug
     */
    Optional<Category> findBySlug(String slug);

    /**
     * Find all active categories
     */
    List<Category> findByIsActiveTrueOrderByDisplayOrderAsc();

    /**
     * Find all root categories (no parent)
     */
    @Query("SELECT c FROM Category c WHERE c.parent IS NULL AND c.isActive = true ORDER BY c.displayOrder ASC")
    List<Category> findRootCategories();

    /**
     * Find children of a category
     */
    List<Category> findByParentIdAndIsActiveTrueOrderByDisplayOrderAsc(Long parentId);
}
