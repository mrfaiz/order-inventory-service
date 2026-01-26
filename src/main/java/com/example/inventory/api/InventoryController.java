package com.example.inventory.api;

import com.example.inventory.repository.InventoryItemRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(("/inventory"))
public class InventoryController {
    private final InventoryItemRepository repository;
    public InventoryController(InventoryItemRepository rep){
        repository = rep;
    }

    @GetMapping("/{productId}")
    public InventorResponse get(@PathVariable UUID productId){
        var item = repository.findByProduct_Id(productId)
                .orElseThrow(()-> new IllegalArgumentException("Inventor not found"));
        return  new InventorResponse(item.getId(), item.getQuantity());
    }

}
