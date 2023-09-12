package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.config.SiteConfig;
import searchengine.config.SitesList;
import searchengine.dto.statistics.IndexingResponse;
import searchengine.model.*;

import java.io.IOException;
import java.util.concurrent.ForkJoinPool;


@Service
@RequiredArgsConstructor
public class IndexingServiceImpl implements IndexingService{

    private final SitesList sites;
    private final SiteService siteService;
    private final PageService pageService;
    private final ParseService parseService;
    private ForkJoinPool pool =  new ForkJoinPool();


    @Override
    public IndexingResponse startIndexing() throws IOException {
        IndexingResponse response = new IndexingResponse();
        try {
            for (SiteConfig siteConfig: sites.getSites()){
                System.out.println(siteConfig.getName() );
                Site site = siteService.getSiteByUrl(siteConfig.getUrl());
                if (site!= null){
                    siteService.deleteSite(site);
                }
                Site newSite = siteService.createSite(siteConfig);
                parseService.startParse(newSite);

            }
            response.setResult(true);
            response.setError("");
        }
        catch (Exception e) {
            response.setResult(false);
            response.setError("Индексация уже запущена");
        }
        return response;
    }

    @Override
    public IndexingResponse stopIndexing() {
        IndexingResponse response = new IndexingResponse();
        if (!siteService.isIndexing()){
            response.setResult(false);
            response.setError("Индексация не запущена");
        }
        else {
            parseService.stopParse(sites);
            response.setResult(true);
            response.setError("");
        }
        return response;
    }

    @Override
    public IndexingResponse indexingPage(String url) throws IOException, InterruptedException {
        IndexingResponse response = new IndexingResponse();
        String siteUrl = pageService.getSiteUrlFromPageUrl(url);
        Site site = siteService.getSiteByUrl(siteUrl);
        if (site == null ){
            response.setResult(false);
            response.setError("Данная страница находится за пределами сайтов, указанных в конфигурационном файле");
        }
        else {
            Page page = pageService.getPageByPath(url.replaceAll(site.getUrl().toString(), ""));
            if (page !=null) {
                pageService.deletePage(page);
            }
            parseService.startParsePage(url, site);
            response.setResult(true);
            response.setError("");
        }
        return response;
    }


}
