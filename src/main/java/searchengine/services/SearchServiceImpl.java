package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.dto.statistics.Data;
import searchengine.dto.statistics.SearchingResponse;
import searchengine.model.IndexEntity;
import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.model.Site;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService{
    private final LemmaService lemmaService;
    private final PageService pageService;
    private final SiteService siteService;
    private final IndexEntityService indexEntityService;
    private final ParseServiceImpl parseService;
    private final SnippetService snippetService;

    @Override
    public SearchingResponse searchingOnRequest(String query, String site) throws IOException {
        SearchingResponse response = new SearchingResponse();
        try {
            System.out.println("searchingOnRequest");
            HashMap<String, Integer> lemmasCountMap = lemmaService.getLemmasCountMap(query);
            List<Lemma> lemmaList = getSortingList(exclusionFromTheListOfLemmasByFrequency(lemmasCountMap.keySet(), site));
            int index = 0;
            List <Page> pages = lemmaService.getLemmaPageList(lemmaList.get(index));
            List<Page> coincidenceList;
                if(lemmaList.size()>1){
                    coincidenceList = getListWithCoincidenceLemma(pages, index, lemmaList);
                }
                else {
                    coincidenceList = pages;
                }
            response.setResult(true);
            response.setData(getDataList(coincidenceList, lemmaList));
            response.setCount(response.getData().size());
            return response;
        }
        catch (Exception ex){
            System.out.println(ex.getMessage());
            response.setResult(false);
            response.setError("По данному запросу ничего не найдено");
            return response;
        }
    }

    public List<Data> getDataList(List<Page> coincidenceList, List<Lemma> lemmaList) throws IOException {
        HashMap<Page, Double> relativeRelevancePagesMap = getRelativeRelevancePagesMap(coincidenceList, lemmaList);
        List<Data> dataList = new ArrayList<>();
        Comparator<Data> comparator = Comparator.comparing(data -> data.getRelevance());
        for (Page page: coincidenceList){
            Data data = new Data();
            Site site = page.getSite();
            data.setSite(site.getUrl());
            data.setSiteName(site.getName());
            data.setUri(page.getPath());
            data.setRelevance(relativeRelevancePagesMap.get(page));
            data.setTitle(parseService.getTitle(site.getUrl() + page.getPath()));
            data.setSnippet(snippetService.getSnippet(page, lemmaList));
            dataList.add(data);
        }
        dataList.sort(comparator);
        return dataList;
    }



    public HashMap<Page, Double> getRelativeRelevancePagesMap(List<Page> pages, List<Lemma> lemmaList){
        HashMap<Page, Double> absoluteRelevancePagesMap = getAbsoluteRelevancePagesMap(pages, lemmaList);
        Double maxAbsoluteRelevance = Collections.max(absoluteRelevancePagesMap.values());
        HashMap<Page, Double> relativeRelevancePagesMap = new HashMap<>();
        for (Page page: pages) {
          Double relativeRelevance = absoluteRelevancePagesMap.get(page)/maxAbsoluteRelevance;
          relativeRelevancePagesMap.put(page, relativeRelevance);
        }
        return relativeRelevancePagesMap;
    }



    public HashMap<Page, Double> getAbsoluteRelevancePagesMap(List<Page> pages, List<Lemma> lemmaList){
        HashMap<Page, Double> absoluteRelevancePagesMap = new HashMap<>();
        for (Page page: pages){
            Double absoluteRelevance = 0.0;
            for (Lemma lemma: lemmaList){
                IndexEntity indexEntity = indexEntityService.getIndexEntityByPageAndLemma(page, lemma);
                if (indexEntity !=null){
                absoluteRelevance+= indexEntity.getIndexRank();
                }
            }
            absoluteRelevancePagesMap.put(page, absoluteRelevance);
        }
        return absoluteRelevancePagesMap;
    }


    public List<Lemma> exclusionFromTheListOfLemmasByFrequency(Set<String> lemmaSet, String site){
        List<Lemma> lemmaListAfterExclusion = new ArrayList<>();
        int maxFrequencyCount = (int) ((pageService.getAllPageCount()*80)/100);
        for (String stringLemma: lemmaSet){
            for (Lemma lemma: lemmaService.getLemmaListByStringLemma(stringLemma)) {
                if (isNotCorrectLemma(lemma, site, maxFrequencyCount, lemmaListAfterExclusion)){
                    continue;
                }
                lemmaListAfterExclusion.add(lemma);
            }
        }
        if (lemmaListAfterExclusion.isEmpty()) {
            System.out.println("Введенный поисковый запрос встречается слишком часто");
        }
        return lemmaListAfterExclusion;
    }

    public boolean isNotCorrectLemma(Lemma lemma, String site, int maxFrequencyCount, List<Lemma> lemmaList){
        return lemma == null|| (site != null && !lemma.getSite().getUrl().equals(site)) ||
                lemma.getFrequency() > maxFrequencyCount || isListContainsLemma(lemmaList, lemma);
    }

    public boolean isListContainsLemma(List<Lemma> lemmaList, Lemma lemma){
        for (Lemma lemmaInList: lemmaList){
            if (lemmaInList.getLemma().equals(lemma.getLemma())){
                System.out.println(true);
                return true;
            }
        }
        return false;
    }

    public List<Lemma> getSortingList(List<Lemma> lemmaList){
        Comparator<Lemma> comparator = Comparator.comparing(lemma -> lemma.getFrequency());
         lemmaList.sort(comparator);
         return lemmaList;
    }

    public List<Page> getListWithCoincidenceLemma(List<Page> pages, int index, List<Lemma> lemmaList){
    List<Page> coincidenceList = new ArrayList<>();
        while (index < lemmaList.size()-1){
            if (!pages.isEmpty()){
                index +=1;
                for (Page page: pages){
                    for (IndexEntity indexEntity: page.getIndexes()){
                        if (indexEntity.getLemma().equals(lemmaList.get(index))){
                            if (!coincidenceList.contains(page)){
                            coincidenceList.add(page);
                            }
                        }
                    }
                }
                getListWithCoincidenceLemma(coincidenceList, index, lemmaList);
            }
        }
        return coincidenceList;
    }
}
