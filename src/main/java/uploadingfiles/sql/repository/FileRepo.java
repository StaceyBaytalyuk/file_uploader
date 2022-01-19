package uploadingfiles.sql.repository;

import org.springframework.data.repository.CrudRepository;
import uploadingfiles.sql.model.File;

import java.time.LocalDateTime;
import java.util.List;

public interface FileRepo extends CrudRepository<File, Integer> {
    List<File> findAllByName(String name);
    List<File> findAllByDateTimeUploadedBetween(LocalDateTime min, LocalDateTime max);

}
