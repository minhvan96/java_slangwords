package main;

import main.entity.SearchHistoryEntity;
import main.entity.SlangWordPuzzleEntity;
import main.entity.SlangWordEntity;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SwDictionary {
    private final ArrayList<SlangWordEntity> dic;
    private final String slangFilePath = getClass().getResource("slang.txt").getPath();
    private final InputStream slangFileInputStream = getClass().getResourceAsStream("slang.txt");
    private final String originalSlangFilePath = getClass().getResource("original_slang").getPath();
    private final InputStream originalSlangFileInputStream = getClass().getResourceAsStream("original_slang");
    private final String searchHistoryFilePath = getClass().getResource("search_history").getPath();
    private final InputStream searchHistoryFileInputStream = getClass().getResourceAsStream("search_history");

    public SwDictionary() {
        init();
        System.out.println(slangFilePath);
        System.out.println(originalSlangFilePath);
        System.out.println(searchHistoryFilePath);
        dic = (ArrayList<SlangWordEntity>) readDictionary(slangFileInputStream);
    }
    public void init(){
        try {
            File langTxt = new File("slang.txt");
            langTxt.createNewFile(); // if file already exists will do nothing
            FileOutputStream oSlangFile = new FileOutputStream(langTxt, false);

            File searchHistoryFile = new File("search_history");
            searchHistoryFile.createNewFile(); // if file already exists will do nothing
            FileOutputStream oSearchHistory = new FileOutputStream(searchHistoryFile, false);

            File originalSlangFile = new File("original_slang");
            originalSlangFile.createNewFile(); // if file already exists will do nothing
            FileOutputStream oOriginalSlang = new FileOutputStream(originalSlangFile, false);

            transferFile(slangFileInputStream, oSlangFile.getChannel());
            transferFile(originalSlangFileInputStream, oOriginalSlang.getChannel());
            transferFile(searchHistoryFileInputStream, oSearchHistory.getChannel());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void transferFile(InputStream srcInputStream, FileChannel dest){

        Path temp = null;
        try {
            temp = Files.createTempFile("resource-", ".ext");
            Files.copy(srcInputStream, temp, StandardCopyOption.REPLACE_EXISTING);
            FileInputStream input = new FileInputStream(temp.toFile());
            FileChannel src = input.getChannel();
            dest.transferFrom(src, 0, src.size());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    // region dictionary interaction
    public Collection<SlangWordEntity> readDictionary() {
        File slangDicFile = new File(slangFilePath);
        ArrayList<SlangWordEntity> slangDic = new ArrayList();
        try {
            System.out.println("Try to read dictionary");
            Scanner studentReader = new Scanner(slangDicFile);
            while (studentReader.hasNextLine()) {
                String line = studentReader.nextLine();
                String[] splitedLine = line.split("`");
                if (splitedLine.length != 2) continue;
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
    private Collection<SlangWordEntity> readDictionary(InputStream inputStream) {
        ArrayList<SlangWordEntity> slangDic = new ArrayList();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitedLine = line.split("`");
                if (splitedLine.length != 2) continue;
                String word = splitedLine[0];
                String meaning = splitedLine[1];
                SlangWordEntity slangWordEntity = new SlangWordEntity(word, meaning);
                slangDic.add(slangWordEntity);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return slangDic;
    }

    public Optional<SlangWordEntity> getRandomSlangWord(){
        return getRandom(dic);
    }
    public void insertRecordToDictionary(String word, String meaning) {
        try {
            Writer output = new BufferedWriter(new FileWriter(slangFilePath, true));
            output.append(word + "`" + meaning + '\n');
            output.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void editRecord(String word, String newDefinition) {
        File file = new File(slangFilePath);

        List<String> fileContent = null;
        try {
            fileContent = new ArrayList<>(Files.readAllLines(file.toPath(), StandardCharsets.UTF_8));
            for (int i = 0; i < fileContent.size(); i++) {
                String[] splitedLine = fileContent.get(i).split("`");
                if (splitedLine.length != 2) continue;

                if (splitedLine[0].equalsIgnoreCase(word)) {
                    fileContent.set(i, word + "`" + newDefinition);
                }
            }

            Files.write(file.toPath(), fileContent, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeRecord(String word) {
        File file = new File(slangFilePath);
        List<String> out = null;
        try {
            out = Files.lines(file.toPath()).filter(line -> !line.split("`", 2)[0].equalsIgnoreCase(word) && !line.isEmpty()).collect(Collectors.toList());
            Files.write(file.toPath(), out, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void reset(){
        File original = new File(originalSlangFilePath);
        File slang = new File(slangFilePath);
        try {
            FileChannel src = new FileInputStream(original).getChannel();
            FileChannel dest = new FileOutputStream(slang).getChannel();
            dest.transferFrom(src, 0, src.size());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public Stream<SlangWordEntity> searchByWord(String searchKeyWord) {
        Predicate<SlangWordEntity> streamsPredicate = word -> searchKeyWord.equalsIgnoreCase(word.getWord());

        writeSearchHistory(1, searchKeyWord);
        return dic.stream().filter(streamsPredicate);
    }

    public Stream<SlangWordEntity> searchByMeaning(String definition) {
        writeSearchHistory(2, definition);
        return dic.stream().filter(x -> x.getMeaning().contains(definition));
    }

    public SlangWordPuzzleEntity getGuessDefinitionPuzzle(){
        SlangWordPuzzleEntity puzzle = new SlangWordPuzzleEntity();
        ArrayList<SlangWordEntity> dictionary = (ArrayList<SlangWordEntity>) readDictionary(slangFileInputStream);

        Optional<SlangWordEntity> answer = getRandom(dictionary);
        puzzle.setAnswer(answer.get());
        for(int i = 0; i < 3; i++){
            Optional<SlangWordEntity> wrongAnswer = getRandom(dictionary);
            puzzle.addWrongAnswer(wrongAnswer.get().getMeaning());
        }
        return puzzle;
    }
    public SlangWordPuzzleEntity getGuessWordGivenDefinitionPuzzle(){
        SlangWordPuzzleEntity puzzle = new SlangWordPuzzleEntity();
        ArrayList<SlangWordEntity> dictionary = (ArrayList<SlangWordEntity>) readDictionary(slangFileInputStream);

        Optional<SlangWordEntity> answer = getRandom(dictionary);
        puzzle.setAnswer(answer.get());
        for(int i = 0; i < 3; i++){
            Optional<SlangWordEntity> wrongAnswer = getRandom(dictionary);
            puzzle.addWrongAnswer((wrongAnswer.get().getWord()));
        }

        return puzzle;
    }
    // endregion

    // region search history


    private void writeSearchHistory(Integer searchType, String searchKeyWord) {
        try {
            System.out.println("History file path: " + searchHistoryFilePath);
            Writer output = new BufferedWriter(new FileWriter(searchHistoryFilePath, true));
            output.append(searchType + "`" + searchKeyWord + '\n');
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
                if (splitedLine.length != 2) continue;
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

    public static <E> Optional<E> getRandom (Collection<E> e) {

        return e.stream()
                .skip((int) (e.size() * Math.random()))
                .findFirst();
    }
}
