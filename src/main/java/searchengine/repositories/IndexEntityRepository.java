package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import searchengine.model.IndexEntity;
import searchengine.model.Lemma;
import searchengine.model.Page;

public interface IndexEntityRepository extends JpaRepository<IndexEntity, Integer> {
    IndexEntity findByPageAndLemma(Page page, Lemma lemma);
}
