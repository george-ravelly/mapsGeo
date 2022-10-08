package com.example.mapsgeo.routes;

import com.example.mapsgeo.handler.GeojsonFeaturesHandler;
import org.springframework.beans.factory.annotation.Autowired;

public class LayerRouter {
    private final GeojsonFeaturesHandler handler;

    @Autowired
    public LayerRouter(GeojsonFeaturesHandler handler) {
        this.handler = handler;
    }
}
