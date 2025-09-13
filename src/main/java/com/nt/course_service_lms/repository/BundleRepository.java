package com.nt.course_service_lms.repository;

import com.nt.course_service_lms.entity.Bundle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing {@link Bundle} entities.
 * <p>
 * Provides basic CRUD operations and custom query methods for Bundle-related data.
 * Extends {@link JpaRepository} to leverage Spring Data JPA functionalities.
 * </p>
 *
 * <p>Custom methods include checks for existence by bundle name or ID.</p>
 */
@Repository
public interface BundleRepository extends JpaRepository<Bundle, Long> {

    /**
     * Checks if a bundle with the specified name exists in the database.
     *
     * @param bundleName the name of the bundle to check
     * @return {@code true} if a bundle with the given name exists, {@code false} otherwise
     */
    boolean existsByBundleName(String bundleName);

    /**
     * Checks if a bundle exists with the specified ID.
     *
     * @param id the ID of the bundle to check
     * @return {@code true} if a bundle with the given ID exists, {@code false} otherwise
     */
    boolean existsById(Long id);

    /**
     * Finds the 5 most recent bundles ordered by creation date.
     *
     * @return a list of the 5 most recently created bundles
     */
    List<Bundle> findTop5ByOrderByCreatedAtDesc();

    /**
     * Finds all bundles that are currently active.
     *
     * @param bundleIds the list of bundle IDs to check
     * @return a list of active bundles
     */
    @Query("SELECT b.bundleId FROM Bundle b WHERE b.bundleId IN :bundleIds")
    List<Long> findExistingIds(@Param("bundleIds") List<Long> bundleIds);

    /**
     * Find all the bundles in the table.
     *
     * @param bundleIds
     * @return list of bundle
     */
    List<Bundle> findByBundleIdIn(List<Long> bundleIds);


}
