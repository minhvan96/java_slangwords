package main;

import main.entity.SearchHistoryEntity;
import main.entity.SlangWordEntity;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SwDictionary {
    private final ArrayList<SlangWordEntity> dic;
    private final String slangFilePath = this.getClass().getResource("resources/slang.txt").getPath();
    private final String originalSlangFilePath = this.getClass().getResource("resources/original_slang").getPath();
    private final String searchHistoryFilePath = this.getClass().getResource("resources/search_history").getPath();
    public SwDictionary() {
        dic = (ArrayList<SlangWordEntity>) readDictionary();
    }

    // region dictionary interaction
    public Collection<SlangWordEntity> readDictionary() {
        File slangDicFile = new File(slangFilePath);
        ArrayList<SlangWordEntity> slangDic = new ArrayList<SlangWordEntity>();
        try {
            Scanner studentReader = new Scanner(slangDicFile);
            while (studentReader.hasNextLine()) {
                String line = studentReader.nextLine();
                String[] splitedLine = line.split("`");
                if (splitedLine.length != 2)
                    continue;
                String word = splitedLine[0];
                String meaning = splitedLine[1];
                SlangWordEntity slangWordEntity = new SlangWordEntity(word, meaning);
                slangDic.add(slangWordEntity);
            }
            return slangDic;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public void insertRecordToDictionary(String word, String meaning){
        try {
            Writer output = new BufferedWriter(new FileWriter(slangFilePath, true));
            output.append(word + "`" + meaning+ '\n');
            output.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void editRecord(String word, String newDefinition){
        File file = new File(slangFilePath);

        List<String> fileContent = null;
        try {
            fileContent = new ArrayList<>(Files.readAllLines(file.toPath(), StandardCharsets.UTF_8));
            for (int i = 0; i < fileContent.size(); i++) {
                String[] splitedLine = fileContent.get(i).split("`");
                if (splitedLine.length != 2)
                    continue;

                if (splitedLine[0].equalsIgnoreCase(word)) {
                    fileContent.set(i, word + "`" + newDefinition);
                }
            }

            Files.write(file.toPath(), fileContent, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Stream<SlangWordEntity> searchByWord(String searchKeyWord) {
        Predicate<SlangWordEntity> streamsPredicate = word -> searchKeyWord.equalsIgnoreCase(word.getWord());

        writeSearchHistory(1, searchKeyWord);
        return dic.stream()
                .filter(streamsPredicate);
    }

    public Stream<SlangWordEntity> searchByMeaning(String definition) {
        writeSearchHistory(2, definition);
        return dic.stream()
                .filter(x -> x.getMeaning().contains(definition));
    }


    // endregion


    // region search history


    private void writeSearchHistory(Integer searchType, String searchKeyWord) {
        try {
            Writer output = new BufferedWriter(new FileWriter(searchHistoryFilePath, true));
            output.append(searchType + "`" + searchKeyWord+ '\n');
            output.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Collection<SearchHistoryEntity> getSearchHistory() {
        File searchHistoryFile = new File(searchHistoryFilePath);
        ArrayList<SearchHistoryEntity> records = new ArrayList<SearchHistoryEntity>();
        try {
            Scanner searchHistoryReader = new Scanner(searchHistoryFile);
            while (searchHistoryReader.hasNextLine()) {
                String line = searchHistoryReader.nextLine();
                String[] splitedLine = line.split("`");
                if (splitedLine.length != 2)
                    continue;
                Integer searchType = Integer.valueOf(splitedLine[0]);
                String searchKeyWord = splitedLine[1];
                SearchHistoryEntity record = new SearchHistoryEntity(searchType, searchKeyWord);
                records.add(record);
            }
            return records;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    // endregion

    // region helper
//    public void removeLine(int id) throws IOException {
//        File file = new File(this.studentPath);
//        System.out.println("Read file from path: " + this.studentPath);
//        System.out.println("File is exist: " + file.exists());
//        List<String> out = Files.lines(file.toPath())
//                .filter(line -> !line.split("#", 2)[0].equals(String.valueOf(id)) && !line.isEmpty())
//                .collect(Collectors.toList());
//        Files.write(file.toPath(), out, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
//        studentIdTextField.setText("");
//        createStudentCollectionTable();
//        JOptionPane.showMessageDialog(new JButton(), "Remove student successfully");
//    }

    //endregion
}
