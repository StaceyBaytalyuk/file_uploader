package uploadingfiles.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uploadingfiles.sql.model.File;
import uploadingfiles.sql.repository.FileRepo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class StatisticsController {
    private FileRepo fileRepo;

    public StatisticsController(FileRepo fileRepo) {
        this.fileRepo = fileRepo;
    }

    @GetMapping("statistics")
    public String statistics(Map<String, Object> model) {
        List<File> files = (ArrayList<File>) fileRepo.findAll();
        model.put("files", files);
        return "statistics";
    }

    @GetMapping("files_search")
    public String filesSearch() {
        return "files_search";
    }

    @PostMapping("searchFilesByDateRange")
    public String searchFilesByDateRange(@RequestParam String min, @RequestParam String max, Map<String, Object> model) {
        if ( !min.isEmpty() && !max.isEmpty() ) {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
            LocalDateTime localMin = LocalDateTime.parse(min, formatter);
            LocalDateTime localMax = LocalDateTime.parse(max, formatter);

            List<File> files = fileRepo.findAllByDateTimeUploadedBetween(localMin, localMax.plusMinutes(1)); // min и max включительно
            model.put("files", files);
            if ( files.isEmpty() ) {
                model.put("message", "Не найдено файлы за этот период");
            }
        } else {
            model.put("message", "Неправильно введены данные");
        }
        return filesSearch();
    }

    @PostMapping("searchFilesByName")
    public String searchFilesByName(@RequestParam String name, Map<String, Object> model) {
        if ( !name.isEmpty() ) {
            List<File> files = fileRepo.findAllByName(name);
            model.put("files", files);
            if ( files.isEmpty() ) {
                model.put("message", "Не найдено");
            }
        } else {
            model.put("message", "Введите имя файла");
        }
        return filesSearch();
    }

    @PostMapping("searchFileById")
    public String searchFileById(@RequestParam Integer id, Map<String, Object> model) {
        if ( id!=null && fileRepo.existsById(id) ) {
            File file = fileRepo.findById(id).orElse(new File());
            model.put("files", file);
        } else {
            model.put("message", "Нет файла с таким id");
        }
        return filesSearch();
    }
}
