package com.example.mapsgeo.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
public class Layer {
    @Id
    private String _id;
    private String path;
    private String name;
    private LocalDateTime createdAt;
    private String type;
    private List<Entity> features;

    public Layer(String type, List<Entity> features) {
        this.type = type;
        this.features = features;
    }

    public Layer(String type, String name, List<Entity> features) {
        this.name = name;
        this.createdAt = LocalDateTime.now();
        this.type = type;
        this.features = features;
    }

    public void setFeaturesId() {
        features.stream().map(feat -> {
            set_id("feature:" + UUID.randomUUID());
            if (feat.getProperties().get("OBJECTID") == null) {
                feat.getProperties().put("OBJECTID", UUID.randomUUID().toString());
            }
            return feat;
        });
    }
}
