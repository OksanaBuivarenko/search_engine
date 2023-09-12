package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import searchengine.model.EnumStatus;
import searchengine.model.Page;
import searchengine.model.Site;

import java.util.List;

public interface SiteRepository extends JpaRepository<Site, Integer> {
    Site findSiteByName(String name);
    Site findSiteByUrl(String url);
    List<Site> findSiteByStatus(EnumStatus enumStatus);
}
