package Main;

import Service.FileManager;
import Service.VocabManager;
import Vocab.Word;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MainMenu {
    public static void main(String[] args) {
        String path="res/words.txt";  //파일 경로
        FileManager fm=new FileManager(path);
        ArrayList<Word> vocabList=fm.loadFromFile();

        VocabManager vm=new VocabManager("홍길동");
        vm.setAll(vocabList);  //파일로부터 만든 리스트를 매니저에 넘김. 이 안에서 작동
        vm.menu();  //메뉴 작업
        fm.saveToFile(vm.getVoc());  //메뉴 끝나면 저장

        // 2) 요약 출력
        System.out.println("=== 로드 결과 ===");
        System.out.println("총 단어 수: " + vocabList.size());

    } //main 의 끝

} //클래스의 끝
