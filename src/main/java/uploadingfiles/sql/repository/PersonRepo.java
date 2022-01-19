package uploadingfiles.sql.repository;

import org.springframework.data.repository.CrudRepository;
import uploadingfiles.sql.model.Person;

import java.util.List;

public interface PersonRepo extends CrudRepository<Person, Integer> {
    List<Person> findAllByName(String name);
    List<Person> findAllByAgeBetween(int min, int max);
}
