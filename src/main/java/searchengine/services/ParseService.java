package searchengine.services;

import org.jsoup.Connection;
import searchengine.config.SitesList;
import searchengine.model.Site;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

public interface ParseService {
    String getTitle(String url) throws IOException;
    Connection.Response getConnection(String url) throws IOException;
    void startParse(Site site) throws IOException;
    void startParsePage(String url, Site site) throws InterruptedException, IOException;
    void stopParse(SitesList sites);

}
