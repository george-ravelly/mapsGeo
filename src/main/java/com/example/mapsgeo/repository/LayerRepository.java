package com.example.mapsgeo.repository;

import com.example.mapsgeo.document.Layer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface LayerRepository extends ReactiveCrudRepository<Layer, String> {
}
