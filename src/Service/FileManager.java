package Service;

import Vocab.Word;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;
import java.io.FileWriter;

public class FileManager {
    private String filePath;

    public FileManager() {
    }

    //파일 이름으로 객체 생성자
    public FileManager(String filePath) {
        this.filePath = filePath;
    }

    //파일 --> 리스트 만들어서 반환
    public ArrayList<Word> loadFromFile(){
        ArrayList<Word> list=new ArrayList<>(); //리턴용 단어 리스트
        try(Scanner file=new Scanner(new File(filePath))){
            while (file.hasNext()){
                String str=file.nextLine().trim();  //한 줄 읽기
                //우리 파일 형식은 "eng \t kor1/ kor2/ kor3...
                if(str.isEmpty()){
                    continue; //만약, 빈줄이 있으면 넘김
                }

                String[] temp=str.split("\\t",4); //두조각만 생성(한번만 쪼갬) -> 세조각 생성(두번 쪼갬) -> 최종 4조각 생성(3번 쪼갬)
                if(temp.length<3){
                    continue;  //형식이 깨졌으면 건너뛴다
                }

                String eng=temp[0].trim();
                String korLine=temp[1].trim();
                String wrong_number = temp[2].trim();
                String book_mark= temp[3].trim();
                //한글 뜻 파싱
                // 단어 추가: 한글 뜻 여러 개를 한 번에 처리 (korLine: "뜻1/ 뜻2/ 뜻3")
                Word w = new Word(eng); //일단 eng 가지고 Word 객체 생성
                StringTokenizer st=new StringTokenizer(korLine,"/");
                while (st.hasMoreTokens()){
                    String kor=st.nextToken().trim(); //토큰들로부터 한글 하나씩 읽고
                    //한글 뜻 중복 방지. 단어의 kors 리스트에 없을때만 넣는다
                    if(!w.getKors().contains(kor)){
                        w.getKors().add(kor);
                    }
                }
                try {
                    w.setWrong_number(Integer.parseInt(wrong_number));  //틀린 횟수 넣기
                    w.setBookMark(Boolean.parseBoolean(book_mark));  // 즐겨찾기 했는 지 안했는 지
                    list.add(w);  //한 줄에서 만든 단어 추가
                } catch (NumberFormatException e){
                    System.out.println("파일 형식 오류로 건너뜀"+str);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("기존 데이터 파일이 없습니다.새로 시작합니다.");
            return new ArrayList<>();
        }
        return list; //만들어진 단어들 리스트 반환
    }


    //만들어진 리스트 --> 파일 저장 (덮어쓰기 방식)
    public void saveToFile(List<Word> data){
        //기존 파일이 있으면 내용 전체를 지우고 새로 씀 (PrintWriter 클래스의 기능)
        try(PrintWriter output=new PrintWriter(filePath)){
            for (Word w : data) {
                output.print(w.getEng());
                output.print("\t"); //탭으로 하나 띄고

                for(int i=0;i<w.getKors().size();i++){
                    //첫 단어 출력 후 구분자 추가하기 위함
                    if(i>0){
                        output.print("/ ");
                    }
                    output.print(w.getKors().get(i)); //한글 뜻은 리스트니까 차례대로 출력
                }
                output.print("\t"); //탭으로 하나 띄고
                output.print(w.getWrong_number()); //틀린 횟수 가져오기
                output.print("\t"); //탭으로 하나 띄고
                output.println(w.isBookMark()); //즐겨찾기 여부 가져오기
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    
    // 예문 파일을 불러서 HashMap으로 반환하는 메소드
    // 파일 형식 예:  apple/This is an apple.
    public HashMap<String, ArrayList<String>> loadExamples(String filePath) {

        // HashMap 생성: key = 영어 단어, value = 예문 리스트
        HashMap<String, ArrayList<String>> exampleMap = new HashMap<>();

        try (Scanner sc = new Scanner(new File(filePath))) {

            // 파일에서 줄 단위로 읽기
            while (sc.hasNextLine()) {
                String line = sc.nextLine();

                // "/" 기준으로 영어단어와 예문을 분리
                // 형식이 잘못한다면(parts 길이가 2가 아니면) 무시
                String[] parts = line.split("/");
                if (parts.length != 2) continue;

                String eng = parts[0];     // 영어 단어
                String example = parts[1]; // 예문

                // 해당 영어 단어가 처음 등장하면 새로운 리스트 생성
                exampleMap.putIfAbsent(eng, new ArrayList<>());

                // 예문 리스트에 예문 추가
                exampleMap.get(eng).add(example);
            }

        } catch (Exception e) {
            // 파일을 읽지 못한다면 오류 메시지 출력
            System.out.println("예문 파일을 불을 수 없습니다: " + e.getMessage());
        }

        // 완성된 <영어단어 - 예문 리스트> 맵 반환
        return exampleMap;
    }
    // 예문을 파일에 추가로 저장하는 메소드
// filePath : 예문 파일 경로
// eng      : 영어 단어
// example  : 해당 단어의 예문
// 파일에 새로운 예문을 한 줄씩 추가(append)하는 방식으로 동작함
    public void appendExample(String filePath, String eng, String example) {

        // FileWriter의 두 번째 인자 true → 기존 내용을 덮어쓰지 않고 뒤에 이어서 작성(append) 모드
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath, true))) {

            // 파일에 "영어단어/예문" 형식으로 한 줄 추가
            pw.println(eng + "/" + example);

        } catch (Exception e) {

            // 파일 저장 중 오류가 발생했을 때 메시지 출력
            System.out.println("예문 저장 실패: " + e.getMessage());
        }
    }
}//클래스의 끝
