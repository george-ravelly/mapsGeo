package com.example.mapsgeo.controller;

import com.example.mapsgeo.document.Layer;
import com.example.mapsgeo.service.LayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
public class LayerController {
    private LayerService service;

    @Autowired
    public LayerController(LayerService service) {
        this.service = service;
    }

    @PostMapping("/layer/{name}")
    public Mono<Layer> saveLayer (
            @PathVariable(value = "name") String name,
            @RequestParam("userId") String userId,
            @RequestBody Layer layer) {
        return service.save(userId, name, layer);
    }

    @GetMapping("layer")
    public Flux<Map<String, String>> listAllByIds (
            @RequestParam List<String> ids
    ) {
        return service.listAllByIds(ids);
    }

    @GetMapping("layer/{id}")
    public Mono<Layer> listByIds (
            @PathVariable("id") String id
    ) {
        return service.findById(id);
    }
}
