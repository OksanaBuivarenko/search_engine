package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.model.IndexEntity;
import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.model.Site;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PageServiceImpl implements PageService{
    private final PageRepository pageRepository;
    private final SiteRepository siteRepository;
    private final LemmaService lemmaService;
    private final IndexEntityService indexEntityService;

    @Override
    public List<Page> getPageListBySite(Site site) {
        return pageRepository.findPageBySite(site);
    }

    @Override
    public Page getPageByPath(String path) {
        return pageRepository.findPageByPath(path);
    }

    @Override
    public void deletePage(Page page) {
        for(IndexEntity index: page.getIndexes()){
          lemmaService.decrementLemma(index.getLemma());
          indexEntityService.deleteIndexEntity(index);
        }
        pageRepository.delete(page);
    }

    @Override
    public synchronized Page createPage(int statusCode, String content, String path, Site site){
        if (getPageByPath(path) == null) {
            Page page = new Page();
            page.setCode(statusCode);
            page.setContent(content);
            page.setPath(path);
            page.setSite(site);
            pageRepository.save(page);
            site.setStatusTime(LocalDateTime.now());
            siteRepository.save(site);
            return  page;
        }
        return null;
    }

    @Override
    public String getSiteUrlFromPageUrl(String url){
        return  url.substring(0,url.indexOf("/", 9));
    }

    @Override
    public void updatePage(int statusCode, String content, String path) {
        Page page = getPageByPath(path);
        page.setCode(statusCode);
        page.setContent(content);
        pageRepository.save(page);
    }

    @Override
    public boolean isValid(Page page) {
        return page.getCode()<400;
    }

    @Override
    public void createPageLemmasAndIndexes(Page page, Site site) throws IOException {
        if (isValid(page)){
            HashMap<String, Integer> lemmasCountMap = lemmaService.getLemmasCountMap(lemmaService.deleteTags(page.getContent()));
            for (Map.Entry entry: lemmasCountMap.entrySet()) {
                Lemma lemma = lemmaService.addLemma(entry.getKey().toString(), site);
                IndexEntity indexEntity = indexEntityService.addIndexEntity(page, lemma, lemmasCountMap.get(entry.getKey()));
            }
        }
        else System.out.println("Код страницы " + page.getPath() + " = " + page.getCode());
    }

    @Override
    public int getAllPageCount() {
        return pageRepository.findAll().size();
    }



}
