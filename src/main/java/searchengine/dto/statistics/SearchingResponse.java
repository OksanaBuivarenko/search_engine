package searchengine.dto.statistics;

import java.util.List;
@lombok.Data
public class SearchingResponse {
    private boolean result;
    private int count;
    private List<Data> data;
    private String error;


}
