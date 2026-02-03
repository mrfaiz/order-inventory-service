package com.example.inventory.api;

import com.example.inventory.domain.InventoryItem;
import com.example.inventory.repository.InventoryItemRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "Inventory", description = "Inventory read APIs")
@RestController
@RequestMapping(("/inventory"))
public class InventoryController {
    private final InventoryItemRepository repository;
    public InventoryController(InventoryItemRepository rep){
        repository = rep;
    }

    @Operation(summary = "Get inventory by product id", description = "Returns current quantity for the given product.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inventory found"),
            @ApiResponse(responseCode = "404", description = "Inventory not found", content = @Content)
    })
    @GetMapping("/{productId}")
    public InventoryResponse get(@PathVariable UUID productId){
        var item = repository.findByProduct_Id(productId)
                .orElseThrow(()-> new IllegalArgumentException("Inventor not found"));
        return  new InventoryResponse(item.getId(), item.getQuantity());
    }

    @Operation(summary = "List inventory", description = "Returns inventory for all products (DTO-based).")
    @ApiResponse(responseCode = "200", description = "List of inventory items")
    @GetMapping()
    public List<InventoryItem> list(){
        return repository.findAll();
    }

}
