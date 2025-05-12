package com.siemens.internship.controller;

import com.siemens.internship.model.Item;
import com.siemens.internship.service.ItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    /**
     * Test for the `getAllItems` method in the `ItemController` class.
     * Validates that all items are retrieved and properly returned as JSON with HTTP status 200.
     */
    @Test
    public void testGetAllItems_ReturnsListOfItems() throws Exception {
        // Create test data
        List<Item> items = new ArrayList<>();
        items.add(new Item(1L, "Item1", "Description1", "Status1", "a@domain.com"));
        items.add(new Item(2L, "Item2", "Description2", "Status2", "b@domain.com"));

        when(itemService.findAll()).thenReturn(items);

        // Prepare expected JSON in a more readable format
        String expectedJson = """
        [
            {
                "id": 1,
                "name": "Item1",
                "description": "Description1",
                "status": "Status1",
                "email": "a@domain.com"
            },
            {
                "id": 2,
                "name": "Item2",
                "description": "Description2",
                "status": "Status2",
                "email": "b@domain.com"
            }
        ]
        """;

        mockMvc.perform(get("/api/items")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }


    /**
     * Test for the `getAllItems` method in the `ItemController` class.
     * Validates that an empty list of items is properly returned as JSON with HTTP status 200.
     */
    @Test
    public void testGetAllItems_ReturnsEmptyList() throws Exception {
        when(itemService.findAll()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/items")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    /**
     * Test for the `createItem` method in the `ItemController` class.
     * Validates that an item is successfully created and returns HTTP status 201.
     */
    @Test
    public void testCreateItem_SuccessfullyCreatesItem() throws Exception {
        Item newItem = new Item(null, "NewItem", "NewDescription", "NewStatus", "valid@domain.com");
        Item savedItem = new Item(1L, "NewItem", "NewDescription", "NewStatus", "valid@domain.com");

        when(itemService.save(eq(newItem))).thenReturn(savedItem);

        String newItemJson = "{\"name\":\"NewItem\",\"description\":\"NewDescription\",\"status\":\"NewStatus\",\"email\":\"valid@domain.com\"}";
        String savedItemJson = "{\"id\":1,\"name\":\"NewItem\",\"description\":\"NewDescription\",\"status\":\"NewStatus\",\"email\":\"valid@domain.com\"}";

        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newItemJson))
                .andExpect(status().isCreated())
                .andExpect(content().json(savedItemJson));

        verify(itemService, times(1)).save(newItem);
    }


    /**
     * Test for the `getItemById` method in the `ItemController` class.
     * Validates that a single item is retrieved for a valid ID with HTTP status 200.
     */
    @Test
    public void testGetItemById_ReturnsItem() throws Exception {
        // Create test data
        Item item = new Item(1L, "Item1", "Description1", "Status1", "a@domain.com");

        when(itemService.findById(eq(1L))).thenReturn(Optional.of(item));

        String expectedJson = """
                {
                    "id": 1,
                    "name": "Item1",
                    "description": "Description1",
                    "status": "Status1",
                    "email": "a@domain.com"
                }
                """;

        mockMvc.perform(get("/api/items/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    /**
     * Test for the `getItemById` method in the `ItemController` class.
     * Validates that HTTP status 404 is returned for a non-existent ID.
     */
    @Test
    public void testGetItemById_ReturnsNotFound() throws Exception {
        when(itemService.findById(eq(1L))).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/items/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    /**
     * Test for the `updateItem` method in the `ItemController` class.
     * Validates that an existing item is successfully updated and returns HTTP status 200.
     */
    @Test
    public void testUpdateItem_SuccessfullyUpdatesItem() throws Exception {
        // Create test data
        Item existingItem = new Item(1L, "Item1", "Description1", "Status1", "a@domain.com");
        Item updatedItem = new Item(1L, "UpdatedItem", "UpdatedDescription", "UpdatedStatus", "updated@domain.com");

        when(itemService.findById(eq(1L))).thenReturn(Optional.of(existingItem));
        when(itemService.save(eq(updatedItem))).thenReturn(updatedItem);

        String updatedItemJson = """
                {
                    "name": "UpdatedItem",
                    "description": "UpdatedDescription",
                    "status": "UpdatedStatus",
                    "email": "updated@domain.com"
                }
                """;

        String expectedJson = """
                {
                    "id": 1,
                    "name": "UpdatedItem",
                    "description": "UpdatedDescription",
                    "status": "UpdatedStatus",
                    "email": "updated@domain.com"
                }
                """;

        mockMvc.perform(put("/api/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedItemJson))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));

        verify(itemService, times(1)).save(updatedItem);
    }

    /**
     * Test for the `updateItem` method in the `ItemController` class.
     * Validates that HTTP status 404 is returned when attempting to update a non-existent item.
     */
    @Test
    public void testUpdateItem_ReturnsNotFound() throws Exception {
        when(itemService.findById(eq(1L))).thenReturn(Optional.empty());
        String updatedItemJson = """
                {
                    "name": "UpdatedItem",
                    "description": "UpdatedDescription",
                    "status": "UpdatedStatus",
                    "email": "updated@domain.com"
                }
                """;

        mockMvc.perform(put("/api/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedItemJson))
                .andExpect(status().isNotFound());

        verify(itemService, times(0)).save(any(Item.class));
    }

    /**
     * Test for the `deleteItem` method in the `ItemController` class.
     * Validates that a valid item is successfully deleted and returns HTTP status 204.
     */
    @Test
    public void testDeleteItem_SuccessfullyDeletesItem() throws Exception {
        doNothing().when(itemService).deleteById(eq(1L));
        when(itemService.findById(eq(1L))).thenReturn(Optional.of(new Item(1L, "Item1", "Description1", "Status1", "a@domain.com")));

        mockMvc.perform(delete("/api/items/1"))
                .andExpect(status().isNoContent());

        verify(itemService, times(1)).deleteById(1L);
    }

    /**
     * Test for the `processItems` method in the `ItemController` class.
     * Validates that processed items are returned with HTTP status 202.
     */
    @Test
    public void testProcessItems_ReturnsAcceptedWithProcessedItems() throws Exception {
        // Create test data
        List<Item> processedItems = new ArrayList<>();
        processedItems.add(new Item(1L, "ProcessedItem1", "ProcessedDescription1", "ProcessedStatus1", "processed1@domain.com"));
        processedItems.add(new Item(2L, "ProcessedItem2", "ProcessedDescription2", "ProcessedStatus2", "processed2@domain.com"));

        when(itemService.processItemsAsync()).thenReturn(processedItems);

        String expectedJson = """
                [
                    {
                        "id": 1,
                        "name": "ProcessedItem1",
                        "description": "ProcessedDescription1",
                        "status": "ProcessedStatus1",
                        "email": "processed1@domain.com"
                    },
                    {
                        "id": 2,
                        "name": "ProcessedItem2",
                        "description": "ProcessedDescription2",
                        "status": "ProcessedStatus2",
                        "email": "processed2@domain.com"
                    }
                ]
                """;

        mockMvc.perform(get("/api/items/process")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andExpect(content().json(expectedJson));
    }

    /**
     * Test for the `processItems` method in the `ItemController` class.
     * Validates that an empty list of processed items is returned with HTTP status 202.
     */
    @Test
    public void testProcessItems_ReturnsEmptyListWhenNoItemsProcessed() throws Exception {
        when(itemService.processItemsAsync()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/items/process")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andExpect(content().json("[]"));
    }
}