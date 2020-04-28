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
import java.util.*

class AutoCompleteAdapter(context: Context, resource: Int,
                          private val items: ArrayList<String>, private val mListener: ItemSelectedListener)
    : ArrayAdapter<String>(context, resource, items) {
    private val itemsAll: ArrayList<String> = items.clone() as ArrayList<String>
    private val suggestions: ArrayList<String> = ArrayList()
    private var disableFilter: Boolean = false


    fun disableFilter(disableFilter: Boolean) {
        this.disableFilter = disableFilter
    }
    override fun getFilter(): Filter {
        return nameFilter
    }

    private val nameFilter: Filter = object : Filter() {
        override fun convertResultToString(resultValue: Any): String {
            return resultValue as String
        }

        @SuppressLint("DefaultLocale")
        override fun performFiltering(constraint: CharSequence): FilterResults {
            return run {
                suggestions.clear()
                for (item in itemsAll) {
                    if (item.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(item)
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = suggestions
                filterResults.count = suggestions.size
                filterResults
            }
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {

            if (disableFilter) {
                clear()
                for (item in itemsAll) {
                    add(item)
                    notifyDataSetChanged()
                }
            } else {
                val filteredList = results.values as ArrayList<String>
                if (results.count > 0) {
                    clear()
                    for (item in filteredList) {
                        add(item)
                    }
                    notifyDataSetChanged()
                }
            }


        }
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.listitem_autocomplete, parent, false)
        }
        val item = items[position]
        val lblName = convertView!!.findViewById<View>(R.id.tvView) as TextView
        lblName.text = item
        convertView.findViewById<View>(R.id.layParent).setOnClickListener { mListener.onItemSelected(item) }
        return convertView
    }


    interface ItemSelectedListener {
        fun onItemSelected(item: String)
    }
}