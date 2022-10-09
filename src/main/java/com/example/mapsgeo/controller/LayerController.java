package com.example.mapsgeo.controller;

import com.example.mapsgeo.document.Layer;
import com.example.mapsgeo.service.LayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class LayerController {
    private LayerService service;
    private String pathFile;

    @Autowired
    public LayerController(@Value("${app.file.path}") String pathFile, LayerService service) {
        this.pathFile = pathFile;
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

    @PostMapping(value = "/file/{userId}/{name}")
    public Mono upload(
            @PathVariable("name") String name,
            @PathVariable("userId") String userId,
            @RequestPart("file") FilePart file
    ){
        var location = pathFile + UUID.randomUUID() + ".json";
        if (!typeFile(file.filename()).equals("geojson")) {
            return Mono.just(ResponseEntity.badRequest().body("Tipo de arquivo incorreto!"));
        }
        try {
            file.transferTo(Path.of(location)).blockOptional();
            return service.saveByFile(userId, name, location).then(
                    Mono.just(ResponseEntity.ok().body("Arquivo salvo com sucesso"))
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Mono.just(ResponseEntity.internalServerError().body("Erro ao tentar salvar o arquivo!"));
    }

    private String typeFile (String name) {
        var i = name.lastIndexOf(".");
        return name.substring(i + 1);
    }
}
