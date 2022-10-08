package com.example.mapsgeo.service;

import com.example.mapsgeo.document.Layer;
import com.example.mapsgeo.document.User;
import com.example.mapsgeo.repository.LayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class LayerService {
    private final LayerRepository repository;
    private final ReactiveMongoTemplate template;

    public Mono<Layer> findById (String id) {
        return repository.findById(id);
    }

    public Flux<Map<String, String>> listAllByIds (List<String> ids) {
        return repository.findAllById(ids)
                .map(layer -> Map.of("name", layer.getName(), "path", layer.getPath()));
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
}
