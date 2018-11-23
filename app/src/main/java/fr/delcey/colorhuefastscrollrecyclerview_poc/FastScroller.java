package fr.delcey.colorhuefastscrollrecyclerview_poc;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Set;

import static android.support.v7.widget.RecyclerView.OnScrollListener;

public class FastScroller extends LinearLayout {
    private static final int HANDLE_HIDE_DELAY = 1000;
    private static final int HANDLE_ANIMATION_DURATION = 100;
    private static final int TRACK_SNAP_RANGE = 5;
    private static final String SCALE_X = "scaleX";
    private static final String SCALE_Y = "scaleY";
    private static final String ALPHA = "alpha";

    private View elevator;
    private TextView bubble;

    private RecyclerView recyclerView;

    private final HandleHider bubbleHider = new HandleHider();
    private final ScrollListener scrollListener = new ScrollListener();
    private int height;

    private AnimatorSet currentAnimator = null;

    private ArrayList<Hue> mItems;

    public FastScroller(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialise(context);
    }

    public FastScroller(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialise(context);
    }

    private void initialise(Context context) {
        setOrientation(HORIZONTAL);
        setClipChildren(false);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.fastscroller, this);
        elevator = findViewById(R.id.fastscroller_elevator);
        bubble = findViewById(R.id.fastscroller_bubble);
    }

    private void setColorOfBubble() {
        int adapterPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

        changeColorOfBubble(mItems.get(adapterPosition).getColor());

        bubble.setText(mItems.get(adapterPosition).getName());
    }

    private void changeColorOfBubble(@ColorInt int color) {
        Drawable background = bubble.getBackground();

        GradientDrawable gradientDrawable = (GradientDrawable) background;
        gradientDrawable.setColor(color);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        height = h;
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
            setPosition(event.getY());
            if (currentAnimator != null) {
                currentAnimator.cancel();
            }
            getHandler().removeCallbacks(bubbleHider);
            if (bubble.getVisibility() == INVISIBLE) {
                showHandle();
            }
            setRecyclerViewPosition(event.getY());
            post(new Runnable() {
                @Override
                public void run() {
                    setColorOfBubble();
                }
            });

            return true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            getHandler().postDelayed(bubbleHider, HANDLE_HIDE_DELAY);
            return true;
        }
        return super.onTouchEvent(event);
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        recyclerView.addOnScrollListener(scrollListener);
    }

    private void setRecyclerViewPosition(float y) {
        if (recyclerView != null) {
            int itemCount = recyclerView.getAdapter().getItemCount();
            float proportion;
            if (elevator.getY() == 0) {
                proportion = 0f;
            } else if (elevator.getY() + elevator.getHeight() >= height - TRACK_SNAP_RANGE) {
                proportion = 1f;
            } else {
                proportion = y / (float) height;
            }
            int targetPos = getValueInRange(itemCount - 1, (int) (proportion * (float) itemCount));
            recyclerView.scrollToPosition(targetPos);
        }
    }

    private int getValueInRange(int max, int value) {
        int minimum = Math.max(0, value);
        return Math.min(minimum, max);
    }

    private void setPosition(float y) {
        float position = y / height;
        int elevatorHeight = elevator.getHeight();
        elevator.setY(getValueInRange(height - elevatorHeight, (int) ((height - elevatorHeight) * position)));
        int handleHeight = bubble.getHeight();
        bubble.setY(getValueInRange(height - handleHeight, (int) ((height - handleHeight) * position)));
    }

    private void showHandle() {
        AnimatorSet animatorSet = new AnimatorSet();
        bubble.setPivotX(bubble.getWidth());
        bubble.setPivotY(bubble.getHeight());
        bubble.setVisibility(VISIBLE);
        Animator growerX = ObjectAnimator.ofFloat(bubble, SCALE_X, 0f, 1f).setDuration(HANDLE_ANIMATION_DURATION);
        Animator growerY = ObjectAnimator.ofFloat(bubble, SCALE_Y, 0f, 1f).setDuration(HANDLE_ANIMATION_DURATION);
        Animator alpha = ObjectAnimator.ofFloat(bubble, ALPHA, 0f, 1f).setDuration(HANDLE_ANIMATION_DURATION);
        animatorSet.playTogether(growerX, growerY, alpha);
        animatorSet.start();
    }

    private void hideHandle() {
        currentAnimator = new AnimatorSet();
        bubble.setPivotX(bubble.getWidth());
        bubble.setPivotY(bubble.getHeight());
        Animator shrinkerX = ObjectAnimator.ofFloat(bubble, SCALE_X, 1f, 0f).setDuration(HANDLE_ANIMATION_DURATION);
        Animator shrinkerY = ObjectAnimator.ofFloat(bubble, SCALE_Y, 1f, 0f).setDuration(HANDLE_ANIMATION_DURATION);
        Animator alpha = ObjectAnimator.ofFloat(bubble, ALPHA, 1f, 0f).setDuration(HANDLE_ANIMATION_DURATION);
        currentAnimator.playTogether(shrinkerX, shrinkerY, alpha);
        currentAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                bubble.setVisibility(INVISIBLE);
                currentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                bubble.setVisibility(INVISIBLE);
                currentAnimator = null;
            }
        });
        currentAnimator.start();
    }

    public void setItems(Set<Hue> hues) {
        mItems = new ArrayList<>(hues);
    }

    private class HandleHider implements Runnable {
        @Override
        public void run() {
            hideHandle();
        }
    }

    private class ScrollListener extends OnScrollListener {
        @Override
        public void onScrolled(RecyclerView rv, int dx, int dy) {
            View firstVisibleView = recyclerView.getChildAt(0);
            int firstVisiblePosition = recyclerView.getChildPosition(firstVisibleView);
            int visibleRange = recyclerView.getChildCount();
            int lastVisiblePosition = firstVisiblePosition + visibleRange;
            int itemCount = recyclerView.getAdapter().getItemCount();
            int position;
            if (firstVisiblePosition == 0) {
                position = 0;
            } else if (lastVisiblePosition == itemCount - 1) {
                position = itemCount - 1;
            } else {
                position = firstVisiblePosition;
            }
            float proportion = (float) position / (float) itemCount;
            setPosition(height * proportion);
        }
    }
}