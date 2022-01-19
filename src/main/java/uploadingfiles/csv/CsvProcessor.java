package uploadingfiles.csv;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import org.springframework.web.multipart.MultipartFile;
import uploadingfiles.sql.model.Person;

import java.io.*;
import java.util.*;

import static uploadingfiles.util.Constants.DEFAULT_UPLOAD_DIR;
import static uploadingfiles.util.Constants.SEPARATOR;

public class CsvProcessor {
    public static List<Person> csvToBean(MultipartFile file) {
        File filePath = new File(DEFAULT_UPLOAD_DIR + '/' + file.getOriginalFilename());
        writeToFile(file);
        Map<String, String> mapping = mappingPerson();

        HeaderColumnNameTranslateMappingStrategy<Person> strategy = new HeaderColumnNameTranslateMappingStrategy<>();
        strategy.setType(Person.class);
        strategy.setColumnMapping(mapping);

        try ( CSVReader csvReader = new CSVReader(new FileReader(filePath)) ) {
            Reader reader = new BufferedReader(new FileReader(filePath));
            CsvToBean csvToBean = new CsvToBeanBuilder(reader)
                    .withType(Person.class)
                    .withSeparator(SEPARATOR)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            List<Person> list = csvToBean.parse(strategy, csvReader);
            return list;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private static Map<String, String> mappingPerson() {
        Map<String, String> mapping = new HashMap<>();
        mapping.put("id", "id");
        mapping.put("name", "name");
        mapping.put("age", "age");
        return mapping;
    }

    private static void writeToFile(MultipartFile multipart) {
        File file = new File(DEFAULT_UPLOAD_DIR + '/' + multipart.getOriginalFilename());
        try (OutputStream os = new FileOutputStream(file)) {
            os.write(multipart.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkHeaders(MultipartFile file, final String[] headersToBe) {
        CSVReader csvReader;
        try {
            Reader reader = new InputStreamReader(file.getInputStream());
            csvReader = new CSVReader(reader, SEPARATOR);
            String[] headers = csvReader.readNext();
            return Arrays.equals(headersToBe, headers);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
