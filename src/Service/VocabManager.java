package Service;

import Vocab.Word;
import Exception.MenuRangeCheckException;

import java.util.*;

public class VocabManager extends FileManager {
    String userName;
    ArrayList<Word> voc = new ArrayList<>();  //여기다 주로 저장
    HashMap<String, Word> vocabMap = new HashMap<>();  //이건 검색용이라 저장에는 포함되지 않을 예정.

    private static final Scanner scan = new Scanner(System.in); //사용자로부터 입력받을 스캐너

    public VocabManager(String userName) {
        this.userName = userName;
    }   //생성자

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
        
         // 예문 파일(example_sentences.txt)을 불러서 HashMap으로 가져옴
        HashMap<String, ArrayList<String>> exMap = loadExamples("example_sentences.txt");

        // 모든 단어 객체(voc 리스트)를 순회하면서
        for (Word w : voc) {

            // 해당 단어의 영어 단어가 예문 맵에 존재하면
            if (exMap.containsKey(w.getEng())) {

                // 그 영어 단어에 저장된 모든 예문을 Word 객체에 추가
                for (String ex : exMap.get(w.getEng())) {
                    w.addExample(ex);
                }
            }
        }
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
            System.out.println(("9) 예문 추가" ));
            System.out.println("10) 종료");
            System.out.print("메뉴 선택: ");

            try {
                choice = scan.nextInt();
                scan.nextLine();
                System.out.println();

                // 1~9만 허용함
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
                    case 8 -> showRandomWord();
                    case 9 -> addExampleSentence();
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

    private void showRandomWord() {
        if (voc.isEmpty()) {
            System.out.println("단어장이 비어있습니다.");
            return;
        }

        Random rand = new Random();
        int idx = rand.nextInt(voc.size());
        Word w = voc.get(idx);

        System.out.println("\n====== 오늘의 랜덤 단어 ======");
        System.out.println("영어: " + w.getEng());
        System.out.println("뜻  : " + w.getKors());
        System.out.println("===============================");
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
        }

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
        }
        System.out.println("시험이 종료되었습니다 수고하셨습니다");
        if (exam_pass_array.isEmpty())
            System.out.println("맞춘 단어가 없습니다");
        else {
            System.out.println("---맞춘 단어---");
            for (Word word : exam_pass_array) {
                System.out.println(word);
            }
        }
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
        selectionSort(sorted);

        System.out.println("\n------ 전체 오답 단어 목록 (내림차순) -------");

