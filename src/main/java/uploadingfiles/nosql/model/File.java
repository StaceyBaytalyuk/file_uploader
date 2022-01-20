package uploadingfiles.nosql.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

import static uploadingfiles.util.Constants.DEFAULT_UPLOAD_DIR;

@Getter
@Setter
@AllArgsConstructor
@RedisHash("File")
public class File implements Serializable {
    static final long serialVersionUID = 1L;

    @Id
    private String name;
    private String path = DEFAULT_UPLOAD_DIR;

    public File(String name) {
        this.name = name;
    }

    public File(uploadingfiles.sql.model.File file) {
        this.name = file.getName();
        this.path = getPath();
    }

    @Override
    public String toString() {
        return "File{ " + " name='" + name + ", path=" + path + " }";
    }
}