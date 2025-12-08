package Main;

import JFrame.VocabFrame;
import Service.FileManager;
import Service.VocabManager;
import Vocab.Word;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;

public class GuiMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            String path = "res/words.txt";
            String exPath = "res/sentences.txt";

            //파일 로드
            FileManager fm = new FileManager(path);
            ArrayList<Word> vocabList = fm.loadFromFile();
            HashMap<String, String> exampleMap =
                    FileManager.loadExamples(exPath);

            //매니저 생성
            VocabManager vm = new VocabManager("홍길동");
            vm.setAll(vocabList);  //위에서 loadFromFile 한 것
            vm.setExampleMap(exampleMap);

            // 3. 프레임 생성 ( 여기서 화면 뜸)
            new VocabFrame(vm, path, exPath);
        });
    }
}

