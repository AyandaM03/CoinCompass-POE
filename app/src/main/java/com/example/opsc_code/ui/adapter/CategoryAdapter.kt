package com.example.opsc_code.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.opsc_code.R
import com.example.opsc_code.data.model.Category

/**
 * RecyclerView Adapter for displaying expense categories.
 * 
 * @property categories List of categories to display
 * @property onDeleteClick Callback invoked when the delete button is clicked
 */
class CategoryAdapter(
    private var categories: List<Category> = emptyList(),
    private val onDeleteClick: ((Category) -> Unit)? = null
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    /**
     * ViewHolder for category items.
     */
    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCategoryName: TextView = itemView.findViewById(R.id.tvCategoryName)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDeleteCategory)

        /**
         * Binds a category to the view.
         * 
         * @param category The category to bind
         */
        fun bind(category: Category) {
            tvCategoryName.text = category.name
            btnDelete.setOnClickListener {
                onDeleteClick?.invoke(category)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount(): Int = categories.size

    /**
     * Updates the adapter's data with a new list of categories.
     * 
     * @param newCategories The new list of categories to display
     */
    fun updateCategories(newCategories: List<Category>) {
        categories = newCategories
        notifyDataSetChanged()
    }

    /**
     * Returns the current list of categories.
     * 
     * @return The current list of categories
     */
    fun getCategories(): List<Category> = categories
}
