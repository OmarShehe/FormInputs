package com.omarshehe.forminputjava.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.omarshehe.forminputjava.R;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteAdapter extends ArrayAdapter<String> {

    private Context context;
    private ItemSelectedListener mListener;
    private ArrayList<String> items, tempItems, suggestions;
    private boolean disableFilter;

    private Filter nameFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((String) resultValue);
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (String item : tempItems) {
                    if (item.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(item);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (disableFilter) {
                clear();
                for (String item : tempItems) {
                    add(item);
                    notifyDataSetChanged();
                }
            } else {
                List<String> filterList = (ArrayList<String>) results.values;
                if (results.count > 0) {
                    clear();
                    for (String item : filterList) {
                        add(item);
                        notifyDataSetChanged();
                    }
                }
            }
        }
    };

    public AutoCompleteAdapter(Context context, int resource, int textViewResourceId, ArrayList<String> items, ItemSelectedListener listener) {
        super(context, resource, textViewResourceId, items);
        this.items = items;
        mListener = listener;
        this.context = context;
        suggestions = new ArrayList<>();
        tempItems = new ArrayList<>(items); // this makes the difference.
    }

    public void disableFilter(boolean disableFilter) {
        this.disableFilter = disableFilter;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listitem_autocomplete, parent, false);
        }
        final String item = items.get(position);
        if (item != null) {
            TextView lblName = view.findViewById(R.id.txtAutocomplete);
            if (lblName != null)
                lblName.setText(item);

            ((LinearLayout) view.findViewById(R.id.layParent)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemSelected(item);
                }
            });
        }


        return view;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    public interface ItemSelectedListener {
        public void onItemSelected(String item);
    }


}
