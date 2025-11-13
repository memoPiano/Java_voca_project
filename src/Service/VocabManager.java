package Service;

import Vocab.Word;

import java.util.*;

public class VocabManager {
    String userName;
    ArrayList<Word> voc = new ArrayList<>();  //여기다 주로 저장
    HashMap<String, Word> vocabMap = new HashMap<>();

    private static final Scanner scan = new Scanner(System.in); //사용자로부터 입력받을 스캐너

    public VocabManager(String userName) {
        this.userName = userName;
    }   //생성자

    public void setAll(Collection<Word> list) {
        voc.clear(); //깔끔하게 비움
        if (list != null)
            voc.addAll(list); //전부 넣기
    }

    public ArrayList<Word> getVoc() {
        return voc;
    }  //이걸 메인에서 저장할거임

    //메뉴
    public void menu() {
        int choice = 0;
        while (choice != 9) {
            System.out.println("\n------ " + userName + "의 단어장 -------");
            System.out.println("1) 단어 추가");
            System.out.println("2) 단어 수정 (영어/뜻)");
            System.out.println("3) 단어 삭제");
            System.out.println("4) 단어 검색 (영→한 / 한→영)");
            System.out.println("9) 종료");
            System.out.print("메뉴 선택: ");

            choice = scan.nextInt();
            scan.nextLine();
            System.out.println();

            switch (choice) {
                case 1 -> addVocab();
                case 2 -> editVocab();
                case 3 -> deleteVocab();
                case 4 -> searchVocab();
                case 5 -> quiz_essay();
                case 6 -> quiz_multiChoice();
                case 9 -> System.out.println("종료합니다");
                default -> System.out.println("메뉴를 다시 선택하세요");
            }
        }
    }

    private void quiz_multiChoice() {
    }

    private void quiz_essay() {

    }

    private void searchVocab() {
        System.out.println("\n[검색] 방향을 선택하세요: 1) 영->한 2) 한->영");
        System.out.print(">> ");
        int dir=scan.nextInt();
        scan.nextLine();

        List<Word> result=new ArrayList<>(); //검색 결과를 저장할 리스트

        if(dir==1){
            System.out.print("검색할 영단어 입력: ");
            String q1=scan.nextLine().trim();
            result=findEngSubString(q1);
            //빈 입력 하면 전체가 출력됨. 방지
            if (q1.isEmpty()) {
                System.out.println("빈 입력입니다. 검색을 취소합니다.");
                return;
            }
        } else if (dir==2) {
            System.out.print("검색할 한글 입력: ");
            String q2=scan.nextLine().trim();
            result=findKorSubString(q2);
            //마찬가지로 방지
            if (q2.isEmpty()) {
                System.out.println("빈 입력입니다. 검색을 취소합니다.");
                return;
            }
        }else{
            System.out.println("잘못된 입력입니다. 1 혹은 2 중에 선택해주세요.");
            return;
        }

        if(result.isEmpty()){
            System.out.println("검색 결과가 없습니다.");
        }else{
            System.out.println("검색결과) "+result.size()+"개");
            for (Word word : result) {
                System.out.println(word);
            }
        }
    }

    //한글로 검색해서 찾기
    private List<Word> findKorSubString(String q) {
        ArrayList<Word> res=new ArrayList<>();
        for (Word w : voc) {
            for (String k : w.getKors()) {
                if(k.contains(q)) {
                    res.add(w);
                    break;
                }
            }
        }
        return res;
    }

    //영어로 검색해서 찾기
    private List<Word> findEngSubString(String q) {
        ArrayList<Word> res=new ArrayList<>();
        for (Word w : voc) {
            String s=w.getEng();
            if(s.contains(q))
                res.add(w);
        }
        return res;
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
