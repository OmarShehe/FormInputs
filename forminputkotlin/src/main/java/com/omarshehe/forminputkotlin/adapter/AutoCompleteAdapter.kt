package com.omarshehe.forminputkotlin.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.omarshehe.forminputkotlin.R
import java.util.ArrayList

class AutoCompleteAdapter(context: Context, resource: Int, private val items: ArrayList<String>, private val mListener: ItemSelectedListener)
    : ArrayAdapter<String>(context, resource, items) {
    private val tempItems: ArrayList<String> = ArrayList(items)
    private val suggestions: ArrayList<String> = ArrayList()
    private var disableFilter: Boolean = false

    private val nameFilter = object : Filter() {
        override fun convertResultToString(resultValue: Any): CharSequence {
            return resultValue as String
        }

        @SuppressLint("DefaultLocale")
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            return if (constraint != null) {
                suggestions.clear()
                for (item in tempItems) {
                    if (item.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(item)
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = suggestions
                filterResults.count = suggestions.size
                filterResults
            } else {
                FilterResults()
            }
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            if (disableFilter) {
                clear()
                for (item in tempItems) {
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

    override fun getFilter(): Filter {
        return nameFilter
    }

    fun disableFilter(disableFilter: Boolean) {
        this.disableFilter = disableFilter
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View? {
        var convertView = view
        if (convertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.listitem_autocomplete, parent, false)
        }
        val item = items[position]
        val lblName = convertView!!.findViewById<TextView>(R.id.tvView)
        if (lblName != null){
            lblName.text = item
        }
        convertView.findViewById<View>(R.id.layParent).setOnClickListener { mListener.onItemSelected(item) }
        return convertView
    }

    interface ItemSelectedListener {
        fun onItemSelected(item: String?)
    }


}
