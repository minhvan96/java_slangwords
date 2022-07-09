package main;

import main.entity.SearchHistoryEntity;
import main.entity.SlangWordPuzzleEntity;
import main.entity.SlangWordEntity;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SwDictionary {
    private ArrayList<SlangWordEntity> dic;
    private final String slangFilePath = this.getClass().getResource("resources/slang.txt").getPath();
    private final InputStream slangFileInputStream = this.getClass().getResourceAsStream("resources/slang.txt");
    private final String originalSlangFilePath = this.getClass().getResource("resources/original_slang").getPath();
    private final String searchHistoryFilePath = this.getClass().getResource("resources/search_history").getPath();

    public SwDictionary() {
        dic = (ArrayList<SlangWordEntity>) readDictionary();
    }

    // region dictionary interaction

    public Collection<SlangWordEntity> readDictionary() {
        ArrayList<SlangWordEntity> slangDic = new ArrayList();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("resources/slang.txt")))) {
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
            dic = (ArrayList<SlangWordEntity>) readDictionary();
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
            dic = (ArrayList<SlangWordEntity>) readDictionary();
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
            dic = (ArrayList<SlangWordEntity>) readDictionary();
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
            dic = (ArrayList<SlangWordEntity>) readDictionary();
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
        ArrayList<SlangWordEntity> dictionary = (ArrayList<SlangWordEntity>) readDictionary();

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
        ArrayList<SlangWordEntity> dictionary = (ArrayList<SlangWordEntity>) readDictionary();

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
            Writer output = new BufferedWriter(new FileWriter(searchHistoryFilePath, true));
            output.append(searchType + "`" + searchKeyWord + '\n');
            output.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
//    private void writeSearchHistory(Integer searchType, String searchKeyWord) {
//        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get(getClass().getResource("resources/search_history").toURI()))) {
//            bufferedWriter.append(searchType + "`" + searchKeyWord + '\n');
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        } catch (URISyntaxException e) {
//            throw new RuntimeException(e);
//        }
//    }

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
