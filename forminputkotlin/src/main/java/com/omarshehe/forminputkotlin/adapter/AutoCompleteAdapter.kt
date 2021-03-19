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
import com.omarshehe.forminputkotlin.interfaces.ItemSelectedListener
import com.omarshehe.forminputkotlin.utils.isNotTrue
import com.omarshehe.forminputkotlin.utils.isTrue
import java.util.*

class AutoCompleteAdapter(
    context: Context,
    resource: Int,
    private val items: List<String>,
    private val mListener: ItemSelectedListener
) : ArrayAdapter<String>(context, resource, items) {
    val itemsAll: MutableList<String> = items.toMutableList()
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
                itemsAll.forEach{
                    if (it.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(it)
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = suggestions
                filterResults.count = suggestions.size
                filterResults
            }
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            disableFilter.isTrue{
                clear()
                itemsAll.forEach{
                    add(it)
                    notifyDataSetChanged()
                }
            }.isNotTrue {
                val filteredList = results.values as MutableList<*>
                if (results.count > 0) {
                    clear()
                    filteredList.forEach { it as String
                        add(it)
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

}