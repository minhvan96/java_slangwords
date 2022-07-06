package main;


import main.Entity.SlangWord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class SlangWords {

    public SlangWords() {
    }

    private void ReadDictionary() {

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
        while (optionScanner.hasNextLine()){
            ArrayList<SlangWord> words;
            int option = Integer.parseInt(optionScanner.nextLine());
            switch (option){
                case 1:
                    System.out.print("Enter word: ");
                    Scanner searchKeyWordScanner = new Scanner(System.in);
                    String searchKeyWord = searchKeyWordScanner.nextLine();
                    words = (ArrayList<SlangWord>) dic.SearchByWord(searchKeyWord);
                    System.out.println(String.format("List Meanings"));
                    words.forEach(word -> System.out.println(word.getMeaning()));

                case 2:
                    System.out.print("Enter definition: ");
                    Scanner searchDefinitionScanner = new Scanner(System.in);
                    String searchDefinition = searchDefinitionScanner.nextLine();
                    words = (ArrayList<SlangWord>) dic.SearchByMeaning(searchDefinition);
                    System.out.println(String.format("List Word"));
                    words.forEach(word -> System.out.println(word.getMeaning()));

                default:
                    break;
            }

        }

        dic.read();
    }
}
