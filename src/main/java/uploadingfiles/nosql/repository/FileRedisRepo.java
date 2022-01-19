package uploadingfiles.nosql.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import uploadingfiles.nosql.model.File;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Optional;

@Repository
public class FileRedisRepo implements AbstractRedisRepository<File> {
    private static final String KEY = "File";
    private RedisTemplate<String, Object> redisTemplate;
    private HashOperations hashOperations;

    @Autowired
    public FileRedisRepo(RedisTemplate<String, Object> redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    private void init(){
        hashOperations = redisTemplate.opsForHash();
    }

    public void add(final File file) {
        hashOperations.put(KEY, file.getName(), file);
    }

    public void delete(final String name) {
        hashOperations.delete(KEY, name);
    }

    public Map<Object, Object> findAll(){
        return hashOperations.entries(KEY);
    }

    public boolean hasKey(final String name) {
        return hashOperations.hasKey(KEY, name);
    }

    public Optional<File> findByName(final String name){
        return Optional.ofNullable((File)hashOperations.get(KEY, name));
    }
}