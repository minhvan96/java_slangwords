package main.entity;

public class SlangWordEntity {
    private String word;
    private String meaning;

    public String getWord() {
        return word;
    }

    public String getMeaning() {
        return meaning;
    }

    public SlangWordEntity(String word, String meaning) {
        this.word = word;
        this.meaning = meaning;
    }
}
