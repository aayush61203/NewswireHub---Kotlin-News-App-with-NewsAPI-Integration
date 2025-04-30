package com.example.news_wirehub

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class NewsAdapter(private val listener: NewsItemClicked): RecyclerView.Adapter<NewsViewHolder>() {

    private val items: ArrayList<News> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_style, parent, false)
        val viewHolder = NewsViewHolder(view)
        view.setOnClickListener{
            listener.onItemClicked(items[viewHolder.absoluteAdapterPosition])
        }
        view.setOnLongClickListener {
            listener.onItemLongClicked(items[viewHolder.absoluteAdapterPosition])
        }
        return viewHolder
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val currentItem = items[position]
        holder.titleView.text = currentItem.title
        holder.author.text = currentItem.author
        Glide.with(holder.itemView.context).load(currentItem.imageUrl).into(holder.image)
        holder.publishedAt.text = currentItem.publishedAt
        holder.source.text = currentItem.source.toString()
    }


    @SuppressLint("NotifyDataSetChanged")
    fun updateNews(updated: ArrayList<News>){
        items.clear()
        items.addAll(updated)

        notifyDataSetChanged()
    }
}


class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val titleView: TextView = itemView.findViewById(R.id.headlines)
    val image: ImageView = itemView.findViewById(R.id.thumb)
    val author: TextView = itemView.findViewById(R.id.author)
    val publishedAt: TextView = itemView.findViewById(R.id.publishedAt)
    val source: TextView = itemView.findViewById(R.id.source )

}

interface NewsItemClicked {
    fun onItemClicked(item: News)
    fun onItemLongClicked(item: News): Boolean
}


