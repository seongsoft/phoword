package com.seongsoft.phoword.component;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class WordSet implements Parcelable {

    private String word;
    private List<String> meaning;
    private String pronounce;
    private String audio;
    private List<String> example;
    private List<String> exampleMeaning;
    private int quizCount;
    private int correctCount;
    private boolean favorite;
    private long date;

    public static final Creator<WordSet> CREATOR = new Creator<WordSet>() {
        @Override
        public WordSet createFromParcel(Parcel source) {
            return new WordSet(source);
        }

        @Override
        public WordSet[] newArray(int size) {
            return new WordSet[size];
        }
    };

    public WordSet(Parcel source) {
        meaning = new ArrayList<>();
        example = new ArrayList<>();
        exampleMeaning = new ArrayList<>();

        word = source.readString();
        source.readStringList(meaning);
        pronounce = source.readString();
        audio = source.readString();
        source.readStringList(example);
        source.readStringList(exampleMeaning);
        source.readInt();
        source.readInt();
        favorite = source.readByte() != 0;
        date = source.readLong();
    }

    public WordSet(String word, ArrayList<String> meaning) {
        this.word = word;
        this.meaning = meaning;
    }

    public WordSet(String word, ArrayList<String> meaning, long date) {
        this(word, meaning);
        this.date = date;
    }

    public WordSet(String word, ArrayList<String> meaning, String pronounce, String audio,
                   ArrayList<String> example, ArrayList<String> exampleMeaning, long date) {
        this(word, meaning, date);
        this.pronounce = pronounce;
        this.audio = audio;
        this.example = example;
        this.exampleMeaning = exampleMeaning;
    }

    public WordSet(String word, ArrayList<String> meaning, String pronounce, String audio,
                   ArrayList<String> example, ArrayList<String> exampleMeaning, int quizCount,
                   int correctCount, boolean favorite, long date) {
        this(word, meaning, pronounce, audio, example, exampleMeaning, date);
        this.quizCount = quizCount;
        this.correctCount = correctCount;
        this.favorite = favorite;
    }

    public String getWord() {
        return word;
    }

    public ArrayList<String> getMeaning() {
        return (ArrayList<String>) meaning;
    }

    public String getPronounce() {
        return pronounce;
    }

    public String getAudio() {
        return audio;
    }

    public ArrayList<String> getExample() {
        return (ArrayList<String>) example;
    }

    public ArrayList<String> getExampleMeaning() {
        return (ArrayList<String>) exampleMeaning;
    }

    public int getQuizCount() {
        return quizCount;
    }

    public int getCorrectCount() {
        return correctCount;
    }

    public long getDate() {
        return date;
    }

    public void setFavorite(boolean isFavorite) {
        this.favorite = isFavorite;
    }

    public boolean isFavorite() {
        return favorite;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(word);
        dest.writeStringList(meaning);
        dest.writeString(pronounce);
        dest.writeString(audio);
        dest.writeStringList(example);
        dest.writeStringList(exampleMeaning);
        dest.writeInt(quizCount);
        dest.writeInt(correctCount);
        dest.writeByte((byte) (favorite ? 1 : 0));
        dest.writeLong(date);
    }

    public static Comparator<WordSet> Comparator = new Comparator<WordSet>() {
        @Override
        public int compare(WordSet lhs, WordSet rhs) {
            int result = lhs.getQuizCount() - rhs.getQuizCount();
            if (result == 0) return rhs.getCorrectCount() - lhs.getCorrectCount();
            return result;
        }
    };

}
