package com.nt.course_service_lms.service;

import com.nt.course_service_lms.dto.inDTO.BundleInDTO;
import com.nt.course_service_lms.dto.inDTO.UpdateBundleInDTO;
import com.nt.course_service_lms.dto.outDTO.BundleOutDTO;
import com.nt.course_service_lms.dto.outDTO.StandardResponseOutDTO;

import java.util.List;

/**
 * Service interface for handling business logic related to Bundle entities.
 * <p>
 * Provides methods for creating, retrieving, updating, and deleting course bundles,
 * as well as checking for their existence by ID.
 * </p>
 */
public interface BundleService {

    /**
     * Creates a new bundle based on the provided {@link BundleInDTO}.
     *
     * @param bundleInDTO the data transfer object containing bundle details
     * @return the newly created {@link BundleOutDTO}
     */
    BundleOutDTO createBundle(BundleInDTO bundleInDTO);

    /**
     * Retrieves all existing bundles.
     *
     * @return a list of all {@link BundleOutDTO}
     */
    List<BundleOutDTO> getAllBundles();

    /**
     * Retrieves a bundle by its ID.
     *
     * @param id the ID of the bundle to retrieve
     * @return the {@link BundleOutDTO} if found
     */
    BundleOutDTO getBundleById(Long id);

    /**
     * Updates the details of an existing bundle.
     *
     * @param bundleId          the ID of the bundle to update
     * @param updateBundleInDTO the updated bundle data
     * @return success message
     */
    BundleOutDTO updateBundle(Long bundleId, UpdateBundleInDTO updateBundleInDTO);

    /**
     * Deletes the bundle with the given ID.
     *
     * @param id the ID of the bundle to delete
     */
    void deleteBundle(Long id);

    /**
     * Checks whether a bundle exists by its ID.
     *
     * @param id the ID to check
     * @return {@code true} if a bundle with the given ID exists, {@code false} otherwise
     */
    boolean existsByBundleId(Long id);

    /**
     * Counts the total number of bundles.
     *
     * @return the total count of bundles
     */
    long countBundles();

    /**
     * Retrieves the bundle name by bundle ID.
     *
     * @param bundleId the ID of the bundle
     * @return the bundle name
     */
    String getBundleNameById(Long bundleId);

    /**
     * Retrieves a list of existing bundle IDs from the provided list.
     *
     * @param bundleIds the list of bundle IDs to check
     * @return a list of existing bundle IDs
     */
    List<Long> findExistingIds(List<Long> bundleIds);

    /**
     * Get bundles by List of Ids.
     *
     * @param bundleIds
     * @return list of bundleOutDTO
     */
    StandardResponseOutDTO<List<BundleOutDTO>> getBundlesByIds(List<Long> bundleIds);
}
