package main;

import main.Entity.SlangWord;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class SwDictionary {
    private ArrayList<SlangWord> dic;
    public SwDictionary() {
        dic = (ArrayList<SlangWord>) read();
    }

    public Collection<SlangWord> read() {
        String slangWordsDic = SlangWord.class.getResource("slang.txt").getPath();
        File slangDicFile = new File(slangWordsDic);
        ArrayList<SlangWord> slangDic = new ArrayList<SlangWord>();
        try {
            Scanner studentReader = new Scanner(slangDicFile);
            while (studentReader.hasNextLine()) {
                String line = studentReader.nextLine();
                String[] splitedLine = line.split("`");
                String word = splitedLine[0];
                String meaning = splitedLine[1];
                SlangWord slangWord = new SlangWord(word, meaning);
                slangDic.add(slangWord);
            }
            return slangDic;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
