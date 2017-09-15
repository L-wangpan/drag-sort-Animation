package com.ss.android.drag.demochannel;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import me.yokeyword.itemtouchhelperdemo.R;
import helper.CalculateSpansize;
import helper.OnDragVHListener;
import helper.OnItemMoveListener;

import static android.support.v7.recyclerview.R.styleable.RecyclerView;

public class ChannelAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnItemMoveListener {

    public static final int TYPE_MY_CHANNEL_HEADER = 0;

    public static final int TYPE_MY = 1;

    public static final int TYPE_OTHER_CHANNEL_HEADER = 2;

    public static final int TYPE_OTHER = 3;


    private static final int COUNT_PRE_MY_HEADER = 1;

    private static final int COUNT_PRE_OTHER_HEADER = COUNT_PRE_MY_HEADER + 1;

    private static final long ANIM_TIME = 360L;


    private long startTime;

    private int ChannelsLines = 0;
    private int otherChannelLines = 0;
    private  int DefautLine=2;

    private static final long SPACE_TIME = 100;

    private LayoutInflater mInflater;
    private ItemTouchHelper mItemTouchHelper;


    private boolean isEditMode;
    CalculateSpansize calculateSpansize;
    private List<ChannelEntity> mMyChannelItems, mOtherChannelItems;

    private OnMyChannelItemClickListener mChannelItemClickListener;

