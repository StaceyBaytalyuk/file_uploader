package uploadingfiles.sql.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "File")
public class File {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "path", length = 1000)
    private String path;

    @Column(name = "size")
    private long size;

    @Column(name = "upload_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime dateTimeUploaded;

    public File(String name, long size) {
        this.name = name;
        this.size = size;
        this.path = "upload-dir";
        this.dateTimeUploaded = LocalDateTime.now();
    }

    public File(String name, String path, long size) {
        this.name = name;
        this.size = size;
        this.path = path;
        this.dateTimeUploaded = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "File{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", size=" + size +
                "bytes, dateTimeUploaded=" + dateTimeUploaded.toString() +
                '}';
    }

    public long getKilobytes() {
        return (long) Math.ceil(size / 1024.);
    }

    public String getDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return dateTimeUploaded.format(formatter);
    }
}
