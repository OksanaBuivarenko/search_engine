package searchengine.services;

import searchengine.model.Page;
import searchengine.model.Site;

import java.io.IOException;
import java.util.List;

public interface PageService {
    List<Page> getPageListBySite(Site site);

    Page getPageByPath(String path);

    void deletePage(Page page);

    Page createPage(int statusCode, String content, String path, Site site);

    String getSiteUrlFromPageUrl(String url);

    void updatePage(int statusCode, String content, String path);

    boolean isValid(Page page);

    void createPageLemmasAndIndexes(Page page, Site site) throws IOException;

    int getAllPageCount();

}
