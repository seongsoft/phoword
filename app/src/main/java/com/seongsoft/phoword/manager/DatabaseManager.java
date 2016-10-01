package com.seongsoft.phoword.manager;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.seongsoft.phoword.component.Vocabulary;
import com.seongsoft.phoword.component.WordSet;
import com.seongsoft.phoword.utility.JSONParser;

import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    public static final String DB_NAME = "words.db";
    public static final int DB_VERISION = 2;

    public static final String WORD_TABLE = "words";
    public static final String WORD = "word";
    public static final String MEANING = "meaning";
    public static final String PRONOUNCE = "pronounce";
    public static final String AUDIO = "audio";
    public static final String EXAMPLE = "example";
    public static final String EXAMPLE_MEANING = "example_meaning";
    public static final String QUIZ_COUNT = "quiz_count";
    public static final String CORRECT_COUNT = "correct_count";
    public static final String FAVORITE = "favorite";
    public static final String DATE = "date";

    public static final String VOCA_TABLE = "vocas";
    public static final String VOCA_NAME = "name";
    public static final String VOCA_COLOR = "color";
    public static final String VOCA_WORD = "voca_word";

    public static final String SETTING_TABLE = "settings";
    public static final String SETTING_SORT = "sort";

    private Context mContext;
    private DatabaseHelper mDBHelper;

    public DatabaseManager(Context context) {
        mContext = context;
        create();
    }

    private class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql = "CREATE TABLE " + WORD_TABLE + " ("
                    + WORD + " TEXT NOT NULL PRIMARY KEY, "
                    + MEANING + " TEXT NOT NULL, "
                    + PRONOUNCE + " TEXT, "
                    + AUDIO + " TEXT, "
                    + EXAMPLE + " TEXT, "
                    + EXAMPLE_MEANING + " TEXT, "
                    + QUIZ_COUNT + " INTEGER DEFAULT 0, "
                    + CORRECT_COUNT + " INTEGER DEFAULT 0, "
                    + FAVORITE + " INTEGER DEFAULT 0, "
                    + DATE + " INTEGER);";
            db.execSQL(sql);

            sql = "CREATE TABLE " + VOCA_TABLE + " ("
                    + VOCA_NAME + " TEXT NOT NULL, "
                    + VOCA_WORD + " TEXT NOT NULL, "
                    + VOCA_COLOR + " TEXT, "
                    + "PRIMARY KEY(" + VOCA_NAME + ", " + VOCA_WORD + "), "
                    + "FOREIGN KEY(" + VOCA_WORD + ") REFERENCES " + WORD_TABLE + "(" + WORD + "));";
            db.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            String sql = "DROP TABLE IF EXISTS " + WORD_TABLE + ";";
            db.execSQL(sql);

            sql = "DROP TABLE IF EXISTS " + VOCA_TABLE + ";";
            db.execSQL(sql);
        }

    }

    private void create() {
        mDBHelper = new DatabaseHelper(mContext, DB_NAME, null, DB_VERISION);
    }

    public void insertWord(String word, String meaning, String pronounce,
                           String audio, String example, String exampleMeaning, long date)
            throws SQLiteConstraintException {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        try {
            String sql = "INSERT INTO " + WORD_TABLE
                    + " (" + WORD + ", " + MEANING + ", " + PRONOUNCE + ", " + AUDIO + ", "
                    + EXAMPLE + ", " + EXAMPLE_MEANING + ", " + DATE + ") "
                    + "VALUES ('" + word + "', '" + meaning + "', '" + pronounce + "', '"
                    + audio + "', " + DatabaseUtils.sqlEscapeString(example) + ", "
                    + DatabaseUtils.sqlEscapeString(exampleMeaning) + ", " + date + ");";
            db.execSQL(sql);
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
        }
    }

    public void insertVoca(Vocabulary vocabulary, WordSet wordSet) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        try {
            String sql = "INSERT INTO " + VOCA_TABLE
                    + " (" + VOCA_NAME + ", " + VOCA_COLOR + ", " + VOCA_WORD + ") "
                    + "VALUES ('"
                    + vocabulary.getName() + "', '"
                    + vocabulary.getColor() + "', '"
                    + ((wordSet != null) ? wordSet.getWord() : 0) + "');";
            db.execSQL(sql);
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
        }
    }

    public void editWord(WordSet sWordSet, WordSet dWordSet) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        String sql = "UPDATE " + WORD_TABLE
                + " SET " + WORD + "='" + dWordSet.getWord() + "', "
                + MEANING + "='" + dWordSet.getMeaning() + "'"
                + " WHERE " + WORD + "='" + sWordSet.getWord() + "';";
        db.execSQL(sql);
    }

    public void editVoca(String listName, Vocabulary vocabulary) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        String sql = "UPDATE " + VOCA_TABLE
                + " SET " + VOCA_NAME + "='" + vocabulary.getName() + "' "
                + " WHERE " + VOCA_NAME + "='" + listName + "';";
        db.execSQL(sql);
    }

    public void removeWord(WordSet wordSet) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        String sql = "DELETE FROM " + WORD_TABLE
                + " WHERE " + WORD + "='" + wordSet.getWord() + "';";
        db.execSQL(sql);
    }

    public void removeVoca(Vocabulary vocabulary) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        String sql = "DELETE FROM " + VOCA_TABLE
                + " WHERE " + VOCA_NAME + "='" + vocabulary.getName() + "';";
        db.execSQL(sql);
    }

    public void removeWordInVoca(Vocabulary vocabulary, WordSet wordSet) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        String sql = "DELETE FROM " + VOCA_TABLE
                + " WHERE " + VOCA_NAME + "='" + vocabulary.getName() + "' AND "
                + VOCA_WORD + "='" + wordSet.getWord() + "';";
        db.execSQL(sql);
    }

    public WordSet selectWord(String word) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        String sql = "SELECT * FROM " + WORD_TABLE + " WHERE " + WORD + "='" + word + "';";
        Cursor cursor = db.rawQuery(sql, null);

        WordSet wordSet = null;

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String meaningJSON = cursor.getString(cursor.getColumnIndex(MEANING));
                List<String> meaning = JSONParser.parseFromJSON(meaningJSON, "뜻");
                String pronounce = cursor.getString(cursor.getColumnIndex(PRONOUNCE));
                String audio = cursor.getString(cursor.getColumnIndex(AUDIO));
                String exampleJSON = cursor.getString(cursor.getColumnIndex(EXAMPLE));
                List<String> example = JSONParser.parseFromJSON(exampleJSON, "예문");
                String exampleMeaningJSON = cursor.getString(cursor.getColumnIndex(EXAMPLE_MEANING));
                List<String> exampleMeaning = JSONParser.parseFromJSON(exampleMeaningJSON, "해석");
                int quizCount = cursor.getInt(cursor.getColumnIndex(QUIZ_COUNT));
                int correctCount = cursor.getInt(cursor.getColumnIndex(CORRECT_COUNT));
                int favorite = cursor.getInt(cursor.getColumnIndex(FAVORITE));
                long date = cursor.getLong(cursor.getColumnIndex(DATE));

                wordSet = new WordSet(word, (ArrayList<String>) meaning, pronounce, audio,
                        (ArrayList<String>) example, (ArrayList<String>) exampleMeaning,
                        quizCount, correctCount, favorite == 1, date);
            }
            cursor.close();
        }

        return wordSet;
    }

    public ArrayList<WordSet> selectWordsInVoca(Vocabulary vocabulary) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
