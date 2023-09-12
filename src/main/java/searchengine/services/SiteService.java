package searchengine.services;

import searchengine.config.SiteConfig;
import searchengine.model.EnumStatus;
import searchengine.model.Site;
import searchengine.repositories.SiteRepository;

import java.util.List;

public interface SiteService {
    Site getSiteByUrl(String url);
    void deleteSite(Site site);
    Site createSite(SiteConfig siteConfig);

    void changeStatus(Site site, EnumStatus indexed);

    void changeStatusTime(Site site);
    void changeLastError(String error, Site site);

    boolean isIndexing();

    void changeStatusAndError(Site site, EnumStatus status, String message);

    Site getSiteByName(String siteUrl);

    List<Site> getSiteList();
}
