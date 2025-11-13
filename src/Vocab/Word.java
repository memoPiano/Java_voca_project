package Vocab;

import java.util.ArrayList;

public class Word {
    String eng;
    ArrayList<String> kors;
    int wrong_number;

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

    @Override
    public String toString() {
        return eng + " : " + kors;
    }
}
