package searchengine.services;
import searchengine.dto.statistics.SearchingResponse;
import java.io.IOException;


public interface SearchService {
    SearchingResponse searchingOnRequest(String query, String site) throws IOException;
}