        boolean any = false;
        for (Word w : sorted) {
            if (w.getWrong_number() == 0)
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
        }

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
        }
        int wrong_time = 0;
        Word quiz_word = quiz_array.get((int) (Math.random() * quiz_array.size()));
        ArrayList<String> quiz_word_kors = quiz_word.getKors();
        System.out.println("주어진 한글: " + quiz_word_kors.get((int) (Math.random() * quiz_word_kors.size())));
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

        // 최대 5문제까지 출제, 만약 5개 보다 적으면 일찍 끝냄
        int numQuestions = Math.min(5, pool.size());

        int localCorrect = 0; // 이번 하드 모드에서 맞힌 개수
        int localWrong = 0;   // 이번 하드 모드에서 틀린 개수

        for (int q = 0; q < numQuestions; q++) {
            Word answer = pool.get(q); // 이번 문제의 정답 단어

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
            }
        }
    }

    //한글로 검색해서 찾기
    private List<Word> findKorSubString(String q) {
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
    private List<Word> findEngSubString(String q) {
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

            System.out.println("'" + eng + "' 단어 삭제 완료!");
        } else {
            System.out.println("삭제 취소!");
        }
    }

    //수정 기능
    private void editVocab() {
        if (voc.isEmpty()) {
            System.out.println("단어장이 비어있습니다.");
            return;
        }
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

        int choice = -1;

        while (true) {
            System.out.print("선택: ");
            try {
                choice = Integer.parseInt(scan.nextLine());
                if (choice >= 1 && choice <= 4) {
                    break;
                } else {
                    System.out.println("1~4 사이의 메뉴를 선택해주세요.");
                }
            } catch (NumberFormatException e) {
                System.out.println("숫자를 입력해주세요.");
            }
        }

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
    //voc 와 vocabMap 업데이트로 수정 -- 강동훈
    //1. 영단어가 이미 있을 시 --> 입력한 한글 뜻이 이미 있으면, 중복이라 추가 안하고 종료
    //이어서 새로운 뜻이 하나라도 있으면, 그 새로운 뜻만 맨 뒤에 추가
    //2. 영단어가 없으면, 새로운 Word 하나 만들어서, voc 와 vocabMap 에 전부 추가
    private void addVocab() {
        System.out.print("[추가] 추가할 영단어를 입력하세요: ");
        String eng = scan.nextLine().trim();

        // 영어 단어 유효성 검사 (한글이나 이상한게 껴있으면 reject)
        if (eng.isEmpty() || !eng.matches("[a-zA-Z]+")) {
            System.out.println("유효하지 않은 영어 단어입니다. (영문자만 입력)");
            return;
        }

        System.out.print("한글 뜻을 입력하세요 (여러 개면 '/' 로 구분): ");
        String korLine = scan.nextLine().trim();
        if (korLine.isEmpty()) {
            System.out.println("뜻이 비어있습니다.");
            return;
        }

        // 한글 뜻 파싱
        String[] parts = korLine.split("/");
        ArrayList<String> newKors = new ArrayList<>();
        for (String p : parts) {
            String k = p.trim();
            if (!k.isEmpty() && !newKors.contains(k)) { // 같은 줄 안에서 중복 제거
                newKors.add(k);
            }
        }
        if (newKors.isEmpty()) {
            System.out.println("유효한 뜻이 없습니다.");
            return;
        }

        // 이미 존재하는 영단어인지 검사
        //이건 복사본이 아니라, 해당 Word 객체를 가리킴. (같은 주소값으로 참조)
        Word existing = vocabMap.get(eng);

        if (existing != null) {
            // 1-1,1-2번 케이스: 기존 단어에 대해 중복 여부 확인
            int addedCount = 0;
            //newKors 는 내가 등록할 한글 뜻들. 이게 기존 단어에 포함되어있는지 체크
            for (String k : newKors) {
                if (!existing.getKors().contains(k)) {
                    existing.getKors().add(k);  // 새 뜻만 뒤에 추가
                    addedCount++;
                }
            }

            if (addedCount == 0) {
                // 1. 입력한 모든 뜻이 이미 존재
                System.out.println("모든 뜻이 이미 존재합니다. 추가된 뜻이 없습니다.");
            } else {
                // 2. 일부 또는 전부 새 뜻이라서 추가됨
                System.out.println("'" + eng + "' 에 새 뜻 " + addedCount + "개가 추가되었습니다.");
                System.out.println("현재 뜻: " + existing.getKors());
            }

        } else {
            // 3. 새로운 영단어
            Word newWord = new Word(eng);
            newWord.getKors().addAll(newKors);

            voc.add(newWord);            // 리스트에 추가
            vocabMap.put(eng, newWord);  // 맵에도 추가

            System.out.println("'" + eng + "' 단어가 새로 추가되었습니다.");
            System.out.println("뜻: " + newWord.getKors());
        }
    }

    //예문추가메소드
    private void addExampleSentence() {
        System.out.print("예문을 추가할 영어 단어 입력: ");
        String eng = scan.nextLine().trim();

        Word w = vocabMap.get(eng);
        if (w == null) {
            System.out.println("해당 단어가 존재하지 않습니다.");
            return;
        }


        System.out.print("추가할 예문 입력: ");
        String example = scan.nextLine().trim();

        w.addExample(example);

        appendExample("example_sentences.txt", eng, example);

        System.out.println("예문이 성공적으로 추가되었습니다!");
    }


}
