package com.siemens.internship.repository;

import com.siemens.internship.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    /**
     * Retrieves the list of all item IDs from the Item entity.
     *
     * @return a list of item IDs as Long objects.
     */
    @Query("SELECT id FROM Item")
    List<Long> findAllIds();
}
