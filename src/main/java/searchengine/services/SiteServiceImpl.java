package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.config.SiteConfig;
import searchengine.model.EnumStatus;
import searchengine.model.Page;
import searchengine.model.Site;
import searchengine.repositories.SiteRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SiteServiceImpl implements SiteService{
    private final SiteRepository siteRepository;
    private final PageService pageService;

    @Override
    public Site getSiteByUrl(String url) {
      return siteRepository.findSiteByUrl(url);
    }

    @Override
    public Site getSiteByName(String siteUrl) {
        return siteRepository.findSiteByName(siteUrl);
    }

    @Override
    public List<Site> getSiteList() {
        return siteRepository.findAll();
    }

    @Override
    public void deleteSite(Site site) {
        for (Page page: pageService.getPageListBySite(site)){
            pageService.deletePage(page);
        }
        siteRepository.delete(site);
    }

    @Override
    public Site createSite(SiteConfig siteConfig) {
        Site site = new Site();
        site.setName(siteConfig.getName());
        site.setUrl(siteConfig.getUrl());
        site.setStatus(EnumStatus.INDEXING);
        site.setStatusTime(LocalDateTime.now());
        siteRepository.save(site);
        return site;
    }

    @Override
    public void changeStatus(Site site, EnumStatus status) {
        site.setStatus(status);
        changeStatusTime(site);
    }

    @Override
    public void changeStatusTime(Site site) {
        site.setStatusTime(LocalDateTime.now());
        siteRepository.save(site);
    }

    @Override
    public void changeLastError(String error, Site site) {
        site.setLastError(error);
        siteRepository.save(site);
    }

    @Override
    public boolean isIndexing() {
        List<Site> sites = siteRepository.findSiteByStatus(EnumStatus.INDEXING);
       return !sites.isEmpty();
    }

    @Override
    public void changeStatusAndError(Site site, EnumStatus status, String message) {
        site.setStatus(status);
        site.setLastError(message);
        changeStatusTime(site);
    }
}
