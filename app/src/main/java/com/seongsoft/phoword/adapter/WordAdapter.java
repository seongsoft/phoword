package com.seongsoft.phoword.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.roughike.bottombar.BottomBar;
import com.seongsoft.phoword.R;
import com.seongsoft.phoword.activity.AddWordToVocaActivity;
import com.seongsoft.phoword.activity.MainActivity;
import com.seongsoft.phoword.component.Vocabulary;
import com.seongsoft.phoword.component.WordSet;
import com.seongsoft.phoword.dialog.RemoveWordDialogFragment;
import com.seongsoft.phoword.dialog.RemoveWordInVocaDialogFragment;
import com.seongsoft.phoword.dialog.WordInfoDialogFragment;
import com.seongsoft.phoword.listener.MyOnActionItemFinishListener;
import com.seongsoft.phoword.manager.DatabaseManager;

import java.util.ArrayList;
import java.util.List;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.ViewHolder> {

    public static final String EXTRA_SELECTED_WORDS = "selected_wordsets";
    public static final String TAG_REMOVE = "dialog_remove";
    public static final String TAG_EDIT = "dialog_edit";
    public static final int REQUEST_REMOVE = 0;
    public static final int REQUEST_EDIT = 1;
    public static final int REQUEST_ADD_TO_VOCA = 2;
    public static final int TYPE_WORD = 0;
    public static final int TYPE_WORD_IN_VOCA = 1;

    private Context mContext;
    private DatabaseManager mDBManager;

    public List<WordSet> mWordSets;
    private Vocabulary mVocabulary;

    private List<CheckBox> mCheckBoxes;
    private List<ImageButton> mFavoriteButtons;
    public ActionMode mActionMode;
    private CheckBox mChoiceModeCheckBox;
    private Drawable mStarImage;
    private Drawable mSelectedStarImage;

    private int mType;
    private int mCount;

    private boolean isChoiceMode;

    public WordAdapter(Context context, ArrayList<WordSet> wordSets, int type) {
        mContext = context;

        if (wordSets == null) mWordSets = new ArrayList<>();
        else mWordSets = wordSets;

        mType = type;

        mCheckBoxes = new ArrayList<>();
        mFavoriteButtons = new ArrayList<>();

        mDBManager = new DatabaseManager(mContext);

        int colorAccent = ContextCompat.getColor(mContext, R.color.colorAccent);

        mStarImage = ContextCompat.getDrawable(mContext, R.drawable.ic_star_border);
        mStarImage.setColorFilter(colorAccent, PorterDuff.Mode.MULTIPLY);

        mSelectedStarImage = ContextCompat.getDrawable(mContext, R.drawable.ic_star_small);
        mSelectedStarImage.setColorFilter(colorAccent, PorterDuff.Mode.MULTIPLY);
    }

    public WordAdapter(Context context, ArrayList<WordSet> wordSets, Vocabulary vocabulary,
                       int type) {
        this(context, wordSets, type);
        mVocabulary = vocabulary;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_word, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final WordSet wordSet = mWordSets.get(position);
        final ImageButton hFavoriteIB = holder.mFavoriteIB;
        final CheckBox hCheckBox = holder.mCheckBox;
        final RelativeLayout hClickLayout = holder.mClickLayout;

        mCheckBoxes.add(hCheckBox);
        mFavoriteButtons.add(hFavoriteIB);

        if (wordSet.isFavorite())
            hFavoriteIB.setImageDrawable(mStarImage);

        hFavoriteIB.setColorFilter(ContextCompat.getColor(mContext, R.color.favorites), PorterDuff.Mode.MULTIPLY);
        hFavoriteIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wordSet.isFavorite()) {
                    hFavoriteIB.setImageDrawable(mStarImage);
                    wordSet.setFavorite(false);
                    mDBManager.setFavorite(wordSet.getWord(), false);
                    Toast.makeText(mContext, "'" + wordSet.getWord() + "'가 선호단어에서 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    hFavoriteIB.setImageDrawable(mSelectedStarImage);
                    wordSet.setFavorite(true);
                    mDBManager.setFavorite(wordSet.getWord(), true);
                    Toast.makeText(mContext, "'" + wordSet.getWord() + "'가 선호단어로 등록되었습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.mWordTV.setText(wordSet.getWord());
        if (wordSet.getMeaning().size() > 1) {
            holder.mMeaningTV.setText(wordSet.getMeaning().get(0) + ", "
                    + wordSet.getMeaning().get(1));
        } else {
            holder.mMeaningTV.setText(wordSet.getMeaning().get(0));
        }

        hCheckBox.setChecked(wordSet.isSelected());
        if (wordSet.isFavorite())
            hFavoriteIB.setImageDrawable(mSelectedStarImage);
        else
            hFavoriteIB.setImageDrawable(mStarImage);

        if (isChoiceMode)
            hCheckBox.setVisibility(View.VISIBLE);

        hClickLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!isChoiceMode) {
                    mActionMode = ((AppCompatActivity) mContext).startSupportActionMode(
                            new ActionBarCallBack());
                    View customView = LayoutInflater.from(mContext)
                            .inflate(R.layout.action_word, null);

                    mChoiceModeCheckBox = (CheckBox) customView.findViewById(R.id.action_check);
                    mChoiceModeCheckBox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mChoiceModeCheckBox.isChecked()) {
                                selectAllWords();
                                mCount = mWordSets.size();
                            } else {
                                deselectAllWords();
                                mCount = 0;
                            }
                            mChoiceModeCheckBox.setText(mCount + " selected");
                        }
                    });

                    if (mActionMode != null) mActionMode.setCustomView(customView);

                    TranslateAnimation animate = new TranslateAnimation(0, hCheckBox.getWidth(), 0, 0);
                    animate.setDuration(50);
                    animate.setFillAfter(true);

                    /*   모든 아이템의 체크박스 활성화   */
                    //checkMode(animate);

                    for (CheckBox checkBox : mCheckBoxes)
                        checkBox.setVisibility(View.VISIBLE);

                    for (ImageButton favoriteButton : mFavoriteButtons)
                        favoriteButton.setVisibility(View.GONE);

                    isChoiceMode = true;
                    hClickLayout.setPressed(false);

                    hCheckBox.setChecked(true);
                    wordSet.setSelected(true);

                    mCount++;
                    mChoiceModeCheckBox.setText(mCount + " selected");

                    BottomBar bottomBar = ((MainActivity) mContext).mBottomBar;
                    for (int index = 0; index < bottomBar.getTabCount(); index++) {
                        bottomBar.getTabAtPosition(index).setEnabled(false);
                        bottomBar.setAlpha(0.2f);
                    }
                }

                return false;
            }
        });

        hClickLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isChoiceMode) {
                    if (wordSet.isSelected())
                        mCount--;
                    else
                        mCount++;
                    mChoiceModeCheckBox.setText(mCount + " selected");

                    hCheckBox.setChecked(!hCheckBox.isChecked());
                    wordSet.setSelected(hCheckBox.isChecked());
                } else {
                    WordInfoDialogFragment wordInfoDialog = WordInfoDialogFragment.newInstance(wordSet);
                    wordInfoDialog.show(((AppCompatActivity) mContext).getSupportFragmentManager(), null);
                }
            }
        });


    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return mWordSets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final RelativeLayout mClickLayout;
        public final CheckBox mCheckBox;
        public final TextView mWordTV;
        public final TextView mMeaningTV;
        public final ImageButton mFavoriteIB;

        public ViewHolder(View itemView) {
            super(itemView);

            mClickLayout = (RelativeLayout) itemView.findViewById(R.id.layout_word_click);
            mCheckBox = (CheckBox) itemView.findViewById(R.id.cb_word);
            mWordTV = (TextView) itemView.findViewById(R.id.tv_word);
            mMeaningTV = (TextView) itemView.findViewById(R.id.tv_meaning);
            mFavoriteIB = (ImageButton) itemView.findViewById(R.id.ib_favorite);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mWordTV.getText() + "'";
        }

    }

    public class ActionBarCallBack implements ActionMode.Callback, MyOnActionItemFinishListener,
            RemoveWordInVocaDialogFragment.RemoveWordInVocaDialogListener {

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            // TODO Auto-generated method stub
            int id = item.getItemId();

            if (id == R.id.action_remove) {
                if (mType == TYPE_WORD) {
                    RemoveWordDialogFragment.newInstance(getSelectedWordSets().size(), this)
                            .show(((AppCompatActivity) mContext).getSupportFragmentManager(),
                                    TAG_REMOVE);
                } else if (mType == TYPE_WORD_IN_VOCA) {
                    RemoveWordInVocaDialogFragment.newInstance(getSelectedWordSets().size(),
                            mVocabulary, this)
                            .show(((AppCompatActivity) mContext).getSupportFragmentManager(),
                                    TAG_REMOVE);
                }
                return true;
            } else if (id == R.id.action_add_to_voca) {
                Intent intent = new Intent(mContext, AddWordToVocaActivity.class);

                intent.putParcelableArrayListExtra(EXTRA_SELECTED_WORDS, getSelectedWordSets());
                ((AppCompatActivity) mContext).startActivityForResult(intent,
                        MainActivity.REQUEST_ADD_WORD_TO_VOCA);

                return true;
            }

            return false;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_word_contextual, menu);
            if (mType == TYPE_WORD_IN_VOCA) {
                menu.findItem(R.id.action_add_to_voca).setVisible(false);
            }

            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            quitChoiceMode();
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        /*   편집 아이템 선택 시   */
        @Override
        public void onActionItemFinish(int requestCode, int resultCode, WordSet wordSet) {
            if (resultCode == RESULT_OK) {
                if (requestCode == REQUEST_EDIT) {
                    editWord(wordSet);
                    quitChoiceMode();
                    Toast.makeText(mContext, "'" + wordSet.getWord() + "'이(가) 수정되었습니다.",
                            Toast.LENGTH_SHORT)
                            .show();
                    mActionMode.finish();
                }
            }
        }

        /*   단어장에 추가 아이템 선택 시   */
        @Override
        public void onActionItemFinish(int requestCode, int resultCode, Vocabulary vocabulary) {
            if (resultCode == RESULT_OK) {
                if (requestCode == REQUEST_ADD_TO_VOCA) {
                    for (int index = 0; index < getSelectedWordSets().size(); index++) {
                        mDBManager.insertVoca(vocabulary, getSelectedWordSets().get(index));
                    }
                    Toast.makeText(mContext,
                            getSelectedWordSets().size() + "개의 단어가 '"
                                    + vocabulary.getName() + "'에 추가되었습니다.",
                            Toast.LENGTH_SHORT)
                            .show();
                    quitChoiceMode();
                    mActionMode.finish();
                }
            }
        }

        /*   삭제 아이템 선택 시   */
        @Override
        public void onActionItemFinish(int requestCode, int resultCode) {
            if (resultCode == RESULT_OK) {
                if (requestCode == REQUEST_REMOVE) {
                    removeWords();
                    quitChoiceMode();
                    Toast.makeText(mContext,
                            getSelectedWordSets().size() + "개의 단어가 삭제되었습니다.",
                            Toast.LENGTH_SHORT)
                            .show();
                    mActionMode.finish();
                }
            }
        }

        // 단어장 안에서 삭제 아이템 선택 시
        @Override
        public void onRemove() {
            removeWordsInVoca();
            quitChoiceMode();
            Toast.makeText(mContext,
                    getSelectedWordSets().size() + "개의 단어가 삭제되었습니다.",
                    Toast.LENGTH_SHORT)
                    .show();
            mActionMode.finish();
        }

    }

    public void quitChoiceMode() {
        if (isChoiceMode) {
            for (CheckBox checkBox : mCheckBoxes) {
                checkBox.setVisibility(View.GONE);
                checkBox.setChecked(false);
            }
            for (WordSet wordSet : mWordSets) {
                wordSet.setSelected(false);
            }

//            if (mContext instanceof WordInListActivity) {
//                for (ImageButton favoriteButton : mFavoriteButtons)
//                    favoriteButton.setVisibility(View.INVISIBLE);
//            } else {
//                for (ImageButton favoriteButton : mFavoriteButtons)
//                    favoriteButton.setVisibility(View.VISIBLE);
//            }
            for (ImageButton favoriteButton : mFavoriteButtons)
                favoriteButton.setVisibility(View.VISIBLE);

            BottomBar bottomBar = ((MainActivity) mContext).mBottomBar;
            for (int index = 0; index < bottomBar.getTabCount(); index++) {
                bottomBar.getTabAtPosition(index).setEnabled(true);
                bottomBar.setAlpha(1.0f);
            }

            isChoiceMode = false;
            mCount = 0;
        }
    }

    private int getSelectedPosition() {
        for (int index = 0; index < mWordSets.size(); index++) {
            if (mWordSets.get(index).isSelected())
                return index;
        }

        return -1;
    }

    private ArrayList<WordSet> getSelectedWordSets() {
        ArrayList<WordSet> wordSets = new ArrayList<>();
        for (WordSet wordSet : mWordSets) {
            if (wordSet.isSelected())
                wordSets.add(wordSet);
        }

        return wordSets;
    }

    private void selectAllWords() {
        for (WordSet wordSet : mWordSets)
            wordSet.setSelected(true);
        for (CheckBox checkBox : mCheckBoxes)
            checkBox.setChecked(true);
    }

    private void deselectAllWords() {
        for (WordSet wordSet : mWordSets)
            wordSet.setSelected(false);
        for (CheckBox checkBox : mCheckBoxes)
            checkBox.setChecked(false);
    }

    private void removeWords() {
        for (int index = 0; index < mWordSets.size(); ) {
            WordSet wordSet = mWordSets.get(index);

            if (wordSet.isSelected()) {
//                if (mContext instanceof WordInListActivity) {
//                    if (((WordInListActivity) mContext).getViewType() == VocabularyAdapter.TYPE_HEADER) {
//                        mDBManager.setFavorite(wordSet, false);
//
//                        int position = mWordRecyclerViewAdapter.search(wordSet);
//                        mWordRecyclerViewAdapter.mWordSets.get(position).setFavorite(false);
//                        mWordRecyclerViewAdapter.notifyDataSetChanged();
//                    } else
//                        mDBManager.removeWordInList(((WordInListActivity) mContext).getWordList(), wordSet);
//                } else {
                mDBManager.removeWord(wordSet);
//                }
                mWordSets.remove(index);
                mCheckBoxes.remove(index);
                notifyItemRemoved(index);
                notifyItemRangeChanged(index, mWordSets.size());
            } else {
                index++;
            }
        }
    }

    private void editWord(WordSet wordSet) {
        int position = getSelectedPosition();
        mDBManager.editWord(mWordSets.get(getSelectedPosition()), wordSet);
        mWordSets.set(position, wordSet);
        notifyDataSetChanged();
    }

    private void removeWordsInVoca() {
        for (int index = 0; index < mWordSets.size(); ) {
            WordSet wordSet = mWordSets.get(index);

            if (wordSet.isSelected()) {
                mDBManager.removeWordInVoca(mVocabulary, wordSet);
                mWordSets.remove(index);
                mCheckBoxes.remove(index);
                notifyItemRemoved(index);
                notifyItemRangeChanged(index, mWordSets.size());
            } else {
                index++;
            }
        }
    }

    /*
        private void checkMode(TranslateAnimation animation) {
            for (LinearLayout itemLayout : mLayouts) {
                //itemLayout.startAnimation(animation);
                itemLayout.setPadding(100, 0, 0, 0);
                //itemLayout.setHasTransientState(true);

            }
        }
    */

    public void add(WordSet wordSet) {
        if (wordSet != null) {
            mWordSets.add(wordSet);
            notifyDataSetChanged();
        }
    }

    public void addAll(List<WordSet> wordSets) {
        if (wordSets != null) {
            mWordSets.addAll(wordSets);
            notifyDataSetChanged();
        }
    }

    public int search(WordSet wordSet) {
        for (int index = 0; index < mWordSets.size(); index++) {
            if (wordSet.getWord().equals(mWordSets.get(index).getWord()))
                return index;
        }

        return -1;
    }

    public List<WordSet> getWordSets() {
        return mWordSets;
    }

}