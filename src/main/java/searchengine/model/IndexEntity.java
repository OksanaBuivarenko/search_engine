package searchengine.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "indexEntity")
public class IndexEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name="page_id")
    private Page page;

    @ManyToOne
    @JoinColumn(name="lemma_id")
    private Lemma lemma;

    @Column(columnDefinition = "FLOAT NOT NULL")
    private float indexRank;
}
