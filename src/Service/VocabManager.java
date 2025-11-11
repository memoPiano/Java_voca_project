package Service;

import Vocab.Word;

import java.util.*;

public class VocabManager {
    String userName;
    ArrayList<Word> voc=new ArrayList<>();  //여기다 주로 저장
    HashMap<String,Word> vocabMap=new HashMap<>();

    private static final Scanner scan=new Scanner(System.in); //사용자로부터 입력받을 스캐너
    public VocabManager(String userName) {
        this.userName = userName;
    }   //생성자

    public void setAll(Collection<Word> list){
        voc.clear(); //깔끔하게 비움
        if(list!=null)
            voc.addAll(list); //전부 넣기
    }

    public ArrayList<Word> getVoc() {
        return voc;
    }  //이걸 메인에서 저장할거임

    //메뉴
    public void menu(){
        int choice=0;
        while (choice !=9){
            System.out.println("\n------ " + userName + "의 단어장 -------");
            System.out.println("1) 단어 추가");
            System.out.println("2) 단어 수정 (영어/뜻)");
            System.out.println("3) 단어 삭제");
            System.out.println("4) 단어 검색 (영→한 / 한→영)");
            System.out.println("9) 종료");
            System.out.print("메뉴 선택: ");

            choice=scan.nextInt();
            scan.nextLine();
            System.out.println();

            switch (choice){
                case 1-> addVocab();
                case 2-> editVocab();
                case 3-> deleteVocab();
                case 4-> searchVocab();
                case 9-> System.out.println("종료합니다");
                default -> System.out.println("메뉴를 다시 선택하세요");
            }
        }
    }

    private void searchVocab() {
        System.out.println("검색 기능");
    }

    private void deleteVocab() {
        System.out.println("삭제 기능");
    }

    private void editVocab() {
        System.out.println("수정 기능");
    }

    private void addVocab() {
        System.out.println("추가 기능");
    }
}
