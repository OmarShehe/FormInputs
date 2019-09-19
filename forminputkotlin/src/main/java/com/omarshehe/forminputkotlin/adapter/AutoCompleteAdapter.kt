package com.omarshehe.forminputkotlin.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.LinearLayout
import android.widget.TextView
import com.omarshehe.forminputkotlin.R
import java.util.ArrayList


class AutoCompleteAdapter :  ArrayAdapter<String> {
    private var mContext : Context
    private var mListener : ItemSelectedListener
    private var mItems: ArrayList<String> = ArrayList()
    private var mTempItems: ArrayList<String> = ArrayList()
    private var mSuggestions: ArrayList<String> = ArrayList()
    private var mDisableFilter: Boolean = true


    constructor(context: Context, resource: Int, textViewResourceId: Int, items: ArrayList<String>,listener : ItemSelectedListener) : super(context, resource, textViewResourceId, items){
        mContext=context
        mListener=listener
        mItems=items
        mTempItems= ArrayList(items)
        mSuggestions=ArrayList()


    }

    private val nameFilter = object : Filter() {
        override fun convertResultToString(resultValue: Any): CharSequence {
            return resultValue as String
        }

        override fun performFiltering(constraint: CharSequence?): Filter.FilterResults {
            if (constraint != null) {
                mSuggestions.clear()
                for (item in mTempItems) {
                    if (item.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        mSuggestions.add(item)
                    }
                }
                val filterResults = Filter.FilterResults()
                filterResults.values = mSuggestions
                filterResults.count = mSuggestions.size
                return filterResults
            } else {
                return Filter.FilterResults()
            }
        }

        override fun publishResults(constraint: CharSequence, results: Filter.FilterResults) {
            if (mDisableFilter) {
                clear()
                for (item in mTempItems) {
                    add(item)
                    notifyDataSetChanged()
                }
            } else {
                val filterList = results.values as ArrayList<String>
                if (results.count > 0) {
                    clear()
                    for (item in filterList) {
                        add(item)
                        notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val item = mItems[position]
        val lblName = convertView?.findViewById(R.id.txtAutocomplete) as TextView

        lblName.text = item

        (convertView.findViewById(R.id.layParent) as LinearLayout).setOnClickListener {
            mListener.onItemSelected(
                item
            )
        }
        return convertView
    }


    fun DisableFilter(disableFilter : Boolean){
        mDisableFilter=disableFilter
    }


    override fun getFilter(): Filter {
        return super.getFilter()
    }

    interface ItemSelectedListener {
        public fun onItemSelected(item: String) {

        }
    }
}