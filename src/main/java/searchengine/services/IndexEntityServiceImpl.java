package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.model.IndexEntity;
import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.repositories.IndexEntityRepository;

@Service
@RequiredArgsConstructor
public class IndexEntityServiceImpl implements IndexEntityService{
    private final IndexEntityRepository indexEntityRepository;

    @Override
    public IndexEntity addIndexEntity(Page page, Lemma lemma, Integer value) {
        IndexEntity indexEntity = new IndexEntity();
        indexEntity.setLemma(lemma);
        indexEntity.setPage(page);
        indexEntity.setIndexRank(value);
        indexEntityRepository.save(indexEntity);
        return indexEntity;
    }

    @Override
    public void deleteIndexEntity(IndexEntity index) {
        indexEntityRepository.delete(index);
    }

    @Override
    public IndexEntity getIndexEntityByPageAndLemma(Page page, Lemma lemma) {
        return indexEntityRepository.findByPageAndLemma(page, lemma);
    }
}
