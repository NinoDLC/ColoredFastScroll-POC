package fr.delcey.colorhuefastscrollrecyclerview_poc;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ColorViewHolder> {

    private List<Hue> mHues;

    public ColorAdapter(Set<Hue> hues) {
        mHues = new ArrayList<>(hues);
    }

    @Override
    public ColorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ColorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ColorViewHolder holder, int position) {
        holder.bind(mHues.get(position));
    }

    @Override
    public int getItemCount() {
        return mHues.size();
    }

    public class ColorViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public ColorViewHolder(View itemView) {
            super(itemView);

            textView = itemView.findViewById(android.R.id.text1);
        }

        public void bind(Hue hue) {
            textView.setText(hue.getName());
            textView.setBackgroundColor(hue.getColor());
        }
    }
}
