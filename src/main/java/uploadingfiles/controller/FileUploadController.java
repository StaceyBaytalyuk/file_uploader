package uploadingfiles.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import uploadingfiles.csv.CsvProcessor;
import uploadingfiles.nosql.repository.FileRedisRepo;
import uploadingfiles.sql.model.Person;
import uploadingfiles.sql.repository.FileRepo;
import uploadingfiles.sql.repository.PersonRepo;
import uploadingfiles.storage.StorageFileNotFoundException;
import uploadingfiles.storage.StorageService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static uploadingfiles.util.Constants.CSV_HEADERS_PERSON;

@Controller
public class FileUploadController {
    private final StorageService storageService;
    private FileRedisRepo fileRedisRepo;
    private FileRepo filePostgresRepo;
    private PersonRepo peopleRepo;

    @Autowired
    public FileUploadController(StorageService storageService, FileRedisRepo fileRedisRepo, FileRepo filePostgresRepo, PersonRepo peopleRepo) {
        this.storageService = storageService;
        this.fileRedisRepo = fileRedisRepo;
        this.filePostgresRepo = filePostgresRepo;
        this.peopleRepo = peopleRepo;
    }

    @GetMapping("upload_form")
    public String uploadForm() {
        return "upload_form";
    }

    @GetMapping
    public String uploadForm(Map<String, Object> model) {
        List<Object> files = new ArrayList<>(fileRedisRepo.findAll().values());
        model.put("files", files);
        return uploadForm();
    }

    @PostMapping("/")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, Map<String, Object> model) {
        if ( checkFile(file) ) {
            if ( !isAlreadySaved(file) ) {
                storageService.store(file);
                saveToRedis(file);
                saveToPostgres(file);
            }
            model.put("message", file.getOriginalFilename() + " успешно загружен!");
        } else {
            model.put("message", "Невозможно прочитать данные из " + file.getOriginalFilename() + ", неподходящие колонки или тип файла");
        }

        return uploadForm(model);
    }

    private void saveToRedis(MultipartFile file) {
        fileRedisRepo.add(new uploadingfiles.nosql.model.File(file.getOriginalFilename()));
    }

    private void saveToPostgres(MultipartFile file) {
        // save info about file
        filePostgresRepo.save(new uploadingfiles.sql.model.File(file.getOriginalFilename(), file.getSize()));

        // save data from file
        List<Person> people = CsvProcessor.csvToBean(file);
        if ( !people.isEmpty() ) {
            peopleRepo.saveAll(people);
        }
    }

    private boolean isAlreadySaved(MultipartFile file) {
        return fileRedisRepo.hasKey(file.getOriginalFilename());
    }

    private boolean checkFile(MultipartFile file) {
        String extension = file.getOriginalFilename().split("\\.")[1];
        if ( extension.equals("csv") ) {
            return CsvProcessor.checkHeaders(file, CSV_HEADERS_PERSON);
        }
        return false;
    }

    @GetMapping("/")
    public String listUploadedFiles(Model model) {
        model.addAttribute("files", storageService.loadAll().map(
                path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                        "serveFile", path.getFileName().toString()).build().toUri().toString())
                .collect(Collectors.toList()));
        return "upload_form";
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }
}