package main.Entity;

public class SearchHistory {
    private Integer searchType;
    private String searchKeyWord;
    public SearchHistory(Integer searchType, String searchKeyWord){
        this.searchType = searchType;
        this.searchKeyWord = searchKeyWord;
    }

    public Integer getSearchType() {
        return searchType;
    }

    public String getSearchKeyWord() {
        return searchKeyWord;
    }
}
