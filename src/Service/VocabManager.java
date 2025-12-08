package Service;

import Vocab.Word;
import Exception.MenuRangeCheckException;

import java.util.*;

public class VocabManager extends FileManager {
    String userName;
    ArrayList<Word> voc = new ArrayList<>();  //여기다 주로 저장
    HashMap<String, Word> vocabMap = new HashMap<>();  //이건 검색용이라 저장에는 포함되지 않을 예정.

    HashMap<String, String> exampleMap = new HashMap<>();  // 영단어와 예문 1:1 매핑

    private static final Scanner scan = new Scanner(System.in); //사용자로부터 입력받을 스캐너

    public VocabManager(String userName) {
        this.userName = userName;
    }   //생성자

    public String getUserName() {
        return userName;
    }

    public void setAll(Collection<Word> list) {
        voc.clear(); //깔끔하게 비움
        vocabMap.clear();
        if (list != null) {
            voc.addAll(list); //전부 넣기

            //초기 로딩시 HashMap도 자동 초기화
            for (Word w : list) {
                vocabMap.put(w.getEng(), w);
            }
        }
    }

    // 예문 Map 세팅/조회용 메서드
    public void setExampleMap(Map<String, String> map) {
        exampleMap.clear();
        if (map != null) {
            exampleMap.putAll(map);
        }
    }

    public Map<String, String> getExampleMap() {
        return exampleMap;
    }

    public ArrayList<Word> getVoc() {
        return voc;
    }  //이걸 메인에서 저장할거임

    //메뉴
    public void menu() {
        int choice = 0;
        while (choice != 10) {
            System.out.println("\n------ " + userName + "의 단어장 -------");
            System.out.println("1) 단어 추가");
            System.out.println("2) 단어 수정 (영어/뜻)");
            System.out.println("3) 단어 삭제");
            System.out.println("4) 단어 검색 (영→한 / 한→영)");
            System.out.println("5) 퀴즈");
            System.out.println("6) 오답노트 보기");
            System.out.println("7) 오답노트 재시험");
            System.out.println("8) 오늘의 추천 단어");
            System.out.println("9) 즐겨찾기");
            System.out.println("10) 종료");
            System.out.print("메뉴 선택: ");

            try {
                choice = scan.nextInt();
                scan.nextLine();
                System.out.println();

                // 1~10만 허용함
                if (!((choice >= 1 && choice <= 10))) {
                    throw new MenuRangeCheckException("메뉴는 1~10만 입력 가능합니다.");
                }

                switch (choice) {
                    case 1 -> addVocab();
                    case 2 -> editVocab();
                    case 3 -> deleteVocab();
                    case 4 -> searchVocab();
                    case 5 -> quiz();
                    case 6 -> show_wrongWord();
                    case 7 -> voc_test();
                    case 8 -> showRandomWords(5);
                    case 9 -> bookMark();
                    case 10 -> System.out.println("종료합니다");
                    default -> System.out.println("메뉴를 다시 선택하세요");
                }
            } catch (MenuRangeCheckException e) {
                System.out.println(e.getMessage());
            } catch (InputMismatchException e) {
                System.out.println("정수로 입력해주세요.");
                scan.nextLine(); // 버퍼 비우기
            }
        }
    }

    private void bookMark() {
        ArrayList<Word> bookMark_list = new ArrayList<>();
        for (Word word : voc) {
            if (word.isBookMark()) {
                bookMark_list.add(word);
            }
        }
        System.out.println("===== 즐겨찾기 =====");
        for (Word word : bookMark_list) {
            System.out.println(word);
        }
        System.out.print("\n1) 즐겨찾기 추가하기 2) 즐겨찾기 삭제하기 3) 메뉴로 돌아가기 ");
        try{
            int user_input = scan.nextInt();
            scan.nextLine();
            if(user_input !=1&&user_input!=2&&user_input !=3)
                throw new MenuRangeCheckException("메뉴는 1~3만 입력 가능합니다.");
            else if(user_input == 1)
            {
                add_bookMark();
                return;
            }
            else if(user_input == 2)
            {
                remove_bookMark(bookMark_list);
                return;
            }
            else
                return;
        }
        catch (MenuRangeCheckException e) {
            System.out.println(e.getMessage());
        } catch (InputMismatchException e) {
            System.out.println("정수로 입력해주세요.");
            scan.nextLine(); // 버퍼 비우기
        }
    }

    private void remove_bookMark(ArrayList<Word> bookMark_list) {
        System.out.print("[즐겨찾기] 즐겨찾기 삭제할 영단어를 입력하세요: ");
        String eng = scan.nextLine().trim();

        for(int i = 0;i<bookMark_list.size();i++)  //처음부터 즐겨찾기에 있는 것만 검사(true인 것만 검사)
        {
            if(eng.equals(bookMark_list.get(i).getEng()))  //즐겨찾기 삭제할 단어랑 즐겨찾기 단어(bookmark = true)가 일치하는 게 있다면
            {
                System.out.println("삭제 되었습니다!");
                bookMark_list.get(i).setBookMark(false); //false로 바꿔서 삭제시키고
                return;  //종료 -> 어짜피 단어 리스트에 같은 단어는 안들어가므로 바로 종료해도 됨
            }
        }
        System.out.println("일치하는 단어가 즐겨찾기에 없습니다");
    }

