/*
 * Copyright 2016 Hippo Seven
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hippo.ehviewer.ui.scene;

import android.content.DialogInterface;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.SwipeDismissItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction;
import com.h6ah4i.android.widget.advrecyclerview.touchguard.RecyclerViewTouchActionGuardManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableSwipeableItemViewHolder;
import com.hippo.app.EditTextDialogBuilder;
import com.hippo.easyrecyclerview.EasyRecyclerView;
import com.hippo.ehviewer.EhApplication;
import com.hippo.ehviewer.R;
import com.hippo.ehviewer.dao.DownloadLabelRaw;
import com.hippo.util.ActivityHelper;
import com.hippo.vector.VectorDrawable;
import com.hippo.view.ViewTransition;
import com.hippo.widget.SimpleImageView;
import com.hippo.yorozuya.ViewUtils;

import java.util.List;

public class DownloadLabelScene extends ToolbarScene {

    @Nullable
    private EasyRecyclerView mRecyclerView;
    @Nullable
    private ViewTransition mViewTransition;

    @Nullable
    private RecyclerView.Adapter mAdapter;

    @Nullable
    public List<DownloadLabelRaw> mList = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mList = EhApplication.getDownloadManager(getContext()).getLabelList();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mList = null;
    }

    @SuppressWarnings("deprecation")
    @Nullable
    @Override
    public View onCreateView2(LayoutInflater inflater,
            @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.scene_download_label, container, false);

        mRecyclerView = (EasyRecyclerView) view.findViewById(R.id.recycler_view);
        View tip = view.findViewById(R.id.tip);
        SimpleImageView tipImage = (SimpleImageView) tip.findViewById(R.id.tip_image);
        tipImage.setDrawable(VectorDrawable.create(getContext(), R.xml.sadpanda_head));
        mViewTransition = new ViewTransition(mRecyclerView, tip);

        if (mRecyclerView != null) {
            // touch guard manager  (this class is required to suppress scrolling while swipe-dismiss animation is running)
            RecyclerViewTouchActionGuardManager guardManager = new RecyclerViewTouchActionGuardManager();
            guardManager.setInterceptVerticalScrollingWhileAnimationRunning(true);
            guardManager.setEnabled(true);

            // drag & drop manager
            RecyclerViewDragDropManager dragDropManager = new RecyclerViewDragDropManager();
            dragDropManager.setDraggingItemShadowDrawable(
                    (NinePatchDrawable) getResources().getDrawable(R.drawable.shadow_8dp));

            // swipe manager
            RecyclerViewSwipeManager swipeManager = new RecyclerViewSwipeManager();

            RecyclerView.Adapter adapter = new LabelAdapter();
            adapter.setHasStableIds(true);
            adapter = dragDropManager.createWrappedAdapter(adapter); // wrap for dragging
            adapter = swipeManager.createWrappedAdapter(adapter); // wrap for swiping
            mAdapter = adapter;

            final GeneralItemAnimator animator = new SwipeDismissItemAnimator();
            animator.setSupportsChangeAnimations(false);

            mRecyclerView.hasFixedSize();
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            mRecyclerView.setAdapter(adapter);
            mRecyclerView.setItemAnimator(animator);

            guardManager.attachRecyclerView(mRecyclerView);
            swipeManager.attachRecyclerView(mRecyclerView);
            dragDropManager.attachRecyclerView(mRecyclerView);
        }

        updateView();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitle(R.string.label);
        setNavigationIcon(VectorDrawable.create(getContext(), R.xml.ic_arrow_left_dark_x24));

        // Clear nav checked item
        setNavCheckedItem(0);

        // Hide IME
        ActivityHelper.hideSoftInput(getActivity());
    }

    @Override
    public void onNavigationClick() {
        onBackPressed();
    }

    @Override
    public int getMenuResId() {
        return R.menu.scene_download_label;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_add: {
                EditTextDialogBuilder builder = new EditTextDialogBuilder(getContext(), null, getString(R.string.label));
                builder.setTitle(R.string.new_label_title);
                builder.setPositiveButton(android.R.string.ok, null);
                AlertDialog dialog = builder.show();
                new NewLabelDialogHelper(builder, dialog);
            }
        }
        return false;
    }

    private void updateView() {
        if (mViewTransition != null) {
            if (mList != null && mList.size() > 0) {
                mViewTransition.showView(0);
            } else {
                mViewTransition.showView(1);
            }
        }
    }

    private class NewLabelDialogHelper implements View.OnClickListener {

        private final EditTextDialogBuilder mBuilder;
        private final AlertDialog mDialog;

        public NewLabelDialogHelper(EditTextDialogBuilder builder, AlertDialog dialog) {
            mBuilder = builder;
            mDialog = dialog;
            Button button = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            if (button != null) {
                button.setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View v) {
            String text = mBuilder.getText();
            if (TextUtils.isEmpty(text)) {
                mBuilder.setError(getString(R.string.label_text_is_empty));
            } else if (getString(R.string.default_download_label_name).equals(text)) {
                mBuilder.setError(getString(R.string.label_text_is_invalid));
            } else if (EhApplication.getDownloadManager(getContext()).containLabel(text)) {
                mBuilder.setError(getString(R.string.label_text_exist));
            } else {
                mBuilder.setError(null);
                mDialog.dismiss();
                EhApplication.getDownloadManager(getContext()).addLabel(text);
                if (mAdapter != null && mList != null) {
                    mAdapter.notifyItemInserted(mList.size() - 1);
                }
                if (mViewTransition != null) {
                    if (mList != null && mList.size() > 0) {
                        mViewTransition.showView(0);
                    } else {
                        mViewTransition.showView(1);
                    }
                }
            }
        }
    }

    private class LabelHolder extends AbstractDraggableSwipeableItemViewHolder
            implements View.OnClickListener {

        public final View swipeHandler;
        public final TextView label;
        public final View dragHandler;

        public LabelHolder(View itemView) {
            super(itemView);

            swipeHandler = itemView.findViewById(R.id.swipe_handler);
            label = (TextView) itemView.findViewById(R.id.label);
            dragHandler = itemView.findViewById(R.id.drag_handler);

            label.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // TODO
        }

        @Override
        public View getSwipeableContainerView() {
            return swipeHandler;
        }
    }

    private class LabelAdapter extends RecyclerView.Adapter<LabelHolder>
            implements DraggableItemAdapter<LabelHolder>,
            SwipeableItemAdapter<LabelHolder> {


        @Override
        public LabelHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new LabelHolder(getActivity().getLayoutInflater()
                    .inflate(R.layout.item_download_label, parent, false));
        }

        @Override
        public void onBindViewHolder(LabelHolder holder, int position) {
            if (mList != null) {
                holder.label.setText(mList.get(position).getLabel());
            }
        }

        @Override
        public long getItemId(int position) {
            return mList != null ? mList.get(position).getId() : 0;
        }

        @Override
        public int getItemCount() {
            return mList != null ? mList.size() : 0;
        }

        @Override
        public boolean onCheckCanStartDrag(LabelHolder holder, int position, int x, int y) {
            return ViewUtils.isViewUnder(holder.dragHandler, x, y, 0);
        }

        @Override
        public ItemDraggableRange onGetItemDraggableRange(LabelHolder holder, int position) {
            return null;
        }

        @Override
        public void onMoveItem(int fromPosition, int toPosition) {
            if (fromPosition == toPosition) {
                return;
            }

            EhApplication.getDownloadManager(getContext()).moveLabel(fromPosition, toPosition);
            if (mAdapter != null && mList != null) {
                notifyItemMoved(fromPosition, toPosition);
            }
        }

        @Override
        public int onGetSwipeReactionType(LabelHolder holder, int position, int x, int y) {
            return SwipeableItemConstants.REACTION_CAN_SWIPE_BOTH_H;
        }

        @Override
        public void onSetSwipeBackground(LabelHolder holder, int position, int type) {
            // Empty
        }

        @Override
        public SwipeResultAction onSwipeItem(LabelHolder holder, int position, int result) {
            switch (result) {
                // swipe right
                case SwipeableItemConstants.RESULT_SWIPED_RIGHT:
                case SwipeableItemConstants.RESULT_SWIPED_LEFT:
                    return null;
                case SwipeableItemConstants.RESULT_CANCELED:
                default:
                    return null;
            }
        }
    }
}
