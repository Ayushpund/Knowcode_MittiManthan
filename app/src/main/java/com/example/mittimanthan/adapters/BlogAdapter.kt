package com.example.mittimanthan.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.mittimanthan.R
import com.example.mittimanthan.models.BlogPost

class BlogAdapter(
    private val blogs: List<BlogPost>,
    private val onBlogClick: (BlogPost) -> Unit
) : RecyclerView.Adapter<BlogAdapter.BlogViewHolder>() {

    class BlogViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardView: CardView = view.findViewById(R.id.blogCard)
        val image: ImageView = view.findViewById(R.id.blogImage)
        val title: TextView = view.findViewById(R.id.blogTitle)
        val description: TextView = view.findViewById(R.id.blogDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_blog, parent, false)
        return BlogViewHolder(view)
    }

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        val blog = blogs[position]
        holder.image.setImageResource(blog.imageResId)
        holder.title.text = blog.title
        holder.description.text = blog.description
        holder.cardView.setOnClickListener { onBlogClick(blog) }
    }

    override fun getItemCount() = blogs.size
} 