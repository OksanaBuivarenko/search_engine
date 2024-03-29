package searchengine.dto.statistics;

@lombok.Data
public class Data {
    private String site;
    private String siteName;
    private String uri;
    private String title;
    private String snippet;
    private Double relevance;
}
