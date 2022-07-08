package main;


import main.entity.SearchHistoryEntity;
import main.entity.SlangWordEntity;

import java.util.*;
import java.util.stream.Collectors;

public class SlangWords {

    public SlangWords() {
    }

    public static void main(String[] args) {
        System.out.println("Hello");

        // region menu
        Map<Integer, String> menu = new HashMap<Integer, String>();
        menu.put(1, "Search by word");
        menu.put(2, "Search by meaning");
        menu.put(3, "Search history");
        menu.put(4, "Add slang");
        menu.put(5, "Edit slang");
        menu.put(6, "Delete slang");
        menu.put(7, "Reset");
        menu.put(8, "Random slang");
        menu.put(9, "Slang puzzle");
        menu.put(10, "Slang puzzle 2");
        // endregion

        SwDictionary dic = new SwDictionary();

        System.out.println("SLANG WORDS APP MENU");
        menu.forEach((k, v) -> System.out.println(k + ": " + v));
        System.out.println();


        System.out.print("Select option: ");
        Scanner optionScanner = new Scanner(System.in);
        while (optionScanner.hasNextLine()) {
            SlangWordEntity[] words;
            int option = Integer.parseInt(optionScanner.nextLine());
            switch (option) {
                case 1:
                    System.out.print("Enter word: ");
                    Scanner searchKeyWordScanner = new Scanner(System.in);
                    String searchKeyWord = searchKeyWordScanner.nextLine();
                    words = dic.searchByWord(searchKeyWord).toArray(SlangWordEntity[]::new);
                    System.out.println(String.format("List Meanings"));
                    for (SlangWordEntity slangWord : words) {
                        System.out.println(slangWord.getMeaning());
                    }
                    break;
                case 2:
                    System.out.print("Enter definition: ");
                    Scanner searchDefinitionScanner = new Scanner(System.in);
                    String searchDefinition = searchDefinitionScanner.nextLine();
                    words = dic.searchByMeaning(searchDefinition).toArray(SlangWordEntity[]::new);
                    System.out.println(String.format("List Word"));

                    for (SlangWordEntity slangWord : words) {
                        System.out.println(slangWord.getMeaning());
                    }
                    break;
                case 3:
                    System.out.println("Search history");
                    ArrayList<SearchHistoryEntity> records = (ArrayList<SearchHistoryEntity>) dic.getSearchHistory();
                    records.forEach(x -> System.out.println(x.getSearchType() == 1
                            ? "Search by word with keyword: " + x.getSearchKeyWord()
                            : "Search by definition: " + x.getSearchKeyWord()));
                    break;

                default:
                    break;
            }

        }

        dic.readDictionary();
    }
}
