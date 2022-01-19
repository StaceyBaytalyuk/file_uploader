package uploadingfiles.sql.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Person")
public class Person {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "age")
    private int age;

    @Override
    public String toString() {
        return id + " " + name + ", " + age + " years old";
    }
}
