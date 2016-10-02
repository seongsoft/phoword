package com.seongsoft.phoword.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import com.roughike.bottombar.BottomBar;
import com.seongsoft.phoword.activity.MainActivity;
import com.seongsoft.phoword.manager.DatabaseManager;
import com.seongsoft.phoword.dialog.AddVocabularyDialogFragment;
import com.seongsoft.phoword.dialog.RemoveVocaDialogFragment;
import com.seongsoft.phoword.fragment.WordInVocaFragment;
import com.seongsoft.phoword.R;
import com.seongsoft.phoword.component.Vocabulary;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BeINone on 2016-09-21.
 */

public class VocabularyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements AddVocabularyDialogFragment.OnVocabularyAddedListener {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ITEM = 1;
    public static final int TYPE_FOOTER = 2;

    private Context mContext;
    private DatabaseManager mDBManager;

    private List<Vocabulary> mVocabularies;
    private List<CheckBox> mCheckBoxes;

    private ActionMode mActionMode;

    private boolean isChoiceMode;

    public VocabularyAdapter(Context context) {
        mContext = context;
        mDBManager = new DatabaseManager(mContext);
        mVocabularies = new ArrayList<>();
        mCheckBoxes = new ArrayList<>();

        add(new Vocabulary(""));    // 단어장 추가 버튼
        add(new Vocabulary(mContext.getString(R.string.favorites), ContextCompat.getColor(mContext, R.color.favorites)));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER || viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_vocabulary, parent, false);
            return new VocabularyItemVH(view);
        } else if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_add_vocabulary, parent, false);
            return new VocabularyFooterVH(view, this);
        }

        throw new RuntimeException("there is no type that matches the type " + viewType
                + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final Vocabulary vocabulary = mVocabularies.get(position);

        if (!isPositionFooter(position)) {
            /* Footer가 아닐 경우 */
            final CheckBox checkBox = ((VocabularyItemVH) holder).mCheckBox;
            mCheckBoxes.add(checkBox);

            ((VocabularyItemVH) holder).mNameTV.setText(vocabulary.getName());

            if (isPositionHeader(position)) {
                /* header일 경우 */
                ((VocabularyItemVH) holder).mImageIV.setImageResource(R.drawable.ic_star_big);
                ((VocabularyItemVH) holder).mImageIV.setBackgroundColor(vocabulary.getColor());

                ((VocabularyItemVH) holder).mCheckBox.setEnabled(false);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isChoiceMode) {
                            ((MainActivity) mContext).mToolbar.setTitle(vocabulary.getName());
                            ((AppCompatActivity) mContext)
                                    .getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.container, WordInVocaFragment.newInstance(
                                            vocabulary, mDBManager.selectFavoriteWords()))
                                    .addToBackStack(null)
                                    .commit();
                        }
                    }
                });
            } else {
                /* item일 경우 */
                ((VocabularyItemVH) holder).mImageIV.setImageResource(R.drawable.ic_vocabulary_big);
                ((VocabularyItemVH) holder).mImageIV.setBackgroundColor(
                        vocabulary.getColor());

                ((VocabularyItemVH) holder).itemView.setOnLongClickListener(
                        new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                if (!isChoiceMode) {
                                    mActionMode = ((AppCompatActivity) mContext).startSupportActionMode(
                                            new ActionBarCallBack());

                                    for (CheckBox checkBox : mCheckBoxes) {
                                        checkBox.setVisibility(View.VISIBLE);
                                    }

                                    isChoiceMode = true;
                                    holder.itemView.setPressed(false);

                                    ((VocabularyItemVH) holder).mCheckBox.setChecked(true);
                                    vocabulary.setSelected(true);

                                    // BottomBar 비활성화
                                    BottomBar bottomBar = ((MainActivity) mContext).mBottomBar;
                                    for (int index = 0; index < bottomBar.getTabCount(); index++) {
                                        bottomBar.getTabAtPosition(index).setEnabled(false);
                                        bottomBar.setAlpha(0.2f);
                                    }
                                }

                                return false;
                            }
                        });

                holder.itemView.setOnClickListener(new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isChoiceMode) {
                            ((MainActivity) mContext).mToolbar.setTitle(vocabulary.getName());
                            ((AppCompatActivity) mContext)
                                    .getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.container, WordInVocaFragment.newInstance(
                                            vocabulary, mDBManager.selectWordsInVoca(vocabulary)))
                                    .addToBackStack(null)
                                    .commit();
                        } else {
                            checkBox.setChecked(!checkBox.isChecked());
                            vocabulary.setSelected(checkBox.isChecked());
                        }
                    }
                });
            }
        } else {
            /* footer일 경우 */
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddVocabularyDialogFragment.newInstance(VocabularyAdapter.this)
                            .show(((AppCompatActivity) mContext).getSupportFragmentManager(), null);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mVocabularies.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionFooter(position)) return TYPE_FOOTER;
        if (isPositionHeader(position)) return TYPE_HEADER;
        return TYPE_ITEM;
    }

    @Override
    public void onVocabularyAdded(Vocabulary vocabulary) {
        if (mDBManager.insertVoca(vocabulary, null)) {
            add(vocabulary);
        } else {
            Toast.makeText(mContext, "'" + vocabulary.getName() + "'이(가) 이미 존재합니다.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    private boolean isPositionFooter(int position) {
        return position == mVocabularies.size() - 1;
    }

    public void add(Vocabulary vocabulary) {
        // 단어장을 Header 뒤에, Footer 앞에 위치시킴.
        if (mVocabularies.size() != 0) mVocabularies.add(mVocabularies.size() - 1, vocabulary);
        else mVocabularies.add(vocabulary);

        notifyDataSetChanged();
    }

    public void addAll(List<Vocabulary> vocabularies) {
        mVocabularies.addAll(mVocabularies.size() - 1, vocabularies);

        notifyDataSetChanged();
    }

    private ArrayList<Vocabulary> getSelectedVocas() {
        List<Vocabulary> selectedVocas = new ArrayList<>();
        for (Vocabulary vocabulary : mVocabularies) {
            if (vocabulary.isSelected()) selectedVocas.add(vocabulary);
        }

        return (ArrayList<Vocabulary>) selectedVocas;
    }

    private void removeVocas() {
        for (int index = 0; index < mVocabularies.size(); ) {
            Vocabulary vocabulary = mVocabularies.get(index);

            if (vocabulary.isSelected()) {
                mDBManager.removeVoca(vocabulary);
                mVocabularies.remove(index);
                mCheckBoxes.remove(index);
                notifyItemRemoved(index);
                notifyItemRangeChanged(index, mVocabularies.size());
            } else {
                index++;
            }
        }
    }

    private void quitChoiceMode() {
        if (isChoiceMode) {
            for (CheckBox checkBox : mCheckBoxes) {
                checkBox.setVisibility(View.GONE);
                checkBox.setChecked(false);
            }
            for (Vocabulary vocabulary : mVocabularies) {
                vocabulary.setSelected(false);
            }

            BottomBar bottomBar = ((MainActivity) mContext).mBottomBar;
            for (int index = 0; index < bottomBar.getTabCount(); index++) {
                bottomBar.getTabAtPosition(index).setEnabled(true);
                bottomBar.setAlpha(1.0f);
            }

            isChoiceMode = false;
        }
    }

    public class ActionBarCallBack implements ActionMode.Callback,
            RemoveVocaDialogFragment.RemoveVocaDialogListener {

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            // TODO Auto-generated method stub
            int id = item.getItemId();

            if (id == R.id.action_remove) {
                RemoveVocaDialogFragment.newInstance(getSelectedVocas().size(), this)
                        .show(((AppCompatActivity) mContext).getSupportFragmentManager(),
                                WordAdapter.TAG_REMOVE);
                return true;
            }

            return false;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_voca_contextual, menu);
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

        @Override
        public void onConfirm() {
            Toast.makeText(mContext,
                    getSelectedVocas().size() + "개의 단어장이 삭제되었습니다.",
                    Toast.LENGTH_SHORT)
                    .show();
            removeVocas();
            mActionMode.finish();
        }

    }

//    public class VocabularyItemVH extends RecyclerView.ViewHolder {
//
//        public ImageView mImageIV;
//        public TextView mNameTV;
//
//        public VocabularyItemVH(View itemView) {
//            super(itemView);
//
//            mImageIV = (ImageView) itemView.findViewById(R.id.iv_vocabulary);
//            mNameTV = (TextView) itemView.findViewById(R.id.tv_vocabulary);
//        }
//
//    }
//
//    public class VocabularyFooterVH extends RecyclerView.ViewHolder {
//
//        public ImageView mImageIV;
//
//        public VocabularyFooterVH(View itemView) {
//            super(itemView);
//
//            mImageIV = (ImageView) itemView.findViewById(R.id.iv_add_vocabulary);
//
//            itemView.setOnClickListener(new android.view.View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    AddVocabularyDialogFragment.newInstance(VocabularyAdapter.this);
//                }
//            });
//        }
//
//    }

}
