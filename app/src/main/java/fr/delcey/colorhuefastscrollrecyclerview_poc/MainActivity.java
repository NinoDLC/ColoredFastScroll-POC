package fr.delcey.colorhuefastscrollrecyclerview_poc;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

public class MainActivity extends AppCompatActivity {

    Set<Hue> mHues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        computeHues();

        RecyclerView recyclerView = findViewById(R.id.main_rv);
        recyclerView.setAdapter(new ColorAdapter(mHues));
        int duration = 200;//getResources().getInteger(R.integer.scroll_duration);
        recyclerView.setLayoutManager(new ScrollingLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false, duration));
        FastScroller fastScroller = findViewById(R.id.fastscroller);
        fastScroller.setItems(mHues);
        fastScroller.setRecyclerView(recyclerView);

    }

    private void computeHues() {
        mHues = new TreeSet<>(new Comparator<Hue>() {
            @Override
            public int compare(Hue o1, Hue o2) {
                return o1.compareTo(o2);
            }
        });

        String[] colors = Const.LIST_OF_COLOR.split("/");

        for (String colorToSplit : colors) {
            String[] hexAndName = colorToSplit.split(",");

            float[] hsv = new float[3];

            int color = Color.parseColor(hexAndName[0]);
            Color.colorToHSV(color, hsv);

            mHues.add(new Hue(hsv, hexAndName[1]));
        }

        Log.d("volko", "onCreate() called with: mdr");
    }
}
