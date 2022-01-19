package uploadingfiles.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uploadingfiles.sql.model.Person;
import uploadingfiles.sql.repository.PersonRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class PeopleController {
    private PersonRepo peopleRepo;

    public PeopleController(PersonRepo peopleRepo) {
        this.peopleRepo = peopleRepo;
    }

    @GetMapping("people")
    public String people(Map<String, Object> model) {
        List<Person> people = (ArrayList<Person>) peopleRepo.findAll();
        model.put("people", people);
        return "people";
    }

    @GetMapping("people_search")
    public String peopleSearch() {
        return "people_search";
    }

    @PostMapping("searchPeopleByAgeRange")
    public String searchPeopleByAgeRange(@RequestParam Integer minAge, @RequestParam Integer maxAge, Map<String, Object> model) {
        if ( minAge!=null && maxAge!=null && minAge>0 && maxAge>0 && minAge<=maxAge ) {
            List<Person> people = peopleRepo.findAllByAgeBetween(minAge, maxAge+1); // min и max включительно
            model.put("people", people);

            if ( people.isEmpty() ) {
                model.put("message", "Люди такого возраста не найдены");
            }
        } else {
            model.put("message", "Неправильно введены данные");
        }
        return peopleSearch();
    }

    @PostMapping("searchPeopleByName")
    public String searchPeopleByName(@RequestParam String name, Map<String, Object> model) {
        if ( !name.isEmpty() ) {
            List<Person> people = peopleRepo.findAllByName(name);
            model.put("people", people);
            if ( people.isEmpty() ) {
                model.put("message", "Не найдено");
            }
        } else {
            model.put("message", "Введите имя");
        }
        return peopleSearch();
    }

    @PostMapping("searchPeopleById")
    public String searchPeopleById(@RequestParam Integer id, Map<String, Object> model) {
        if ( id!=null && peopleRepo.existsById(id) ) {
            Person person = peopleRepo.findById(id).orElse(new Person());
            model.put("people", person);
        } else {
            model.put("message", "Нет человека с таким id");
        }
        return peopleSearch();
    }
}
