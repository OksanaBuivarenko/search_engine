package searchengine.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "lemma")
public class Lemma {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name="site_id")
    private Site site;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String lemma;

    @Column(columnDefinition = "INT NOT NULL")
    private int frequency;

    @OneToMany(mappedBy="lemma", cascade = CascadeType.ALL)
    private List<IndexEntity> indexes;
}