    public ChannelAdapter(Context context, ItemTouchHelper helper, List<ChannelEntity> mMyChannelItems, List<ChannelEntity> mOtherChannelItems, int maxSpan) {
        this.mInflater = LayoutInflater.from(context);
        this.mItemTouchHelper = helper;
        this.mMyChannelItems = mMyChannelItems;
        this.mOtherChannelItems = mOtherChannelItems;
        calculateSpansize = new CalculateSpansize();
        CalculateLines(mMyChannelItems, TYPE_MY, maxSpan);
        CalculateLines(mOtherChannelItems, TYPE_OTHER, maxSpan);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_MY_CHANNEL_HEADER;
        } else if (position == mMyChannelItems.size() + 1) {
            return TYPE_OTHER_CHANNEL_HEADER;
        } else if (position > 0 && position < mMyChannelItems.size() + 1) {
            return TYPE_MY;
        } else if (position > mMyChannelItems.size() + 1 && position < mMyChannelItems.size() + 1 + mOtherChannelItems.size()) {
            return TYPE_OTHER;
        } else {
            return TYPE_OTHER;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final View view;
        switch (viewType) {
            case TYPE_MY_CHANNEL_HEADER:
                view = mInflater.inflate(R.layout.item_my_channel_header, parent, false);
                final MyChannelHeaderViewHolder holder = new MyChannelHeaderViewHolder(view);
                holder.tvBtnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isEditMode) {
                            startEditMode((RecyclerView) parent);
                            holder.tvBtnEdit.setText(R.string.finish);
                        } else {
                            cancelEditMode((RecyclerView) parent);
                            holder.tvBtnEdit.setText(R.string.edit);
                        }
                    }
                });
                return holder;

            case TYPE_MY:
                view = mInflater.inflate(R.layout.item_my, parent, false);
                final MyViewHolder myHolder = new MyViewHolder(view);
                myHolder.textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        int position = myHolder.getAdapterPosition();
                        if (isEditMode) {
                            RecyclerView recyclerView = ((RecyclerView) parent);
                            View targetView = recyclerView.getLayoutManager().findViewByPosition(mMyChannelItems.size() + COUNT_PRE_OTHER_HEADER);
                            View currentView = recyclerView.getLayoutManager().findViewByPosition(position);
                            RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
                            int spanCount = ((GridLayoutManager) manager).getSpanCount();
                            if (recyclerView.indexOfChild(targetView) >= 0) {
                                int targetX, targetY;
                                int currentline = getpreLine(mMyChannelItems, COUNT_PRE_MY_HEADER, spanCount, position);
                                Log.d("Tag", "--------------lines---------" + ChannelsLines);
                                if (currentline < ChannelsLines||currentline==2) {
                                    View preTargetView = recyclerView.getLayoutManager().findViewByPosition(mMyChannelItems.size() + COUNT_PRE_OTHER_HEADER - 1);
                                    targetX = preTargetView.getLeft();
                                    targetY = preTargetView.getTop() + preTargetView.getHeight() / 2;
                                    ChannelsLines--;
                                    Log.d("Tag", "----------decreaselines---------" + ChannelsLines);
                                } else {
                                    targetX = targetView.getLeft();
                                    targetY = targetView.getTop();
                                }
                                moveMyToOther(myHolder, spanCount);
                                startAnimation(recyclerView, currentView, targetX, targetY);

                            } else {
                                moveMyToOther(myHolder, spanCount);
                            }
                        } else {
                            mChannelItemClickListener.onItemClick(v, position - COUNT_PRE_MY_HEADER);
                        }
                    }
                });

                myHolder.textView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(final View v) {
                        if (!isEditMode) {
                            RecyclerView recyclerView = ((RecyclerView) parent);
                            startEditMode(recyclerView);

                            // header 按钮文字 改成 "完成"
                            View view = recyclerView.getChildAt(0);
                            if (view == recyclerView.getLayoutManager().findViewByPosition(0)) {
                                TextView tvBtnEdit = (TextView) view.findViewById(R.id.tv_btn_edit);
                                tvBtnEdit.setText(R.string.finish);
                            }
                        }
                        mItemTouchHelper.startDrag(myHolder);
                        return true;
                    }
                });

                myHolder.textView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (isEditMode) {
                            switch (MotionEventCompat.getActionMasked(event)) {
                                case MotionEvent.ACTION_DOWN:
                                    startTime = System.currentTimeMillis();
                                    break;
                                case MotionEvent.ACTION_MOVE:
                                    if (System.currentTimeMillis() - startTime > SPACE_TIME) {
                                        mItemTouchHelper.startDrag(myHolder);
                                    }
                                    break;
                                case MotionEvent.ACTION_CANCEL:
                                case MotionEvent.ACTION_UP:
                                    startTime = 0;
                                    break;
                            }

                        }
                        return false;
                    }
                });
                return myHolder;

            case TYPE_OTHER_CHANNEL_HEADER:
                view = mInflater.inflate(R.layout.item_other_channel_header, parent, false);
                return new RecyclerView.ViewHolder(view) {
                };

            case TYPE_OTHER:
                view = mInflater.inflate(R.layout.item_other, parent, false);
                final OtherViewHolder otherHolder = new OtherViewHolder(view);
                otherHolder.textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RecyclerView recyclerView = ((RecyclerView) parent);
                        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
                        int currentPiosition = otherHolder.getAdapterPosition();

                        View currentView = manager.findViewByPosition(currentPiosition);
                        GridLayoutManager gridLayoutManager = ((GridLayoutManager) manager);
                        int spanCount = gridLayoutManager.getSpanCount();
                        View preTargetView = manager.findViewByPosition(mMyChannelItems.size() - 1 + COUNT_PRE_MY_HEADER);
                        if (recyclerView.indexOfChild(preTargetView) >= 0) {
                            int targetX = preTargetView.getLeft();
                            int targetY = preTargetView.getTop();

                            int targetPosition = mMyChannelItems.size() - 1 + COUNT_PRE_OTHER_HEADER;

                            // target 在最后一行第一个
                            Log.d("Tag", ChannelsLines + "-------------------");
                            int sumSpansize = CalculateSumSpan(mMyChannelItems, spanCount);
                            if (sumSpansize == 0 || (sumSpansize + mOtherChannelItems.get(currentPiosition - mMyChannelItems.size() - 2).getSpansize()) > spanCount * ChannelsLines) {
                                View targetView = manager.findViewByPosition(targetPosition);
                                targetX = targetView.getLeft();
                                targetY = targetView.getTop();
                                ChannelsLines++;
                                Log.d("Tag", "----------increaselines---------" + ChannelsLines);
                                Log.d("Tag", "-----wowowowowo----");
                            } else {
                                targetX += preTargetView.getWidth();

                            }
                            //通过测试发现如果底部推荐栏可见部分最后一行最后一个可见item如果有点击的话，
                            // 会出现映像移动动画会稍晚于上面my Channels的item刷新动画
                            if (currentPiosition == gridLayoutManager.findLastVisibleItemPosition()) {
                                Log.d("Tag", "into----");
                                moveOtherToMyWithDelay(otherHolder);
                            } else {
                                moveOtherToMy(otherHolder, spanCount);
                            }
                            startAnimation(recyclerView, currentView, targetX, targetY);

                        } else {
                            moveOtherToMy(otherHolder, spanCount);
                        }
                    }
                });
                return otherHolder;
        }
        return null;
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {

            MyViewHolder myHolder = (MyViewHolder) holder;
           /* ViewGroup.LayoutParams layoutParams = myHolder.itemView.getLayoutParams();
            layoutParams=calculateSpansize.getSpansize(mMyChannelItems.get((position-1)).getName());*/
            myHolder.textView.setText(mMyChannelItems.get(position - COUNT_PRE_MY_HEADER).getName());
            if (isEditMode) {
                myHolder.imgEdit.setVisibility(View.VISIBLE);
            } else {
                myHolder.imgEdit.setVisibility(View.INVISIBLE);
            }

        } else if (holder instanceof OtherViewHolder) {

            ((OtherViewHolder) holder).textView.setText(mOtherChannelItems.get(position - mMyChannelItems.size() - COUNT_PRE_OTHER_HEADER).getName());

        } else if (holder instanceof MyChannelHeaderViewHolder) {

            MyChannelHeaderViewHolder headerHolder = (MyChannelHeaderViewHolder) holder;
            if (isEditMode) {
                headerHolder.tvBtnEdit.setText(R.string.finish);
            } else {
                headerHolder.tvBtnEdit.setText(R.string.edit);
            }
        }
    }

    @Override
    public int getItemCount() {

        return mMyChannelItems.size() + mOtherChannelItems.size() + COUNT_PRE_OTHER_HEADER;
    }


    /*
        创建移动动画
     */
    private void startAnimation(RecyclerView recyclerView, final View currentView, float targetX, float targetY) {
        final ViewGroup viewGroup = (ViewGroup) recyclerView.getParent();
        final ImageView mirrorView = addMirrorView(viewGroup, recyclerView, currentView);

        Animation animation = getTranslateAnimator(
                targetX - currentView.getLeft(), targetY - currentView.getTop());
        currentView.setVisibility(View.INVISIBLE);
        mirrorView.startAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                viewGroup.removeView(mirrorView);
                if (currentView.getVisibility() == View.INVISIBLE) {
                    currentView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /*

     */
    private void moveMyToOther(MyViewHolder myHolder, int Spansize) {
        int position = myHolder.getAdapterPosition();
        int tempspanSize = mOtherChannelItems.get(0).getSpansize();
        int startPosition = position - COUNT_PRE_MY_HEADER;
        if (startPosition > mMyChannelItems.size() - 1) {
            return;
        }
        ChannelEntity item = mMyChannelItems.get(startPosition);
        mMyChannelItems.remove(startPosition);

        mOtherChannelItems.add(0, item);
//        mOtherChannelItems.get(1).setSpansize(tempspanSize);
        notifyItemMoved(position, mMyChannelItems.size() + COUNT_PRE_OTHER_HEADER);
        CalculateLines(mMyChannelItems, TYPE_MY, Spansize);
    }

    /*
     Recommended移动到 Mychannel
     */
    private void moveOtherToMy(OtherViewHolder otherHolder, int Spansize) {
        int position = processItemRemoveAdd(otherHolder);
        if (position == -1) {
            return;
        }
        notifyItemMoved(position, mMyChannelItems.size() - 1 + COUNT_PRE_MY_HEADER);
        CalculateLines(mMyChannelItems, TYPE_MY, Spansize);
    }

    /*
        Recommended移动到 Mychannel 伴随延迟
    */
    private void moveOtherToMyWithDelay(OtherViewHolder otherHolder) {
        final int position = processItemRemoveAdd(otherHolder);
        if (position == -1) {
            return;
        }
        delayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                notifyItemMoved(position, mMyChannelItems.size() - 1 + COUNT_PRE_MY_HEADER);
            }
        }, 200);
    }

    private Handler delayHandler = new Handler();

    private int processItemRemoveAdd(OtherViewHolder otherHolder) {
        int position = otherHolder.getAdapterPosition();

        int startPosition = position - mMyChannelItems.size() - COUNT_PRE_OTHER_HEADER;
        if (startPosition > mOtherChannelItems.size() - 1) {
            return -1;
        }
        ChannelEntity item = mOtherChannelItems.get(startPosition);
        mOtherChannelItems.remove(startPosition);
        mMyChannelItems.add(item);
        return position;
    }


    private ImageView addMirrorView(ViewGroup parent, RecyclerView recyclerView, View view) {

        /*
        *创建映像
        *
        * */
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(true);
        final ImageView mirrorView = new ImageView(recyclerView.getContext());
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        mirrorView.setImageBitmap(bitmap);
        view.setDrawingCacheEnabled(false);
        int[] locations = new int[2];
        view.getLocationOnScreen(locations);
        int[] parenLocations = new int[2];
        recyclerView.getLocationOnScreen(parenLocations);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(bitmap.getWidth(), bitmap.getHeight());
        params.setMargins(locations[0], locations[1] - parenLocations[1], 0, 0);
        parent.addView(mirrorView, params);

        return mirrorView;
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        ChannelEntity fromitem = mMyChannelItems.get(fromPosition - COUNT_PRE_MY_HEADER);
        mMyChannelItems.remove(fromPosition - COUNT_PRE_MY_HEADER);
        mMyChannelItems.add(toPosition - COUNT_PRE_MY_HEADER, fromitem);
        notifyItemMoved(fromPosition, toPosition);
    }


    private void startEditMode(RecyclerView parent) {
        isEditMode = true;

        int visibleChildCount = parent.getChildCount();
        for (int i = 0; i < visibleChildCount; i++) {
            View view = parent.getChildAt(i);
            ImageView imgEdit = (ImageView) view.findViewById(R.id.img_edit);
            if (imgEdit != null) {
                imgEdit.setVisibility(View.VISIBLE);
            }
        }
    }


    private void cancelEditMode(RecyclerView parent) {
        isEditMode = false;

        int visibleChildCount = parent.getChildCount();
        for (int i = 0; i < visibleChildCount; i++) {
            View view = parent.getChildAt(i);
            ImageView imgEdit = (ImageView) view.findViewById(R.id.img_edit);
            if (imgEdit != null) {
                imgEdit.setVisibility(View.INVISIBLE);
            }
        }
    }

    private TranslateAnimation getTranslateAnimator(float targetX, float targetY) {
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.ABSOLUTE, targetX,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.ABSOLUTE, targetY);
        // RecyclerView默认移动动画250ms 这里设置360ms 是为了防止在位移动画结束后 remove(view)过早 导致闪烁
        translateAnimation.setDuration(ANIM_TIME);
        translateAnimation.setFillAfter(true);
        return translateAnimation;
    }

    interface OnMyChannelItemClickListener {
        void onItemClick(View v, int position);
    }

    public void setOnMyChannelItemClickListener(OnMyChannelItemClickListener listener) {
        this.mChannelItemClickListener = listener;
    }


    class MyViewHolder extends RecyclerView.ViewHolder implements OnDragVHListener {
        private TextView textView;
        private ImageView imgEdit;

        //        private View mitemView;
        public MyViewHolder(View itemView) {
            super(itemView);
//            this.mitemView=itemView;
            textView = (TextView) itemView.findViewById(R.id.tv);
            imgEdit = (ImageView) itemView.findViewById(R.id.img_edit);
        }


        @Override
        public void onItemSelected() {
            textView.setBackgroundResource(R.drawable.bg_channel_p);
        }


        @Override
        public void onItemFinish() {
            textView.setBackgroundResource(R.drawable.bg_channel);
        }
    }


    class OtherViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public OtherViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.tv);
        }
    }


    class MyChannelHeaderViewHolder extends RecyclerView.ViewHolder {
        private TextView tvBtnEdit;

        public MyChannelHeaderViewHolder(View itemView) {
            super(itemView);
            tvBtnEdit = (TextView) itemView.findViewById(R.id.tv_btn_edit);
        }
    }

    private int CalculateSumSpan(List<ChannelEntity> items, int spanCount) {
        int currentlines = 2;

        int currentSpansize = spanCount;
        if (items.size() == 0) {
            return 0;
        }
        for (int i = 0; i < items.size(); i++) {

            int tempSize = items.get(i).getSpansize();
            if (currentSpansize + tempSize > (spanCount * currentlines)) {
                currentSpansize = spanCount * currentlines + tempSize;
                currentlines++;
            } else {
                currentSpansize += tempSize;
            }

        }
        return currentSpansize;
    }

    private boolean isLineLast(List<ChannelEntity> items, int type, int spanCount, int position) {
        int currentlines = 0;
        int currentSpansize = 0;
        int lines;
        if (type == TYPE_MY) {
            lines = ChannelsLines;
        } else if (type == TYPE_OTHER) {
            lines = otherChannelLines;
        } else {
            lines = ChannelsLines;
        }
        currentlines = 2;
        currentSpansize = spanCount;
        for (int i = 0; i < items.size(); i++) {
            int tempSize = items.get(i).getSpansize();
            if (position - 1 == i && position < items.size()) {
                int temp = items.get(position).getSpansize();
                if (currentSpansize + temp > (spanCount * currentlines)) {
                    return true;
                }
                return false;
            }
            if (currentSpansize + tempSize > (spanCount * currentlines)) {
                currentSpansize = spanCount * currentlines + tempSize;
                currentlines++;
            } else {
                currentSpansize += tempSize;
            }
        }
        if (currentlines < lines)
            return true;
        else {
            return false;
        }
    }

    public int getpreLine(List<ChannelEntity> items, int type, int spanCount, int position) {
        int currentlines = 0;

        int currentSpansize = 0;

        if (type == TYPE_MY) {
            currentlines = 2;
            currentSpansize = spanCount;
        } else if (type == TYPE_OTHER) {
            currentlines = 2;
            currentSpansize = spanCount;
        } else {
            currentlines = 1;
            currentSpansize = 0;
        }
        for (int i = 0; i < items.size(); i++) {
            int tempSize = items.get(i).getSpansize();
            if (position - 1 == i) {
                continue;
            }
            if (currentSpansize + tempSize > (spanCount * currentlines)) {
                currentSpansize = spanCount * currentlines + tempSize;
                currentlines++;
            } else {
                currentSpansize += tempSize;
            }

        }
        return currentlines;
    }

    private void CalculateLines(List<ChannelEntity> items, int type, int spanCount) {
        int currentlines = 0;

        int currentSpansize = 0;

        if (type == TYPE_MY) {
            currentlines = DefautLine;
            currentSpansize = spanCount;
            for (int i = 0; i < items.size(); i++) {
                int tempSize = items.get(i).getSpansize();
                if (currentSpansize + tempSize > (spanCount * currentlines)) {
                    currentSpansize = spanCount * currentlines + tempSize;
                    currentlines++;
                } else {
                    currentSpansize += tempSize;
                }

            }
            ChannelsLines = currentlines;

        } else if (type == TYPE_OTHER) {
            currentlines = DefautLine;
            currentSpansize = spanCount;
            for (int i = 0; i < items.size(); i++) {
                int tempSize = items.get(i).getSpansize();
                if (currentSpansize + tempSize > (spanCount * currentlines)) {
                    currentSpansize = spanCount * currentlines + tempSize;
                    currentlines++;
                } else {
                    currentSpansize += tempSize;
                }

            }
            otherChannelLines = currentlines;
        } else {

            ChannelsLines = DefautLine;
            otherChannelLines = DefautLine;
        }


    }
    private int getChannelsLine(){
        return ChannelsLines;
    }
    private  int getOtherChannelLines(){
        return otherChannelLines;
    }

}
