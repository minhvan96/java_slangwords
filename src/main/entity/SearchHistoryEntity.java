package main.entity;

public class SearchHistoryEntity {
    private Integer searchType;
    private String searchKeyWord;
    public SearchHistoryEntity(Integer searchType, String searchKeyWord){
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
