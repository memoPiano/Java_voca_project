package Main;

import JFrame.VocabFrame; // GUI 프레임 임포트
import Service.FileManager;
import Service.VocabManager;
import Vocab.Word;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// 메인 메서드. 이 클래스가 실행 클래스
public class MainMenu {
    public static void main(String[] args) {
        String path="res/words.txt";  //파일 경로
        String exPath   = "res/sentences.txt"; //예문 파일 경로

        // 1. 단어 로드
        FileManager fm=new FileManager(path);
        ArrayList<Word> vocabList=fm.loadFromFile();

        // 2. 예문 로드
        HashMap<String, String> exampleMap = FileManager.loadExamples(exPath);


        // 3. 매니저에 전달
        VocabManager vm=new VocabManager("홍길동");
        vm.setAll(vocabList);  //파일로부터 만든 리스트를 매니저에 넘김. 이 안에서 작동
        vm.setExampleMap(exampleMap);

        // 4. 메뉴 구동
        vm.menu();
        //new VocabFrame(vm);

        // 5. 메뉴 끝나면 저장
        fm.saveToFile(vm.getVoc());
        FileManager.saveExamples(exPath,vm.getExampleMap() );

        // 요약 출력 (디버깅용)
//        System.out.println("=== 로드 결과 ===");
//        System.out.println("총 단어 수: " + vm.getVoc().size());

    } //main 의 끝

} //클래스의 끝
