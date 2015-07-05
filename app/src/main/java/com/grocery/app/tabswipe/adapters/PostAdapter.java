package com.grocery.app.tabswipe.adapters;

/**
 * Created by SG0222540 on 6/28/2015.
 */

import java.util.ArrayList;

import android.speech.tts.UtteranceProgressListener;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.grocery.app.tabswipe.R;
import com.grocery.app.tabswipe.models.DataModel;
import com.grocery.app.tabswipe.utilities.Utilities;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private boolean buyFlag;
    private ArrayList<DataModel> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView itemName;
        public TextView description;
        public TextView txtQuantity;
        public ImageButton btnPlus;
        public ImageButton btnMinus;

        public ViewHolder(View v) {
            super(v);
            itemName = (TextView) v.findViewById(R.id.txtItemName);
            description = (TextView) v.findViewById(R.id.txtDescription);
            txtQuantity = (TextView) v.findViewById(R.id.txtQuantity);
            btnPlus = (ImageButton) v.findViewById(R.id.btnPlus);
            btnMinus = (ImageButton) v.findViewById(R.id.btnMinus);
            btnPlus.setVisibility(View.GONE);
            btnMinus.setVisibility(View.VISIBLE);

        }
    }

    public void add(DataModel item) {
        if (mDataset.contains(item)) {
            int position = mDataset.indexOf(item);
            mDataset.get(position).setQuantity(item.getQuantity());
            notifyDataSetChanged();
        } else {
            mDataset.add(mDataset.size(), item);
            notifyItemInserted(mDataset.size());
        }
    }

    public void remove(String item) {
        int position = mDataset.indexOf(item);
        mDataset.remove(position);
        notifyItemRemoved(position);
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public PostAdapter(ArrayList<DataModel> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.buy_leg_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final String itemName = mDataset.get(position).getItemName();
        holder.itemName.setText(itemName);
        holder.description.setText(mDataset.get(position).getDescription());
        holder.txtQuantity.setText(mDataset.get(position).getQuantity());
        /*holder.btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = Integer.parseInt(holder.txtQuantity.getText().toString());
                if (quantity - 1 == 0) {
                    remove(itemName);
                } else {
                    holder.txtQuantity.setText(String.valueOf(quantity - 1));
                    mDataset.get(position).setQuantity(String.valueOf(quantity - 1));
                    Utilities.addToMyItems(itemName, mDataset.get(position));
                }
            }
        });*/
        /*holder.itemName.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(name);
            }
        });*/
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }


}