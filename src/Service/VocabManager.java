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
                case 9 -> System.out.println("종료합니다");
                default -> System.out.println("메뉴를 다시 선택하세요");
            }
        }
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

    //삭제 기능
    private void deleteVocab() {
            System.out.print("[삭제] 영어 단어 입력: ");
            String eng = scan.nextLine().trim().toLowerCase();

            Word w = vocabMap.get(eng);
            if(w == null) {
                System.out.println("해당 단어가 존재하지 않습니다.");
                return;
            }

            System.out.print("정말 삭제하시겠습니까? (Y/N) : ");
            String confirm = scan.nextLine().trim().toUpperCase();
            while(!confirm.equals("Y") && !confirm.equals("N")) {
                System.out.print("Y/N 중에서 입력해주세요: ");
                confirm = scan.nextLine().trim().toUpperCase();
            }

            if(confirm.equals("Y")) {
                voc.remove(w);
                vocabMap.remove(eng);
                System.out.println(eng + "단어 삭제 완료!");
            } else {
                System.out.println("삭제 취소");
            }
        }

    //수정 기능
    private void editVocab() {
        System.out.print("[수정] 영어 단어 입력: ");
        String eng = scan.nextLine().trim().toLowerCase();

        Word w = vocabMap.get(eng);
        if (w == null) {
            System.out.println("해당 단어가 존재하지 않습니다.");
            return;
        }

        System.out.println("현재 단어: " + w.getEng());
        System.out.println("현재 뜻: " + w.getKors());

        System.out.println("\n[수정 메뉴]");
        System.out.println("1) 영어 단어 수정");
        System.out.println("2) 한글 뜻 추가");
        System.out.println("3) 한글 뜻 삭제");
        System.out.println("4) 한글 뜻 전체 수정");
        System.out.print("선택: ");

        int choice = scan.nextInt();
        scan.nextLine(); // 개행 제거

        switch (choice) {

            //영어 단어 자체를 수정
            case 1 -> {
                System.out.print("새 영어 단어 입력: ");
                String newEng = scan.nextLine().trim().toLowerCase();
                if (newEng.isEmpty()) {
                    System.out.println("입력이 비어 있습니다.");
                    return;
                }

                // map 업데이트
                vocabMap.remove(eng);
                w.setEng(newEng);
                vocabMap.put(newEng, w);
                System.out.println("영어 단어 수정 완료!");
            }

            //한글 뜻 추가
            case 2 -> {
                System.out.print("추가할 뜻 입력 (/로 여러 개 가능): ");
                String line = scan.nextLine().trim();
                String[] addList = line.split("/");

                for (String k : addList) {
                    k = k.trim();
                    if (!w.getKors().contains(k))
                        w.getKors().add(k);
                }
                System.out.println("뜻 추가 완료!");
            }

            //한글 뜻 삭제
            case 3 -> {
                System.out.print("삭제할 뜻 입력: ");
                String delKor = scan.nextLine().trim();

                if (w.getKors().remove(delKor)) {
                    System.out.println("뜻 삭제 완료!");
                } else {
                    System.out.println("해당 뜻이 없습니다.");
                }
            }

            //뜻 전체 새로 작성
            case 4 -> {
                System.out.print("새로운 뜻 입력 (/로 여러 개 가능): ");
                String line = scan.nextLine().trim();
                String[] newList = line.split("/");

                ArrayList<String> newKors = new ArrayList<>();
                for (String k : newList) {
                    k = k.trim();
                    if (!k.isEmpty())
                        newKors.add(k);
                }
                w.setKors(newKors);
                System.out.println("뜻 전체 수정 완료!");
            }

            default -> System.out.println("잘못된 선택입니다.");
        }
    }






    //추가 기능
    private void addVocab() {
            System.out.print("[추가] 추가할 영단어를 입력하세요: ");
            String eng = scan.nextLine().trim().toLowerCase();

        // 영어 단어 유효성 검사
            if (eng.isEmpty() || !eng.matches("[a-zA-Z]+")) {
                System.out.println("유효하지 않은 영어 단어입니다.");
                return;
            }

            System.out.print("한글 뜻을 입력하세요:  ");
            String korLine = scan.nextLine().trim();
            if (korLine.isEmpty()) {
                System.out.println("뜻이 비어있습니다.");
                return;
            }

            String[] kors = korLine.split("/");

            // 이미 존재하는 영단어인지 검사
            Word existing = vocabMap.get(eng);

            if (existing != null) {
                // 기존 단어에 뜻 추가 (중복 제외)
                for (String k : kors) {
                    if (!existing.getKors().contains(k))
                        existing.getKors().add(k);
                }


                System.out.println("기존 단어 '" + eng + "' 에 뜻이 추가되었습니다.");
            } else {
                Word newWord = new Word(eng);
                for (String k : kors) {
                    if (!newWord.getKors().contains(k))
                        newWord.getKors().add(k);
                }
                voc.add(newWord);
                vocabMap.put(eng, newWord);
                System.out.println(eng + "추가 완료!");
            }
        }
}
