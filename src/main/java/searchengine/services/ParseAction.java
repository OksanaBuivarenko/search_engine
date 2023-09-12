package searchengine.services;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import searchengine.model.EnumStatus;
import searchengine.model.Page;
import searchengine.model.Site;

import java.io.IOException;
import java.util.concurrent.RecursiveAction;



public class ParseAction extends RecursiveAction {
    String websiteURL;
    String strURL;
    private Site site;
    private final PageService pageService;
    private final SiteService siteService;
    private final Connection.Response response;


    public ParseAction(String websiteURL, Site site, PageService pageService, SiteService siteService, Connection.Response response) {
        this.websiteURL = websiteURL;
        this.site = site;
        this.pageService = pageService;
        this.siteService = siteService;
        this.response = response;
    }

    @Override
    protected void compute() {
        try {
            int statusCode = response.statusCode();
            Document doc = Jsoup.connect(websiteURL).get();
            Elements elements = doc.select("a");

            for (Element child : elements) {
                strURL = child.absUrl("href");
                String path = strURL.replaceAll(site.getUrl().toString(), "");
                if (isCorrectUrl(path)){
                    continue;
                }
                Thread.sleep(1000);
                Document content = Jsoup.connect(strURL).get();
                Page page = pageService.createPage(statusCode, content.toString(), path, site);
                pageService.createPageLemmasAndIndexes(page, site);
                ParseAction task = new ParseAction(strURL, site, pageService, siteService, response);
            }
            siteService.changeStatus(site, EnumStatus.INDEXED);
            System.out.println("Parse completed!!!!!!!!!!!!!!!!!!!!!!!!!! + site " + site.getName());
        }catch (InterruptedException ex) {
            site.setLastError("Индексация остановлена пользователем");
            siteService.changeStatus(site, EnumStatus.FAILED);
            System.out.println("Индексация остановлена пользователем");
        }
        catch (Exception e) {
            site.setLastError(e.getMessage());
            siteService.changeStatus(site, EnumStatus.FAILED);

        }
    }

    public boolean isCorrectUrl(String path) {
        return websiteURL.equals(strURL) || strURL.contains(".pdf") || strURL.contains("#") ||
               pageService.getPageByPath(path) != null || !strURL.contains(site.getUrl());
    }

    public String parsePage( String url) throws IOException, InterruptedException {
        int statusCode = response.statusCode();
        Document doc = Jsoup.connect(url).get();
        String path = url.replaceAll(site.getUrl().toString(), "");
        if (!url.contains(".pdf") || !url.contains("#") || (pageService.getPageByPath(path) == null)
                || strURL.contains(site.getUrl())) {
            Thread.sleep(1000);
            Page page = pageService.createPage(statusCode, doc.toString(), path, site);
            pageService.createPageLemmasAndIndexes(page, site);
        }
        System.out.println("Page update compleated!");
        return strURL;
    }
}