    private void add_bookMark() {
        System.out.print("[즐겨찾기] 즐겨찾기할 영단어를 입력하세요: ");
        String eng = scan.nextLine().trim();

        for(int i = 0;i<voc.size();i++)
        {
            if(eng.equals(voc.get(i).getEng()))
            {
                if(voc.get(i).isBookMark())  //즐겨찾기에 있는 단어인지 검사
                {
                    System.out.println("이미 즐겨찾기에 있습니다");
                    return;
                }
                System.out.println("즐겨찾기 단어 추가가 완료 되었습니다");  //즐겨찾기할 단어가 전체 리스트에 있다면
                voc.get(i).setBookMark(true);
                return;
            }
        }
        System.out.println("일치하는 단어가 단어장에 없습니다!");
    }


    //랜덤 단어를 count개 만큼 보여줌
    private void showRandomWords(int count) {
        if (voc.isEmpty()) {
            System.out.println("단어장이 비어있습니다.");
            return;
        }
        //이미 뽑은 인덱스를 저장해 두는 Set
        //같은 단어 두 번 추천 안 나오게 하기 위함.
        Set<Integer> used = new HashSet<>();
        Random rand = new Random();

        System.out.println("\n====== 오늘의 추천 단어 (" + count + "개) ======");

        for (int i = 0; i < count && i < voc.size(); i++) {
            int idx;
            do {
                idx = rand.nextInt(voc.size());
            } while (!used.add(idx)); //이미 뽑힌 인덱스면 다시 뽑기

            Word w = voc.get(idx);

            // 예문 가져오기
            String ex = exampleMap.get(w.getEng());
            if (ex == null || ex.trim().isEmpty()) {
                ex = "(예문 없음)";
            }

            // 단어 출력
            System.out.println("\n----------------------------------");
            System.out.println("* 영어      : " + w.getEng());
            System.out.println("* 뜻        : " + String.join("/ ", w.getKors()));
            System.out.println("* 예문      : " + ex);
        }
        System.out.println("----------------------------------");
    }



    //오답 단어만 재시험
    private void voc_test() {
        ArrayList<Word> test_array = new ArrayList<>();
        ArrayList<Word> exam_pass_array = new ArrayList<>();

        for (Word word : voc) {
            if (word.getWrong_number() != 0) {
                test_array.add(word);
            }
        }
        if (test_array.isEmpty()) {
            System.out.println("틀린 단어가 없습니다!");
            return;
        } //틀린 횟수가 1이상이면 시험볼 배열에 넣기

        System.out.println("한글 뜻을 보고 영어를 입력하세요");
        Collections.shuffle(test_array);
        for (int i = 0; i < test_array.size(); i++) {
            System.out.print(test_array.get(i).getKors() + ": ");
            Scanner scan = new Scanner(System.in);
            String test_eng = scan.nextLine();
            if (test_eng.equals(test_array.get(i).getEng())) {
                test_array.get(i).setWrong_number(test_array.get(i).getWrong_number() - 1);
                exam_pass_array.add(test_array.get(i));
            }
        } //맞추면 wrong_number 1씩 줄여주기, 맞춘 단어를 출력하기 위해 맞추면 array add
        System.out.println("시험이 종료되었습니다 수고하셨습니다");
        if (exam_pass_array.isEmpty())
            System.out.println("맞춘 단어가 없습니다");
        else {
            System.out.println("---맞춘 단어---");
            for (Word word : exam_pass_array) {
                System.out.println(word);
            }
        } //맞춘 단어 출력
    }


    private int totalQuizCount = 0;   // 총 문제 수
    private int correctCount = 0;     // 맞춘 문제 수
    private int wrongCount = 0;       // 틀린 문제 수

    //오답노트 정보 보기
    private void show_wrongWord() {
        System.out.println("\n====== 학습 통계 ======");

        if (totalQuizCount == 0) {
            System.out.println("아직 퀴즈 기록이 없습니다.");
            System.out.println("총 퀴즈 문제 수 : 0");
            System.out.println("맞은 문제 수   : 0");
            System.out.println("틀린 문제 수   : 0");
            System.out.println("정답률         : 0.00%\n");
        } else {
            double correctRate = (correctCount * 100.0) / totalQuizCount; // 정답률

            System.out.println("총 퀴즈 문제 수 : " + totalQuizCount);
            System.out.println("맞은 문제 수   : " + correctCount);
            System.out.println("틀린 문제 수   : " + wrongCount);
            System.out.printf("정답률         : %.2f%%\n", correctRate);
        }

        // 퀴즈를 안 봤어도 오답 목록/통계는 항상 보여주기
        showTop5();
        showAllWrongWords();
    }


