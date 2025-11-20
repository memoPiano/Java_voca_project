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

    @Override
    public String toString() {
        return eng + " : " + kors;
    }
}
