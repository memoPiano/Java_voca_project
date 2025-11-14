package Service;

import Vocab.Word;

import java.util.*;

public class VocabManager extends FileManager{
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
            System.out.println("5) 퀴즈");
            System.out.println("6) 오답노트");
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
                case 5 -> quiz();
                case 7 -> show_wrongWord();
                case 9 -> System.out.println("종료합니다");
                default -> System.out.println("메뉴를 다시 선택하세요");
            }
        }
    }

    private void show_wrongWord() {

    }

    private void quiz_multiChoice() {
        ArrayList<Word> quiz_array = voc;
        if(quiz_array.size()<4)
        {
            System.out.println("단어장에 최소 4개 이상의 단어가 들어가있어야 합니다.");
            return;
        }
        System.out.println("----------------------------");
        System.out.println("다음으로 보여지는 한글 뜻을 가지는 영단어를 골라주시면 됩니다. 기회는 총 3번입니다");
        System.out.println("3초 뒤 퀴즈가 시작됩니다");
        System.out.println();
        long beforeStart_time_limit = 3_000_000_000L;
        long beforeStart_start = System.nanoTime();
        while(true)
        {
            long beforeStart_end = System.nanoTime();
            if(beforeStart_end - beforeStart_start >=beforeStart_time_limit)
            {
                break;
            }
        }
        int wrong_time=0;
        Word quiz_word = quiz_array.get((int)(Math.random()*quiz_array.size()));
        ArrayList<String> quiz_word_kors = quiz_word.getKors();
        System.out.println("주어진 한글: " + quiz_word_kors.get((int)(Math.random()*quiz_word_kors.size())));
        System.out.println();
        String choice1_eng = "";
        String choice2_eng = "";
        String choice3_eng = "";
        String quiz_word_eng = quiz_word.getEng();

        while(choice1_eng.equals(choice2_eng) || choice2_eng.equals(choice3_eng) || choice1_eng.equals(choice3_eng) || choice1_eng.equals(quiz_word_eng) || choice2_eng.equals(quiz_word_eng) || choice3_eng.equals(quiz_word_eng))
        {
            Word choice1 = quiz_array.get((int)(Math.random()*quiz_array.size()));
            choice1_eng = choice1.getEng();
            Word choice2 = quiz_array.get((int)(Math.random()*quiz_array.size()));
            choice2_eng = choice2.getEng();
            Word choice3 = quiz_array.get((int)(Math.random()*quiz_array.size()));
            choice3_eng = choice3.getEng();
        }

        int[] order = {0,1,2,3};
        Random rand = new Random();
        for(int i =0;i<4;i++)
        {
            int r = rand.nextInt(4);
            int temp = order[i];
            order[i] = order[r];
            order[r] = temp;
        }
        String[] arr = {quiz_word_eng, choice1_eng, choice2_eng, choice3_eng};
        for(int i = 0;i<4;i++)
            System.out.println("("+i+") " + arr[order[i]]);

        while(wrong_time!=3)
        {
            System.out.print("사용자의 답: ");
            int user_input = scan.nextInt();
            scan.nextLine();

            if(arr[user_input-1].equals(quiz_word_eng))
            {
                System.out.println("정답입니다!");
                break;
            }
            else
            {
                System.out.println("틀렸습니다");
                wrong_time++;
                continue;
            }
        }

        if(wrong_time!=0&&wrong_time!=3)
        {
            quiz_word.setWrong_number(quiz_word.getWrong_number()+1);
        }
        else if(wrong_time == 3)
        {
            System.out.println("정답을 맞추지 못하였습니다");
            quiz_word.setWrong_number(quiz_word.getWrong_number()+1);
        }
    }

    private void quiz_essay() {
        ArrayList<Word> quiz_array = voc;
        System.out.println("----------------------------");
        System.out.println("다음으로 보여지는 한글 뜻을 가지고 영어를 입력하시면 됩니다. 기회는 총 3번입니다");
        System.out.println("3초 뒤 퀴즈가 시작됩니다");
        System.out.println();
        long beforeStart_time_limit = 3_000_000_000L;
        long beforeStart_start = System.nanoTime();
        while(true)
        {
            long beforeStart_end = System.nanoTime();
            if(beforeStart_end - beforeStart_start >=beforeStart_time_limit)
            {
                break;
            }
        }
        int wrong_time=0;
        Word quiz_word = quiz_array.get((int)(Math.random()*quiz_array.size()));
        ArrayList<String> quiz_word_kors = quiz_word.getKors();
        System.out.println("주어진 한글: " + quiz_word_kors.get((int)(Math.random()*quiz_word_kors.size())));
        while(wrong_time != 3)
        {
            System.out.print("사용자의 답: ");
            String user_input = scan.nextLine();
            String user_input_trim_ver = user_input.trim();
            if(user_input_trim_ver.equals(quiz_word.getEng()))
            {
                System.out.println("정답입니다!");
                break;
            }
            else
            {
                System.out.println("정답이 아닙니다! 다른 답을 입력해주세요");
                wrong_time++;
                continue;
            }
        }

        if(wrong_time!=0&&wrong_time!=3)
        {
            quiz_word.setWrong_number(quiz_word.getWrong_number()+1);
        }
        else if(wrong_time == 3)
        {
            System.out.println("정답을 맞추지 못하였습니다");
            quiz_word.setWrong_number(quiz_word.getWrong_number()+1);
        }

    }

    private void quiz() {
        System.out.println("---------------------------");
        System.out.println("1) 객관식 퀴즈");
        System.out.println("2) 주관식 퀴즈");
        System.out.print("퀴즈 선택: ");
        int user_choice = scan.nextInt();
        scan.nextLine();
        System.out.println();

        switch (user_choice)
        {
            case 1 -> quiz_multiChoice();
            case 2 -> quiz_essay();
            default -> System.out.println("메뉴를 다시 선택하세요");
        }
        return;
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
