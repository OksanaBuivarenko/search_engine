package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import searchengine.config.SiteConfig;
import searchengine.config.SitesList;
import searchengine.model.EnumStatus;
import searchengine.model.Site;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

@Service
@RequiredArgsConstructor
public class ParseServiceImpl implements ParseService{
    @Value("${connection.userAgent}")
    String userAgent;
    @Value("${connection.referrer}")
    String referrer;

    private final PageService pageService;
    private final SiteService siteService;
    List<ForkJoinPool> pools = new ArrayList<>();

    public String getTitle(String url) throws IOException {
        Connection.Response response = getConnection(url);
        Document doc = Jsoup.connect(url).get();
        return doc.title();
    }

     public Connection.Response getConnection(String url) throws IOException {
         Connection.Response response = Jsoup.connect(url)
                 .userAgent(userAgent)
                 .referrer(referrer)
                 .execute();
         return response;
     }

     public void startParse(Site site) throws IOException {
        ParseAction parseAction = new ParseAction(site.getUrl(), site, pageService, siteService, getConnection(site.getUrl()));
        ForkJoinPool pool = new ForkJoinPool();
        pools.add(pool);
        pool.submit(parseAction);
     }

     public void startParsePage(String url, Site site) throws InterruptedException, IOException {
         ParseAction parseAction = new ParseAction(url, site, pageService, siteService, getConnection(url));
         parseAction.parsePage(url);
     }

     public void stopParse(SitesList sites){
         for (SiteConfig siteConfig: sites.getSites()){
             Site site = siteService.getSiteByUrl(siteConfig.getUrl());
             if (site == null){
                 Site newSite = siteService.createSite(siteConfig);
                 siteService.changeStatusAndError(newSite, EnumStatus.FAILED, "Индексация остановлена пользователем");
             }
             if (site.getStatus()!=EnumStatus.INDEXED){
                 siteService.changeStatusAndError(site, EnumStatus.FAILED, "Индексация остановлена пользователем");
             }
         }
         for (ForkJoinPool pool: pools){
                 pool.shutdownNow();
         }
     }
}

