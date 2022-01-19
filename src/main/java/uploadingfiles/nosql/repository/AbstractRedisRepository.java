package uploadingfiles.nosql.repository;

import java.util.Map;
import java.util.Optional;

public interface AbstractRedisRepository<T> {
    Map<Object, Object> findAll();

    void add(T obj);

    void delete(String name);

    Optional<T> findByName(String name);

    boolean hasKey(String name);
}
