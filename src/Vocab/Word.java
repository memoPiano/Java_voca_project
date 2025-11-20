package Vocab;

import java.util.ArrayList;

public class Word {
    String eng;  //영어 뜻
    ArrayList<String> kors;  //한국어 뜻
    int wrong_number;  //틀린 횟수
    boolean bookMark;  //즐겨찾기 여부

    public Word(String eng) {
        this.eng = eng;
        this.kors=new ArrayList<>();
    }

    //getter 및 setter
    public String getEng() {
        return eng;
    }
    public void setEng(String eng) {
        this.eng = eng;
    }
    public ArrayList<String> getKors() {
        return kors;
    }
    public void setKors(ArrayList<String> kors) {
        this.kors = kors;
    }
    public int getWrong_number() {return wrong_number;}
    public void setWrong_number(int wrong_number) {this.wrong_number = wrong_number;}
    public void increaseWrong() {
        wrong_number++;
    }
    public boolean isBookMark() {return bookMark;}
    public void setBookMark(boolean bookMark) {this.bookMark = bookMark;}

    // 예문을 저장하는 리스트
// 한 단어에 여러 개의 예문을 추가할 수 있기 때문에 ArrayList 사용
    private ArrayList<String> examples = new ArrayList<>();

    // 예문 리스트를 반환하는 메소드
// 다른 클래스에서 단어의 예문들을 확인할 때 사용
    public ArrayList<String> getExamples() {
        return examples;
    }

    // 새로운 예문을 추가하는 메소드
// 단어 학습 시 예문을 계속 늘릴 수 있도록 설계됨
    public void addExample(String example) {
        examples.add(example);
    }

    @Override
    public String toString() {
        return eng + " : " + kors;
    }
}
