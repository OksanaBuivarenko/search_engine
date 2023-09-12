package searchengine.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "page", indexes = @Index(columnList = "path"))
public class Page {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name="site_id")
    private Site site;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String path;

    @Column(columnDefinition = "INT NOT NULL")
    private int code;

    @Column(columnDefinition = "MEDIUMTEXT NOT NULL")
    private String content;

    @OneToMany(mappedBy="page")
    private List<IndexEntity> indexes;

}
