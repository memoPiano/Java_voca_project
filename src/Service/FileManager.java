package Service;

import Vocab.Word;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

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

                String[] temp=str.split("\\t",3); //두조각만 생성(한번만 쪼갬) -> 세조각 생성(두번 쪼갬)
                if(temp.length<3){
                    continue;  //형식이 깨졌으면 건너뛴다
                }

                String eng=temp[0].trim();
                String korLine=temp[1].trim();
                String wrong_number = temp[2].trim();
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
                w.setWrong_number(Integer.parseInt(wrong_number));  //틀린 횟수 넣기
                list.add(w);  //한 줄에서 만든 단어 추가
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
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
                output.println(w.getWrong_number()); //틀린 횟수 가져오기
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}//클래스의 끝
