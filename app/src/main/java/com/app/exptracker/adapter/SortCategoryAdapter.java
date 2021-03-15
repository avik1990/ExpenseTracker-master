package com.app.exptracker.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.exptracker.R;
import com.app.exptracker.model.ExcelDataModel;
import com.app.exptracker.utility.CircularTextView;
import com.app.exptracker.utility.ItemTouchHelperAdapter;
import com.app.exptracker.utility.ItemTouchHelperViewHolder;
import com.app.exptracker.utility.OnStartDragListener;

import java.util.Collections;
import java.util.List;

public class SortCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperAdapter {

    private List<ExcelDataModel> mPersonList;
    OnItemClickListener mItemClickListener;
    private final LayoutInflater mInflater;
    private final OnStartDragListener mDragStartListener;
    private Context mContext;
    GetSortedList getSortedList;

    public interface GetSortedList {
        void sortedList(List<ExcelDataModel> mPersonList);
    }

    public void updateReceiptsList(List<ExcelDataModel> list_shareofshelf) {
        this.mPersonList = list_shareofshelf;
        this.notifyDataSetChanged();
    }

    public SortCategoryAdapter(Context context, List<ExcelDataModel> list, OnStartDragListener dragListner, GetSortedList getSortedList) {
        this.mPersonList = list;
        this.mInflater = LayoutInflater.from(context);
        mDragStartListener = dragListner;
        this.getSortedList = getSortedList;
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = mInflater.inflate(R.layout.cat_drag_item, viewGroup, false);
        return new VHItem(v);
    }

    /*@Override
    public int getItemViewType(int position) {
        return position;
    }*/

    @Override
    public long getItemId(int position) {
        return position;
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int i) {
        if (viewHolder instanceof VHItem) {
            final VHItem holder = (VHItem) viewHolder;
            ((VHItem) viewHolder).title.setText(mPersonList.get(i).getCategory());
            ((VHItem) viewHolder).image_menu.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                        mDragStartListener.onStartDrag(holder);
                    }
                    return false;
                }
            });

            try {
                if (mPersonList.get(i).getIcon_name() != null && !mPersonList.get(i).getIcon_name().isEmpty()) {
                    holder.cv_text.setVisibility(View.GONE);
                    holder.imageView.setVisibility(View.VISIBLE);
                    int imageid = mContext.getResources().getIdentifier(mPersonList.get(i).getIcon_name(), "drawable", mContext.getPackageName());
                    holder.imageView.setImageResource(imageid);
                } else {
                    //holder.cv_text.setSolidColor(mContext.getResources().getColor(Color.parseColor("#D3D3D3")));
                    holder.cv_text.setVisibility(View.VISIBLE);
                    holder.imageView.setVisibility(View.GONE);
                    holder.cv_text.setText(mPersonList.get(i).getCategory().substring(0, 1));
                }
            } catch (Exception e) {
                e.printStackTrace();
                holder.imageView.setImageResource(R.drawable.ic_gray_no_img);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mPersonList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }


    public class VHItem extends RecyclerView.ViewHolder implements View.OnClickListener, ItemTouchHelperViewHolder {
        public TextView title;
        private ImageView imageView;
        private ImageView image_menu;
        CircularTextView cv_text;

        public VHItem(View itemView) {
            super(itemView);
            cv_text = itemView.findViewById(R.id.cv_text);
            title = itemView.findViewById(R.id.name);
            image_menu = itemView.findViewById(R.id.image_menu);
            imageView = itemView.findViewById(R.id.circle_imageView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getPosition());
            }
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }

    @Override
    public void onItemDismiss(int position) {
        mPersonList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        //Log.v("", "Log position" + fromPosition + " " + toPosition);
        if (fromPosition < mPersonList.size() && toPosition < mPersonList.size()) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(mPersonList, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(mPersonList, i, i - 1);
                }
            }
            notifyItemMoved(fromPosition, toPosition);
            getSortedList.sortedList(mPersonList);
        }
        // updateList(mPersonList);
        return true;
    }

    public void updateList(List<ExcelDataModel> list) {
        mPersonList = list;
        notifyDataSetChanged();
    }
}