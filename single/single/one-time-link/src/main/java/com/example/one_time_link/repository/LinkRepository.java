package com.example.one_time_link.repository;

import com.example.one_time_link.model.OneTimeLink;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class LinkRepository {

    private final Map<String, OneTimeLink> store = new ConcurrentHashMap<>();

    public void save(OneTimeLink link) {
        store.put(link.getToken(), link);
    }

    public Optional<OneTimeLink> find(String token) {
        return Optional.ofNullable(store.get(token));
    }
}