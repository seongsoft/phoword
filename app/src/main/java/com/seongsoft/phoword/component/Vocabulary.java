package com.seongsoft.phoword.component;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;

import com.seongsoft.phoword.R;

import java.util.Random;

public class Vocabulary implements Parcelable {

    private int[] colors;

    private String name;
    private int color;
    private boolean selected;

    private Random rand;

    public static final Creator<Vocabulary> CREATOR = new Creator<Vocabulary>() {
        @Override
        public Vocabulary createFromParcel(Parcel source) {
            return new Vocabulary(source);
        }

        @Override
        public Vocabulary[] newArray(int size) {
            return new Vocabulary[size];
        }
    };

    public Vocabulary(Parcel source) {
        name = source.readString();
    }

    public Vocabulary(Context context, String name) {
        this.name = name;
        initColors(context);
        rand = new Random(System.currentTimeMillis());
        color = colors[rand.nextInt(colors.length)];
    }

    public Vocabulary(String name) {
        this.name = name;
    }

    public Vocabulary(String name, int color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    private void initColors(Context context) {
        colors = new int[10];
        colors[0] = ContextCompat.getColor(context, R.color.voca1);
        colors[1] = ContextCompat.getColor(context, R.color.voca2);
        colors[2] = ContextCompat.getColor(context, R.color.voca3);
        colors[3] = ContextCompat.getColor(context, R.color.voca5);
        colors[4] = ContextCompat.getColor(context, R.color.voca6);
        colors[5] = ContextCompat.getColor(context, R.color.voca7);
        colors[6] = ContextCompat.getColor(context, R.color.voca8);
        colors[7] = ContextCompat.getColor(context, R.color.voca9);
        colors[8] = ContextCompat.getColor(context, R.color.voca10);
        colors[9] = ContextCompat.getColor(context, R.color.voca11);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
    }

}
