package com.example.mapsgeo.service;

import com.example.mapsgeo.document.Entity;
import com.example.mapsgeo.document.Layer;
import com.example.mapsgeo.document.User;
import com.example.mapsgeo.repository.LayerRepository;
import lombok.RequiredArgsConstructor;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.FileReader;
import java.io.IOException;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class LayerService<T> {
    private final LayerRepository repository;
    private final ReactiveMongoTemplate template;

    public Mono<Layer> findById (String id) {
        return repository.findById(id);
    }

    public Flux<Map<String, String>> listAllByIds (List<String> ids) {
        return repository.findAllById(ids)
                .map(layer -> Map.of("name", layer.getName(), "id", layer.get_id()));
    }

    public Mono<Layer> save (String userId, String name, Layer layer) {
        if (layer.getName() == null) layer.setName(name);
        layer.setPath(deAccent(name));
        layer.setCreatedAt(LocalDateTime.now());
        layer.setFeaturesId();

        return repository.save(layer)
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(next -> {
                    Optional<User> user = template.findOne(
                            Query.query(Criteria.where("_id").is(userId)),
                            User.class,
                            "user").blockOptional();
                    user.get().getLayers().add(next.get_id());
                    template.save(user.get(), "user").blockOptional();
                });
    }

    public static String deAccent(String str) {
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern
                .matcher(nfdNormalizedString)
                .replaceAll("")
                .toLowerCase(Locale.ROOT)
                .replace(" ", "_");
    }

    public Mono<Layer> saveByFile(String userId, String name, String location) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(new FileReader(location));
        Layer layer;
        if (json.get("type").toString().equals("FeatureCollection")) {
            JSONArray arrayList = (JSONArray) json.get("features");
            layer = new Layer(json.get("type").toString(), arrayList);
        } else {
            List<Entity> list = new ArrayList<>();
            list.add(new Entity(
                    json.get("type").toString(),
                    json.get("geometry"),
                    (Map<String, T>) json.get("properties")));
            layer = new Layer("FeatureCollection", list);
        }
        return save(userId, name, layer);
    }
}