    //선택정렬로 내림차순하는 코드
    private void selectionSort(ArrayList<Word> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            int maxIndex = i;
            for (int j = i + 1; j < list.size(); j++) {
                if (list.get(j).getWrong_number() > list.get(maxIndex).getWrong_number()) {
                    maxIndex = j;
                }
            }
            // swap 하기
            if (maxIndex != i) {
                Word temp = list.get(i);
                list.set(i, list.get(maxIndex));
                list.set(maxIndex, temp);
            }
        }
    }

    //상위 5개 오답 보기
    private void showTop5() {
        //원본 손상 방지용
        ArrayList<Word> sorted = new ArrayList<>(voc);
        selectionSort(sorted);

        System.out.println("\n--- 오답 상위 TOP 5 ---");
        int count = 0;
        for (Word w : sorted) {
            if (w.getWrong_number() == 0)
                break;  // 오답 없는 단어 이후는 필요 없음
            System.out.println(w.getEng() + " : " + w.getWrong_number() + "회");
            count++;
            if (count == 5)
                break;
        }

        if (count == 0) {
            System.out.println("오답이 없습니다!");
        }
    }

    //전체 오답리스트 보기
    private void showAllWrongWords() {

        ArrayList<Word> sorted = new ArrayList<>(voc);
        selectionSort(sorted); //voc에서 가져온 ArrayList를 selectionSort메소드를 이용해서 정렬하기

        System.out.println("\n------ 전체 오답 단어 목록 (내림차순) -------");

        boolean any = false;
        for (Word w : sorted) {
            if (w.getWrong_number() == 0)  //wrong_number이 1보다 커야 오답노트 출력함
                continue;
            any = true;
            System.out.println(w.getEng() + " : " + w.getWrong_number() + "회");
        }

        if (!any) {
            System.out.println("오답 단어가 없습니다!");
        }
    }


    //객관식 쉬움모드
    private void quiz_multiChoice() {
        ArrayList<Word> quiz_array = voc;
        totalQuizCount++;
        if (quiz_array.size() < 4) {
            System.out.println("단어장에 최소 4개 이상의 단어가 들어가있어야 합니다.");
            return;
        }
        System.out.println("----------------------------");
        System.out.println("다음으로 보여지는 한글 뜻을 가지는 영단어를 골라주시면 됩니다. 기회는 총 3번입니다");
        System.out.println("3초 뒤 퀴즈가 시작됩니다");
        System.out.println();
        long beforeStart_time_limit = 3_000_000_000L;
        long beforeStart_start = System.nanoTime();
        while (true) {
            long beforeStart_end = System.nanoTime();
            if (beforeStart_end - beforeStart_start >= beforeStart_time_limit) {
                break;
            }
        }
        int wrong_time = 0;
        Word quiz_word = quiz_array.get((int) (Math.random() * quiz_array.size()));
        ArrayList<String> quiz_word_kors = quiz_word.getKors();
        System.out.println("주어진 한글: " + quiz_word_kors.get((int) (Math.random() * quiz_word_kors.size())));
        System.out.println();
        String choice1_eng = "";
        String choice2_eng = "";
        String choice3_eng = "";
        String quiz_word_eng = quiz_word.getEng();

        while (choice1_eng.equals(choice2_eng) || choice2_eng.equals(choice3_eng) || choice1_eng.equals(choice3_eng) || choice1_eng.equals(quiz_word_eng) || choice2_eng.equals(quiz_word_eng) || choice3_eng.equals(quiz_word_eng)) {
            Word choice1 = quiz_array.get((int) (Math.random() * quiz_array.size()));
            choice1_eng = choice1.getEng();
            Word choice2 = quiz_array.get((int) (Math.random() * quiz_array.size()));
            choice2_eng = choice2.getEng();
            Word choice3 = quiz_array.get((int) (Math.random() * quiz_array.size()));
            choice3_eng = choice3.getEng();
        }

        String[] arr = {quiz_word_eng, choice1_eng, choice2_eng, choice3_eng};
        Random rand = new Random();
        for (int i = arr.length - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            String temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
        for (int i = 0; i < 4; i++)
            System.out.println("(" + (i + 1) + ") " + arr[i]);

        while (wrong_time != 3) {
            System.out.print("사용자의 답: ");
            try {
                int user_input = Integer.parseInt(scan.nextLine());
                //scan.nextLine();
                user_input--; //답이랑 매칭이 안돼있어서 추가했습니다

                if (user_input < 0 || user_input >= 4) {
                    System.out.println("1~4 사이의 번호를 입력해주세요.");
                    continue;
                }

                if (arr[user_input].equals(quiz_word_eng)) {
                    System.out.println("정답입니다!");
                    correctCount++;
                    break;
                } else {
                    System.out.println("틀렸습니다");
                    wrong_time++;
                    //continue;
                }
            } catch (NumberFormatException e) {
                System.out.println("숫자만 입력해주세요");
            }
        }

        if (wrong_time != 0 && wrong_time != 3) {
            quiz_word.setWrong_number(quiz_word.getWrong_number() + 1);
            wrongCount++;
        } else if (wrong_time == 3) {
            System.out.println("정답을 맞추지 못하였습니다");
            quiz_word.setWrong_number(quiz_word.getWrong_number() + 1);
            wrongCount++;
        }
    }

    //주관식 (한글 보여주고 영어 맞추기)
    private void quiz_essay() {

        if (voc.isEmpty()) {
            System.out.println("단어장이 비어있어 퀴즈를 진행할 수 없습니다.");
            return;
        } //단어장 비어있는 지 검사

        ArrayList<Word> quiz_array = voc;
        totalQuizCount++;
        System.out.println("----------------------------");
        System.out.println("다음으로 보여지는 한글 뜻을 가지고 영어를 입력하시면 됩니다. 기회는 총 3번입니다");
        System.out.println("3초 뒤 퀴즈가 시작됩니다");
        System.out.println();
        long beforeStart_time_limit = 3_000_000_000L;
        long beforeStart_start = System.nanoTime();
        while (true) {
            long beforeStart_end = System.nanoTime();
            if (beforeStart_end - beforeStart_start >= beforeStart_time_limit) {
                break;
            }
        } //nanotime이용해서 준비할 시간 3초
        int wrong_time = 0;   //3번의 기회
        Word quiz_word = quiz_array.get((int) (Math.random() * quiz_array.size()));   //quiz_array에서 random index의 Word 객체 가져오기
        ArrayList<String> quiz_word_kors = quiz_word.getKors();  //뜻 여러개 있을 때를 대비
        System.out.println("주어진 한글: " + quiz_word_kors.get((int) (Math.random() * quiz_word_kors.size())));   //무작위로 가져온 단어의 한글 뜻 보여주기
        while (wrong_time != 3) {
            System.out.print("사용자의 답: ");
            String user_input = scan.nextLine();
            String user_input_trim_ver = user_input.trim();
            if (user_input_trim_ver.equals(quiz_word.getEng())) {
                System.out.println("정답입니다!");
                correctCount++;
                break;
            } else {
                System.out.println("정답이 아닙니다! 다른 답을 입력해주세요");
                wrong_time++;
                //continue;
            }
        }  //틀리면 wrong_time++시키고 wrong_time!=3일동안에 계속해서 영어를 trim을 이용해서 공백 제거해서 입력받기

        if (wrong_time != 0 && wrong_time != 3) {
            quiz_word.setWrong_number(quiz_word.getWrong_number() + 1);
            wrongCount++;
        } else if (wrong_time == 3) {
            System.out.println("정답을 맞추지 못하였습니다");
            quiz_word.setWrong_number(quiz_word.getWrong_number() + 1);
            wrongCount++;
        }  // 0이 아니면 틀린횟수++, 틀린횟수가 3이면 정답을 맞추지 못하였다는 문구 띄우기
    }

    //퀴즈 메서드
    private void quiz() {
        System.out.println("---------------------------");
        System.out.println("1) 객관식 퀴즈");
        System.out.println("2) 주관식 퀴즈");
        System.out.print("퀴즈 선택: ");

        try {
            int user_choice = scan.nextInt();
            scan.nextLine();
            System.out.println();

            if (!((user_choice >= 1 && user_choice <= 2))) {
                throw new MenuRangeCheckException("메뉴는 1 or 2만 입력 가능합니다.");
            }

            switch (user_choice) {
                case 1 -> quiz_multiMenu();   // 객관식 메뉴로 분리
                case 2 -> quiz_essay();
                default -> System.out.println("메뉴를 다시 선택하세요");
            }
        } catch (MenuRangeCheckException e) {
            System.out.println(e.getMessage());
        } catch (InputMismatchException e) {
            System.out.println("정수만 입력해주세요");
            scan.nextLine(); // 버퍼 비우기
        }
    }

    // 객관식 메뉴 (easy / hard)
    private void quiz_multiMenu() {
        System.out.println("------ 객관식 모드 선택 ------");
        System.out.println("1) Easy 모드 ");
        System.out.println("2) Hard 모드 ");
        System.out.print("선택: ");

        try {
            int mode = scan.nextInt();
            scan.nextLine();
            System.out.println();

            if (!((mode >= 1 && mode <= 2))) {
                throw new MenuRangeCheckException("메뉴는 1 or 2만 입력 가능합니다.");
            }

            switch (mode) {
                case 1 -> quiz_multiChoice();        // by 의찬
                case 2 -> quiz_multiChoiceHard();    // 하드모드
                default -> System.out.println("메뉴를 다시 선택하세요");
            }
        } catch (MenuRangeCheckException e) {
            System.out.println(e.getMessage());
        } catch (InputMismatchException e) {
            System.out.println("정수만 입력해주세요");
            scan.nextLine(); // 버퍼 비우기
        }
    }

    private void quiz_multiChoiceHard() {
        //System.out.println("퀴즈 하드모드 구현 필요");
        // 1) 단어 수 체크
        if (voc.size() < 4) {
            System.out.println("단어가 4개 이상 있어야 하드 모드를 진행할 수 있습니다.");
            return;
        }

        // 2) 문제에 사용할 단어 풀 만들기
        ArrayList<Word> pool = new ArrayList<>(voc);
        Random rand = new Random();

        int[] rand_array = new int[5];
        for(int i = 0;i<5;i++)
        {
            rand_array[i] = (int) (Math.random()*voc.size());
        }

        // 최대 5문제까지 출제, 만약 5개 보다 적으면 일찍 끝냄
        int numQuestions = Math.min(5, pool.size());

        int localCorrect = 0; // 이번 하드 모드에서 맞힌 개수
        int localWrong = 0;   // 이번 하드 모드에서 틀린 개수

        for (int q = 0; q < numQuestions; q++) {
            Word answer = pool.get(rand_array[q]); // 이번 문제의 정답 단어

            // 2-1) 한글 뜻 문자열로 만들기 ("뜻1 / 뜻2 / 뜻3")
            StringBuilder korSb = new StringBuilder();
            for (int i = 0; i < answer.getKors().size(); i++) {
                if (i > 0) korSb.append(" / ");
                korSb.append(answer.getKors().get(i));
            }

            // 2-2) 보기 목록 만들기: 정답 1개 + 오답 3개
            ArrayList<Word> options = new ArrayList<>();
            options.add(answer); // 정답 먼저 넣기

            // 오답 3개 뽑기 (voc 전체에서 랜덤)
            while (options.size() < 4) {
                Word candidate = voc.get(rand.nextInt(voc.size()));
                if (!options.contains(candidate)) { // 이미 들어간 단어는 제외
                    options.add(candidate);
                }
            }

            // 2-3)
            // 보기 순서를 랜덤하게 출력(직접 섞기) ---------
            ArrayList<Word> temp = new ArrayList<>(options);
            ArrayList<Word> displayed = new ArrayList<>();

            System.out.println("\n[" + (q + 1) + "번 문제]");
            System.out.println("뜻: " + korSb);

            for (int i = 0; i < 4; i++) {
                int idx = rand.nextInt(temp.size());
                Word w = temp.get(idx);
                displayed.add(w);
                temp.remove(idx);

                System.out.println((i + 1) + ") " + w.getEng());
            }

            // 2-4) 정답 위치 찾기
            int correctIndex = -1;
            for (int i = 0; i < displayed.size(); i++) {
                if (displayed.get(i) == answer) {
                    correctIndex = i;
                    break;
                }
            }

            //사용자 입력 처리
            System.out.print("정답 번호를 입력하세요 (1~4): ");
            String input = scan.nextLine().trim();

            // 전체 퀴즈 통계: 문제 하나 풀 때마다 +1
            totalQuizCount++;

            boolean isCorrect = false;

            // 4) 정답 판정: 1~4 중에서 정답 번호면 true, 그 외는 전부 오답
            if (input.matches("[1-4]")) {
                int choice = Integer.parseInt(input);
                if (choice - 1 == correctIndex) {
                    isCorrect = true;
                }
            }
            // matches 안 맞거나 번호가 달라도 isCorrect는 false → 전부 오답

            // 5) 결과 처리 및 통계 업데이트
            if (isCorrect) {
                System.out.println("정답입니다! ");
                correctCount++;
                localCorrect++;
            } else {
                System.out.println("오답입니다. ");
                System.out.println("정답: " + (correctIndex + 1) + ") " + answer.getEng());
                wrongCount++;
                localWrong++;
                // 이 단어의 오답 횟수도 1 증가
                answer.setWrong_number(answer.getWrong_number() + 1);
            }
        }
        System.out.println();
        System.out.println("====== 하드 모드 결과 ======");
        System.out.println("총 " + numQuestions + "문제 중 " + localCorrect + "개 정답, " + localWrong + "개 오답.");
    }
    //*********************  검색 메서드 **********************

    //검색 메서드
    private void searchVocab() {
        if (voc.isEmpty()) {
            System.out.println("단어장이 비어있습니다.");
            return;
        }
        int dir = -1;
        while (true) {
            System.out.println("\n[검색] 방향을 선택하세요: 1) 영->한 2) 한->영");
            System.out.print(">> ");
            try {
                dir = Integer.parseInt(scan.nextLine());
                // 1이나 2면 통과(반복 종료)
                if (dir == 1 || dir == 2) {
                    break;
                } else {
                    System.out.println("잘못된 입력입니다. 1 혹은 2 중에 선택해주세요.");
                }
            } catch (NumberFormatException e) {
                System.out.println("숫자만 입력해주세요.");
            }
        }

        List<Word> result; //검색 결과를 저장할 리스트

        if (dir == 1) {
            System.out.print("검색할 영단어 입력: ");
            String q1 = scan.nextLine().trim();
            result = findEngSubString(q1);
            //빈 입력 하면 전체가 출력됨. 방지
            if (q1.isEmpty()) {
                System.out.println("빈 입력입니다. 검색을 취소합니다.");
                return;
            }
        } else if (dir == 2) {
            System.out.print("검색할 한글 입력: ");
            String q2 = scan.nextLine().trim();
            result = findKorSubString(q2);
            //마찬가지로 방지
            if (q2.isEmpty()) {
                System.out.println("빈 입력입니다. 검색을 취소합니다.");
                return;
            }
        } else {
            System.out.println("잘못된 입력입니다. 1 혹은 2 중에 선택해주세요.");
            return;
        }

        if (result.isEmpty()) {
            System.out.println("검색 결과가 없습니다.");
        } else {
            System.out.println("검색결과) " + result.size() + "개");
            for (Word word : result) {
                System.out.println(word);

                //예문도 같이 출력 (추가 기능)
                String ex=exampleMap.get(word.getEng());
                if( ex != null){
                    System.out.println("  예문:  "+ex);
                } else{
                    System.out.println("  예문: (아직 등록된 예문이 없습니다)");
                }
            }
        }
    }

    //한글로 검색해서 찾기
    public List<Word> findKorSubString(String q) {
        ArrayList<Word> res = new ArrayList<>();
        for (Word w : voc) {
            for (String k : w.getKors()) {
                if (k.contains(q)) {
                    res.add(w);
                    break;
                }
            }
        }
        return res;
    }

    //영어로 검색해서 찾기
    public List<Word> findEngSubString(String q) {
        ArrayList<Word> res = new ArrayList<>();
        for (Word w : voc) {
            String s = w.getEng();
            if (s.contains(q))
                res.add(w);
        }
        return res;
    }

    //삭제 기능
    private void deleteVocab() {
        if (voc.isEmpty()) {
            System.out.println("단어장이 비어있습니다.");
            return;
        }
        System.out.print("[삭제] 영어 단어 입력: ");
        String eng = scan.nextLine().trim();

        //맵에서 먼저 찾기
        Word w = vocabMap.get(eng);
        if (w == null) {
            System.out.println("해당 단어가 존재하지 않습니다.");
            return;
        }

        System.out.print("정말 삭제하시겠습니까? (Y/N) : ");
        String confirm = scan.nextLine().trim();
        if (confirm.equalsIgnoreCase("Y")) {
            // 1) 리스트에서 객체 제거
            voc.remove(w);  // 같은 Word 객체 참조라서 이거면 ok
            // 2) 맵에서도 키 제거
            vocabMap.remove(eng);
            // 3) 예문 맵에서도 제거
            exampleMap.remove(eng);

            System.out.println("'" + eng + "' 단어 삭제 완료!");
        } else {
            System.out.println("삭제 취소!");
        }
    }

    //GUI 용 삭제 메서드
    // eng 단어 삭제 (voc, vocabMap, exampleMap 모두 정리)
    public String deleteWord(String eng) {
        if (eng == null) eng = "";
        eng = eng.trim();

        if (eng.isEmpty()) {
            return "영어 단어가 비어 있습니다.";
        }

        Word w = vocabMap.get(eng);
        if (w == null) {
            return "해당 단어가 존재하지 않습니다.";
        }

        // 1) 리스트에서 제거
        voc.remove(w);          // 같은 객체 참조라 이 한 줄이면 충분
        // 2) 맵에서 제거
        vocabMap.remove(eng);
        // 3) 예문 맵에서도 제거
        exampleMap.remove(eng);

        return "'" + eng + "' 단어 삭제 완료!";
    }



    //수정 기능 (CLI 버전)
    private void editVocab() {
        if (voc.isEmpty()) {
            System.out.println("단어장이 비어있습니다.");
            return;
        }

        System.out.print("[수정] 영어 단어 입력: ");
        String eng = scan.nextLine().trim();  // 정확히 일치하는 단어만 수정

        Word w = vocabMap.get(eng);
        if (w == null) {
            System.out.println("해당 단어가 존재하지 않습니다.");
            return;
        }

        // 현재 상태 출력
        System.out.println("\n[수정] 영어: " + w.getEng());

        System.out.print("현재 뜻: ");
        for (int i = 0; i < w.getKors().size(); i++) {
            System.out.print("[" + (i + 1) + "] " + w.getKors().get(i) + " ");
        }
        System.out.println();

        String curEx = exampleMap.get(w.getEng());
        System.out.println("현재 예문: " + (curEx != null ? curEx : "(등록된 예문 없음)"));

        // 수정 메뉴
        System.out.println("\n[수정 메뉴]");
        System.out.println("1) 영어 단어 수정");
        System.out.println("2) 한글 뜻 추가");
        System.out.println("3) 한글 뜻 삭제");
        System.out.println("4) 예문 수정");
        System.out.println("0) 취소");

        int choice = -1;
        while (true) {
            System.out.print("선택: ");
            String line = scan.nextLine().trim();
            try {
                choice = Integer.parseInt(line);
                if (choice >= 0 && choice <= 4) {
                    break;
                } else {
                    System.out.println("0~4 사이의 메뉴를 선택해주세요.");
                }
            } catch (NumberFormatException e) {
                System.out.println("숫자를 입력해주세요.");
            }
        }

        switch (choice) {
            case 0 -> {
                System.out.println("수정을 취소합니다.");
                return;
            }

            // 1) 영어 단어 수정 (중복 방지 + 예문 key도 함께 변경)
            case 1 -> {
                System.out.print("새 영어 단어 입력: ");
                String newEng = scan.nextLine().trim();
                if (newEng.isEmpty()) {
                    System.out.println("입력이 비어 있습니다. 수정 취소.");
                    return;
                }

                // 이미 존재하는 단어와 충돌하면 안 됨
                if (!newEng.equals(eng) && vocabMap.containsKey(newEng)) {
                    System.out.println("이미 존재하는 영어 단어입니다. 다른 단어를 입력해주세요.");
                    return;
                }

                vocabMap.remove(eng);   // 예전 key 제거
                w.setEng(newEng);       // Word 객체 내부 eng 변경
                vocabMap.put(newEng, w);

                // 예문 key도 함께 변경
                updateExampleKey(eng, newEng);

                System.out.println("영어 단어 수정 완료!");
            }

            // 2) 한글 뜻 추가 (중복 방지)
            case 2 -> {
                System.out.print("추가할 뜻 입력 (/로 여러 개 가능): ");
                String line = scan.nextLine().trim();
                if (line.isEmpty()) {
                    System.out.println("입력이 비어 있습니다.");
                    return;
                }

                String[] addList = line.split("/");
                int added = 0;

                for (String k : addList) {
                    k = k.trim();
                    if (!k.isEmpty() && !w.getKors().contains(k)) {
                        w.getKors().add(k);
                        added++;
                    }
                }

                if (added == 0) {
                    System.out.println("모든 뜻이 이미 존재합니다. 추가된 뜻이 없습니다.");
                } else {
                    System.out.println("뜻 " + added + "개가 추가되었습니다.");
                    System.out.println("현재 뜻: " + w.getKors());
                }
            }

            // 3) 한글 뜻 삭제 (번호 선택)
            case 3 -> {
                if (w.getKors().isEmpty()) {
                    System.out.println("삭제할 뜻이 없습니다.");
                    return;
                }

                System.out.println("삭제할 뜻 번호를 선택하세요 (0 = 취소):");
                for (int i = 0; i < w.getKors().size(); i++) {
                    System.out.println((i + 1) + ") " + w.getKors().get(i));
                }

                int idx = -1;
                while (true) {
                    System.out.print("번호 입력: ");
                    String s = scan.nextLine().trim();
                    try {
                        idx = Integer.parseInt(s);
                        if (idx == 0) {
                            System.out.println("삭제를 취소합니다.");
                            return;
                        }
                        if (1 <= idx && idx <= w.getKors().size()) {
                            break;
                        } else {
                            System.out.println("1~" + w.getKors().size() + " 사이의 번호 또는 0을 입력해주세요.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("숫자를 입력해주세요.");
                    }
                }

                String removed = w.getKors().remove(idx - 1);
                System.out.println("뜻 '" + removed + "' 삭제 완료!");

                if (w.getKors().isEmpty()) {
                    System.out.println("남은 뜻이 없습니다. 필요하면 뜻을 다시 추가해주세요.");
                } else {
                    System.out.println("현재 뜻: " + w.getKors());
                }
            }

            // 4) 예문 수정
            case 4 -> {
                String cur = exampleMap.get(w.getEng());
                if (cur != null) {
                    System.out.println("현재 예문: " + cur);
                } else {
                    System.out.println("현재 예문: (등록된 예문 없음)");
                }

                System.out.print("새 예문을 입력하세요 (그냥 엔터면 예문 삭제): ");
                String newEx = scan.nextLine().trim();

                if (newEx.isEmpty()) {
                    exampleMap.remove(w.getEng());
                    System.out.println("예문이 삭제되었습니다.");
                } else {
                    exampleMap.put(w.getEng(), newEx);
                    System.out.println("예문이 저장되었습니다.");
                }
            }

            default -> System.out.println("잘못된 선택입니다.");
        }
    }

//**************단어 수정 관련 메서드 ******************************

//수정 기능 GUI 버전
// 0) 단어 찾기 (GUI에서 현재 상태 불러올 때 사용)
    public Word findExact(String eng) {
        if (eng == null) return null;
        return vocabMap.get(eng.trim());
    }

    // 1) 영어 단어 이름 변경
    public String renameEng(String oldEng, String newEng) {
        if (oldEng == null) oldEng = "";
        if (newEng == null) newEng = "";
        oldEng = oldEng.trim();
        newEng = newEng.trim();

        Word w = vocabMap.get(oldEng);
        if (w == null) return "해당 단어가 존재하지 않습니다.";

        if (newEng.isEmpty()) {
            return "입력이 비어 있습니다. 수정 취소.";
        }
        if (!newEng.equals(oldEng) && vocabMap.containsKey(newEng)) {
            return "이미 존재하는 영어 단어입니다. 다른 단어를 입력해주세요.";
        }

        // 맵 키 변경
        vocabMap.remove(oldEng);
        w.setEng(newEng);
        vocabMap.put(newEng, w);

        // 예문 key도 같이 변경
        updateExampleKey(oldEng, newEng);

        return "영어 단어가 '" + oldEng + "' → '" + newEng + "' 로 수정되었습니다.";
    }

    // 2) 한글 뜻 추가 (/로 여러 개 가능)
    public String addKorMeanings(String eng, String korLine) {
        if (eng == null) eng = "";
        if (korLine == null) korLine = "";
        eng = eng.trim();
        korLine = korLine.trim();

        Word w = vocabMap.get(eng);
        if (w == null) return "해당 단어가 존재하지 않습니다.";
        if (korLine.isEmpty()) return "입력이 비어 있습니다.";

        String[] addList = korLine.split("/");
        int before = w.getKors().size();

        java.util.Arrays.stream(addList)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .filter(s -> !w.getKors().contains(s))
                .forEach(s -> w.getKors().add(s));

        int added = w.getKors().size() - before;

        if (added == 0) {
            return "모든 뜻이 이미 존재합니다. 추가된 뜻이 없습니다.";
        } else {
            return "뜻 " + added + "개가 추가되었습니다. 현재 뜻: " + w.getKors();
        }
    }

    // 3) 한글 뜻 삭제 (1-based index)
    public String removeKorMeaning(String eng, int index1Based) {
        if (eng == null) eng = "";
        eng = eng.trim();
        Word w = vocabMap.get(eng);
        if (w == null) return "해당 단어가 존재하지 않습니다.";

        if (w.getKors().isEmpty()) return "삭제할 뜻이 없습니다.";

        if (index1Based < 1 || index1Based > w.getKors().size()) {
            return "유효하지 않은 번호입니다.";
        }

        String removed = w.getKors().remove(index1Based - 1);
        if (w.getKors().isEmpty()) {
            return "뜻 '" + removed + "' 삭제 완료! (남은 뜻 없음)";
        } else {
            return "뜻 '" + removed + "' 삭제 완료! 현재 뜻: " + w.getKors();
        }
    }

    // 4) 예문 수정/삭제
    public String updateExample(String eng, String newExample) {
        if (eng == null) eng = "";
        eng = eng.trim();
        Word w = vocabMap.get(eng);
        if (w == null) return "해당 단어가 존재하지 않습니다.";

        if (newExample == null || newExample.trim().isEmpty()) {
            exampleMap.remove(eng);
            return "예문이 삭제되었습니다.";
        } else {
            exampleMap.put(eng, newExample.trim());
            return "예문이 저장되었습니다.";
        }
    }


    //영어를 바꾸면 예문의 key도 같이 옮겨줘야 함.
    private void updateExampleKey(String oldEng, String newEng) {
        if (oldEng.equals(newEng))
            return;  //같으면 안바꾸고 종료

        String ex = exampleMap.remove(oldEng);
        if (ex != null) {
            exampleMap.put(newEng, ex);
        }
    }

    //*************** 이 아래는 단어 add 관련 메서드 ******************************

    // 옛날 콘솔용 단어 추가 기능 (Scanner 사용하는 버전)
    private void addVocab() {
        System.out.print("[추가] 추가할 영단어를 입력하세요: ");
        String eng = scan.nextLine();

        System.out.print("한글 뜻을 입력하세요 (여러 개면 '/' 로 구분): ");
        String korLine = scan.nextLine();

        System.out.print("예문을 입력하시겠습니까? (없으면 그냥 엔터) : ");
        String ex = scan.nextLine();

        String msg = addVocabCore(eng, korLine, ex);
        System.out.println(msg);
    }

    // CLI / GUI 모두 동작하는 버전
    public String addVocabCore(String eng, String korLine, String exampleOpt) {
        // null 도 처리 + trim
        eng        = java.util.Objects.toString(eng, "").trim();
        korLine    = java.util.Objects.toString(korLine, "").trim();
        exampleOpt = java.util.Objects.toString(exampleOpt, "").trim();

        if (eng.isEmpty() || !eng.matches("[a-zA-Z]+")) {
            return "유효하지 않은 영어 단어입니다. (영문자만 입력)";
        }
        if (korLine.isEmpty()) {
            return "뜻이 비어있습니다.";
        }

        // 1) 한글 뜻 파싱
        java.util.List<String> newKors =
                java.util.Arrays.stream(korLine.split("/"))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .distinct()
                        .toList();

        if (newKors.isEmpty()) {
            return "유효한 뜻이 없습니다.";
        }

        // 2) 기존 단어 있는지 확인
        Word existing = vocabMap.get(eng);

        if (existing != null) {
            int beforeSize = existing.getKors().size();

            // 새 뜻 중 기존에 없는 것만 추가 (stream 버전)
            newKors.stream()
                    .filter(k -> !existing.getKors().contains(k))
                    .forEach(k -> existing.getKors().add(k));

            int addedCount = existing.getKors().size() - beforeSize;

            if (addedCount == 0) {
                return "모든 뜻이 이미 존재합니다. 추가된 뜻이 없습니다.";
            }
            return "'" + eng + "' 에 새 뜻 " + addedCount + "개가 추가되었습니다.";

        } else {
            // 3) 새로운 단어 생성
            Word newWord = new Word(eng);
            newWord.getKors().addAll(newKors);

            voc.add(newWord);
            vocabMap.put(eng, newWord);

            if (!exampleOpt.isEmpty()) {
                exampleMap.put(eng, exampleOpt);
            }

            return "'" + eng + "' 단어가 새로 추가되었습니다.";
        }
    }




//    //추가 기능
//    //voc 와 vocabMap 업데이트로 수정 -- 강동훈
//    //1. 영단어가 이미 있을 시 --> 입력한 한글 뜻이 이미 있으면, 중복이라 추가 안하고 종료
//    //이어서 새로운 뜻이 하나라도 있으면, 그 새로운 뜻만 맨 뒤에 추가
//    //2. 영단어가 없으면, 새로운 Word 하나 만들어서, voc 와 vocabMap 에 전부 추가
//    private void addVocab() {
//        System.out.print("[추가] 추가할 영단어를 입력하세요: ");
//        String eng = scan.nextLine().trim();
//
//        // 영어 단어 유효성 검사 (한글이나 이상한게 껴있으면 reject)
//        if (eng.isEmpty() || !eng.matches("[a-zA-Z]+")) {
//            System.out.println("유효하지 않은 영어 단어입니다. (영문자만 입력)");
//            return;
//        }
//
//        System.out.print("한글 뜻을 입력하세요 (여러 개면 '/' 로 구분): ");
//        String korLine = scan.nextLine().trim();
//        if (korLine.isEmpty()) {
//            System.out.println("뜻이 비어있습니다.");
//            return;
//        }
//
//        // 한글 뜻 파싱
//        String[] parts = korLine.split("/");
//        ArrayList<String> newKors = new ArrayList<>();
//        for (String p : parts) {
//            String k = p.trim();
//            if (!k.isEmpty() && !newKors.contains(k)) { // 같은 줄 안에서 중복 제거
//                newKors.add(k);
//            }
//        }
//        if (newKors.isEmpty()) {
//            System.out.println("유효한 뜻이 없습니다.");
//            return;
//        }
//
//        // 이미 존재하는 영단어인지 검사
//        //이건 복사본이 아니라, 해당 Word 객체를 가리킴. (같은 주소값으로 참조)
//        Word existing = vocabMap.get(eng);
//
//        if (existing != null) {
//            // 1-1,1-2번 케이스: 기존 단어에 대해 중복 여부 확인
//            int addedCount = 0;
//            //newKors 는 내가 등록할 한글 뜻들. 이게 기존 단어에 포함되어있는지 체크
//            for (String k : newKors) {
//                if (!existing.getKors().contains(k)) {
//                    existing.getKors().add(k);  // 새 뜻만 뒤에 추가
//                    addedCount++;
//                }
//            }
//
//            if (addedCount == 0) {
//                // 1. 입력한 모든 뜻이 이미 존재
//                System.out.println("모든 뜻이 이미 존재합니다. 추가된 뜻이 없습니다.");
//            } else {
//                // 2. 일부 또는 전부 새 뜻이라서 추가됨
//                System.out.println("'" + eng + "' 에 새 뜻 " + addedCount + "개가 추가되었습니다.");
//                System.out.println("현재 뜻: " + existing.getKors());
//            }
//
//        } else {
//            // 3. 새로운 영단어
//            Word newWord = new Word(eng);
//            newWord.getKors().addAll(newKors);
//
//            voc.add(newWord);            // 리스트에 추가
//            vocabMap.put(eng, newWord);  // 맵에도 추가
//
//            // 예문 입력 추가
//            System.out.print("예문을 입력하시겠습니까? (없으면 그냥 엔터) : ");
//            String ex = scan.nextLine().trim();
//            if (!ex.isEmpty()) {
//                exampleMap.put(eng, ex);
//                System.out.println("예문이 등록되었습니다.");
//            } else {
//                System.out.println("예문 없이 단어만 등록되었습니다.");
//            }
//            System.out.println("=".repeat(20));
//            System.out.println("'" + eng + "' 단어가 새로 추가되었습니다.");
//            System.out.println("뜻: " + newWord.getKors());
//            System.out.println("예문: "+exampleMap.get(newWord.getEng()));
//        }
//    }

}
