package main;

import main.Entity.SlangWord;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class SwDictionary {
    public SwDictionary() {

    }

    public void read() {
        String slangWordsDic = SlangWord.class.getResource("slang.txt").getPath();
        File slangDicFile = new File(slangWordsDic);
        try {
            Scanner studentReader = new Scanner(slangDicFile);

            while (studentReader.hasNextLine()) {
                String line = studentReader.nextLine();
                System.out.println(line);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
