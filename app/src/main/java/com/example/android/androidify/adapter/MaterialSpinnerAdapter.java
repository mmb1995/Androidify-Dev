package com.example.android.androidify.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;

/**
 * Adapter that works with Material ExposedDropdownMenu in order to prevent filtering and to
 * force the dropdown to always display all of the available options
 * @param <String>
 */
public class MaterialSpinnerAdapter<String> extends ArrayAdapter<String> {
    protected String[] values;

    public MaterialSpinnerAdapter(Context context, int resource, String[] values) {
        super(context, resource, values);
        this.values = values;
    }

    private Filter customFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            results.values = values;
            results.count = values.length;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            notifyDataSetChanged();
        }
    };

    @Override
    public Filter getFilter() {
        return customFilter;
    }
}
