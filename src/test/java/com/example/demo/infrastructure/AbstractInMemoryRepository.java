package com.example.demo.infrastructure;

import java.util.*;
import java.util.stream.Stream;

public abstract class AbstractInMemoryRepository<ID, DATA> {
    private final Map<ID, DATA> dataById;
    private int idIndex;
    private final List<ID> ids;

    protected AbstractInMemoryRepository(List<ID> ids) {
        dataById = new HashMap<>();
        idIndex = 0;
        this.ids = new ArrayList<>(ids);
    }

    public ID generateNewId() {
        ID id = ids.get(idIndex);
        idIndex++;
        return id;
    }

    protected void add(ID id, DATA data) {
        if(dataById.containsKey(id)) {
            throw new RuntimeException("data with id "+ id + " was already added");
        }

        dataById.put(id, data);
    }

    protected void update(ID id, DATA data) {
        dataById.put(id, data);
    }

    protected Stream<DATA> streamData() {
        return dataById.values().stream().map(this::copy);
    }

    public Optional<DATA> findById(ID id) {
        return Optional.ofNullable(dataById.get(id)).map(this::copy);
    }

    protected abstract DATA copy(DATA data);

    public void deleteAll() {
        dataById.clear();
        idIndex = 0;
    }

    public void setNextGeneratedIds(List<ID> nextGeneratedIds) {
        ids.addAll(idIndex, nextGeneratedIds);
    }

    public void setNextGeneratedId(ID nextGeneratedId) {
        setNextGeneratedIds(List.of(nextGeneratedId));
    }
}
