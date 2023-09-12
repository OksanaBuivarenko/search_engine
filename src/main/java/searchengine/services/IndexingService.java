package searchengine.services;

import searchengine.dto.statistics.IndexingResponse;

import java.io.IOException;

public interface IndexingService {
    IndexingResponse startIndexing() throws IOException;
    IndexingResponse stopIndexing();
    IndexingResponse indexingPage(String url) throws IOException, InterruptedException;
}
