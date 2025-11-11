package Vocab;

import java.util.ArrayList;

public class Word {
    String eng;
    ArrayList<String> kors;

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

    @Override
    public String toString() {
        return eng + " : " + kors;
    }
}
