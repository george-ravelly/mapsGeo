package com.example.mapsgeo.handler;

import com.example.mapsgeo.document.Layer;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public class GeojsonFeaturesHandler<T> {
    private final ReactiveMongoTemplate mongoTemplate;

    @Autowired
    public GeojsonFeaturesHandler(ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    private Mono<ServerResponse> saveGeojson (ServerRequest request) {
        String path = request.pathVariable("collection");
        Mono<Layer> geojsonFeaturesMono = request.bodyToMono(Layer.class);
        return ServerResponse
                .ok().contentType(MediaType.APPLICATION_JSON)
                .body(mongoTemplate.insert(geojsonFeaturesMono, path).then(Mono.just("Cadastro realizado com sucesso!")), String.class);
    }

    private Mono<ServerResponse> listAll (ServerRequest request) {
        String path = request.pathVariable("path");
        Query query = new Query();
        query.fields().include("name", "path");
        return ServerResponse
                .ok().contentType(MediaType.APPLICATION_JSON)
                .body(mongoTemplate.find(
                        query,
                        Layer.class,
                        path
                ), Document.class);
    }
}