//        String sql = "SELECT " + VOCA_WORD + ", " + VOCA_MEANING + " FROM " + VOCA_TABLE
//                + " WHERE " + VOCA_NAME + "='" + vocabulary.getName() + "';";
        String sql = "SELECT " + VOCA_WORD + " FROM " + VOCA_TABLE
                + " WHERE " + VOCA_NAME + "='" + vocabulary.getName() + "' " + "NOT NULL " + ";";
        Cursor cursor = db.rawQuery(sql, null);
        List<WordSet> wordSets = new ArrayList<>();

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String word = cursor.getString(cursor.getColumnIndex(VOCA_WORD));
                    if ("0".equals(word)) {
                        cursor.moveToNext();
                        continue;
                    }
                    WordSet wordSet = selectWord(word);
                    wordSets.add(wordSet);

                    cursor.moveToNext();
                }
            }
        }

        return (ArrayList<WordSet>) wordSets;
    }

    public ArrayList<WordSet> selectAllWords() {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        String sql = "SELECT * FROM " + WORD_TABLE + ";";
        Cursor cursor = db.rawQuery(sql, null);
        List<WordSet> wordSets = new ArrayList<>();

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    /*   DB에 저장된 모든 단어 정보를 불러옴   */
                    String word = cursor.getString(cursor.getColumnIndex(WORD));
                    String meaningJSON = cursor.getString(cursor.getColumnIndex(MEANING));
                    List<String> meaning = JSONParser.parseFromJSON(meaningJSON, "뜻");
                    String pronounce = cursor.getString(cursor.getColumnIndex(PRONOUNCE));
                    String audio = cursor.getString(cursor.getColumnIndex(AUDIO));
                    String exampleJSON = cursor.getString(cursor.getColumnIndex(EXAMPLE));
                    List<String> example = JSONParser.parseFromJSON(exampleJSON, "예문");
                    String exampleMeaningJSON = cursor.getString(cursor.getColumnIndex(EXAMPLE_MEANING));
                    List<String> exampleMeaning = JSONParser.parseFromJSON(exampleMeaningJSON, "해석");
                    int quizCount = cursor.getInt(cursor.getColumnIndex(QUIZ_COUNT));
                    int correctCount = cursor.getInt(cursor.getColumnIndex(CORRECT_COUNT));
                    int favorite = cursor.getInt(cursor.getColumnIndex(FAVORITE));
                    long date = cursor.getLong(cursor.getColumnIndex(DATE));

                    WordSet wordSet
                            = new WordSet(word, (ArrayList<String>) meaning, pronounce, audio,
                            (ArrayList<String>) example, (ArrayList<String>) exampleMeaning,
                            quizCount, correctCount, favorite == 1, date);

                    wordSets.add(wordSet);

                    /*   Cursor를 close 후 다음으로 옮김   */
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }

        return (ArrayList<WordSet>) wordSets;
    }

    public ArrayList<Vocabulary> selectAllVocas() {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        String sql = "SELECT * FROM " + VOCA_TABLE + ";";
        Cursor cursor = db.rawQuery(sql, null);
        List<Vocabulary> vocabularies = new ArrayList<>();

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    /*   DB에 저장된 모든 단어와 뜻을 불러옴   */
                    String name = cursor.getString(cursor.getColumnIndex(VOCA_NAME));
                    int color = cursor.getInt(cursor.getColumnIndex(VOCA_COLOR));
                    Vocabulary vocabulary = new Vocabulary(name, color);
                    String word = cursor.getString(cursor.getColumnIndex(VOCA_WORD));
                    if ("0".equals(word))
                        vocabularies.add(vocabulary);

                    /*   Cursor를 close 후 다음으로 옮김   */
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }

        return (ArrayList<Vocabulary>) vocabularies;
    }

    public void setFavorite(String word, boolean isFavorite) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        String sql = "UPDATE " + WORD_TABLE
                + " SET " + FAVORITE + "=" + (isFavorite ? 1 : 0)
                + " WHERE " + WORD + "='" + word + "';";
        db.execSQL(sql);
    }

    public ArrayList<WordSet> selectFavoriteWords() {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        String sql = "SELECT * FROM " + WORD_TABLE + " WHERE " + FAVORITE + "" +
                "=" + 1 + ";";
        Cursor cursor = db.rawQuery(sql, null);
        List<WordSet> wordSets = new ArrayList<>();

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    /*   DB에 저장된 모든 단어 정보를 불러옴   */
                    String word = cursor.getString(cursor.getColumnIndex(WORD));
                    String meaningJSON = cursor.getString(cursor.getColumnIndex(MEANING));
                    List<String> meaning = JSONParser.parseFromJSON(meaningJSON, "뜻");
                    String pronounce = cursor.getString(cursor.getColumnIndex(PRONOUNCE));
                    String audio = cursor.getString(cursor.getColumnIndex(AUDIO));
                    String exampleJSON = cursor.getString(cursor.getColumnIndex(EXAMPLE));
                    List<String> example = JSONParser.parseFromJSON(exampleJSON, "예문");
                    String exampleMeaningJSON = cursor.getString(cursor.getColumnIndex(EXAMPLE_MEANING));
                    List<String> exampleMeaning = JSONParser.parseFromJSON(exampleMeaningJSON, "해석");
                    int quizCount = cursor.getInt(cursor.getColumnIndex(QUIZ_COUNT));
                    int correctCount = cursor.getInt(cursor.getColumnIndex(CORRECT_COUNT));
                    int favorite = cursor.getInt(cursor.getColumnIndex(FAVORITE));
                    long date = cursor.getLong(cursor.getColumnIndex(DATE));

                    WordSet wordSet
                            = new WordSet(word, (ArrayList<String>) meaning, pronounce, audio,
                            (ArrayList<String>) example, (ArrayList<String>) exampleMeaning,
                            quizCount, correctCount, favorite == 1, date);

                    wordSets.add(wordSet);

                    /*   Cursor를 close 후 다음으로 옮김   */
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }

        return (ArrayList<WordSet>) wordSets;
    }

    public boolean isEmptyVoca(Vocabulary vocabulary) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        String sql = "SELECT " + VOCA_WORD + " FROM " + VOCA_TABLE
                + " WHERE " + VOCA_NAME + "='" + vocabulary.getName() + "' LIMIT 2;";
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    if (!cursor.getString(cursor.getColumnIndex(VOCA_WORD)).equals("0")) {
                        return false;
                    }
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
        return true;
    }

    public boolean vocaExists(String listName) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        String sql = "SELECT " + VOCA_NAME + " FROM " + VOCA_TABLE
                + " WHERE " + VOCA_NAME + "='" + listName + "';";
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor != null && cursor.getCount() > 0) return true;
        else return false;
    }

    public ArrayList<WordSet> selectWordsforQuiz(String vocabularyName, int limit) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        String sql = null;
        Cursor cursor = null;
        List<WordSet> wordSets = null;

        if (vocabularyName.equals("모든 단어")) {
            sql = "SELECT * FROM " + WORD_TABLE
                    + " ORDER BY " + QUIZ_COUNT + " ASC, " + CORRECT_COUNT + " DESC"
                    + " LIMIT " + limit + ";";
            cursor = db.rawQuery(sql, null);
            wordSets = new ArrayList<>();

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    while (!cursor.isAfterLast()) {
                    /*   DB에 저장된 모든 단어와 뜻을 불러옴   */
                        String word = cursor.getString(cursor.getColumnIndex(WORD));
                        String meaningJSON = cursor.getString(cursor.getColumnIndex(MEANING));
                        List<String> meaning = JSONParser.parseFromJSON(meaningJSON, "뜻");
                        String pronounce = cursor.getString(cursor.getColumnIndex(PRONOUNCE));
                        String audio = cursor.getString(cursor.getColumnIndex(AUDIO));
                        String exampleJSON = cursor.getString(cursor.getColumnIndex(EXAMPLE));
                        List<String> example = JSONParser.parseFromJSON(exampleJSON, "예문");
                        List<String> exampleMeaning = JSONParser.parseFromJSON(exampleJSON, "해석");
                        int quizCount = cursor.getInt(cursor.getColumnIndex(QUIZ_COUNT));
                        int correctCount = cursor.getInt(cursor.getColumnIndex(CORRECT_COUNT));
                        int favorite = cursor.getInt(cursor.getColumnIndex(FAVORITE));
                        long date = cursor.getLong(cursor.getColumnIndex(DATE));

                        WordSet wordSet
                                = new WordSet(word, (ArrayList<String>) meaning, pronounce, audio,
                                (ArrayList<String>) example, (ArrayList<String>) exampleMeaning,
                                quizCount, correctCount, favorite == 1, date);

                        wordSets.add(wordSet);

                    /*   Cursor를 close 후 다음으로 옮김   */
                        cursor.moveToNext();
                    }
                }
                cursor.close();
            }
        } else {
            sql = "SELECT " + VOCA_WORD + " FROM " + VOCA_TABLE
                    + " WHERE " + VOCA_NAME + "='" + vocabularyName
                    + "' ORDER BY " + QUIZ_COUNT + " ASC, " + CORRECT_COUNT + " DESC"
                    + " LIMIT " + limit + ";";
            cursor = db.rawQuery(sql, null);
            wordSets = new ArrayList<>();

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    while (!cursor.isAfterLast()) {
                        String word = cursor.getString(cursor.getColumnIndex(VOCA_WORD));
                        if ("0".equals(word)) {
                            cursor.moveToNext();
                            continue;
                        }

                        wordSets.add(selectWord(word));

                        cursor.moveToNext();
                    }
                }
                cursor.close();
            }
        }

        return (ArrayList<WordSet>) wordSets;
    }

    public void upCountQuizWord(String word) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        String sql = "UPDATE " + WORD_TABLE + " SET " + CORRECT_COUNT + " = " + CORRECT_COUNT + "+" + 1 + " WHERE " + WORD + " = " + "'" + word + "'";
        db.execSQL(sql);
    }

    public void downCountQuizWord(String word) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        String sql = "UPDATE " + WORD_TABLE + " SET " + CORRECT_COUNT + " = " + CORRECT_COUNT + " - " + 1 + " WHERE " + WORD + " = " + "'" + word + "'";
        db.execSQL(sql);
    }

    public void upQuizCount(String[] wordList) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        for (String word : wordList) {
            String sql = "UPDATE " + WORD_TABLE + " SET " + QUIZ_COUNT + " = " + QUIZ_COUNT + " + " + 1 + " WHERE " + WORD + " = " + "'" + word + "'";
            db.execSQL(sql);
        }
    }

    public int getCorrectCount() {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        String sql = "SELECT " + CORRECT_COUNT + " FROM " + WORD_TABLE + ";";
        Cursor cursor = db.rawQuery(sql, null);
        int sum = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    int quiz_count = cursor.getInt(cursor.getColumnIndex(CORRECT_COUNT));
                    sum += quiz_count;
                    cursor.moveToNext();
                }
            }
        }
        return sum;
    }

    public int getQuizCount() {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        String sql = "SELECT " + QUIZ_COUNT + " FROM " + WORD_TABLE + "WHERE " + QUIZ_COUNT + ">0;";
        Cursor cursor = db.rawQuery(sql, null);
        int sum = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    int quiz_count = cursor.getInt(cursor.getColumnIndex(QUIZ_COUNT));
                    sum += quiz_count;
                    cursor.moveToNext();
                }
            }
        }
        return sum;
    }


    public ArrayList<WordSet> selectNoSyncWord() {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        String sql = "SELECT * FROM " + WORD_TABLE + " WHERE " + EXAMPLE + " IS NULL;";
        Cursor cursor = db.rawQuery(sql, null);
        List<WordSet> wordSets = new ArrayList<>();

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String word = cursor.getString(cursor.getColumnIndex(WORD));
                    WordSet wordSet = selectWord(word);
                    wordSets.add(wordSet);

                    cursor.moveToNext();
                }
            }
        }
        return (ArrayList<WordSet>) wordSets;
    }

}
