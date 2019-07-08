package com.example.redditclient.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.redditclient.R
import com.example.redditclient.utils.beGone
import kotlinx.android.synthetic.main.post_list_item.view.*
import java.util.*
import java.util.concurrent.TimeUnit

class PostAdapter(
    private val onClick: (PostAdapterItem) -> Unit
) : RecyclerView.Adapter<PostAdapter.VH>() {

    var postsList: MutableList<PostAdapterItem> = mutableListOf()

    override fun onCreateViewHolder(container: ViewGroup, viewType: Int): VH {
        return VH(
            LayoutInflater.from(container.context).inflate(
                R.layout.post_list_item, container, false
            ))
    }

    override fun getItemCount(): Int = postsList.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(postsList[position])
    }

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: PostAdapterItem) {
            Glide.with(itemView.context)
                .load(item.thumbnail)
                .centerCrop()
                .placeholder(R.drawable.post_image_placeholder)
                .into(itemView.ivPostImage)

            itemView.tvTitle.text = item.title.orEmpty()
            itemView.tvSubreddit.text = item.subreddit.orEmpty()
            itemView.tvAuthor.text = itemView.context.getString(R.string.posted_by, item.author.orEmpty())
            showDate(item.created)
            itemView.tvComments.text = itemView.context.getString(R.string.comments, item.num_comments.toString())
            itemView.tvRating.text = item.score.toString()

            itemView.setOnClickListener { onClick.invoke(item) }
        }

        private fun showDate(createdDate: Long?) {
            if (createdDate == null) itemView.tvDate.beGone()
            else {
                try {
                    val postCreatedDate = Date(createdDate*1000L)
                    val now = Date()
                    val minutes = TimeUnit.MILLISECONDS.toMinutes(now.time - postCreatedDate.time)
                    val hours = TimeUnit.MILLISECONDS.toHours(now.time - postCreatedDate.time)
                    val days = TimeUnit.MILLISECONDS.toDays(now.time - postCreatedDate.time)

                    val dateText = when {
                        hours < 1 -> itemView.context.getString(R.string.minutes_ago, minutes.toString())
                        hours < 24 -> itemView.context.getString(R.string.hours_ago, hours.toString())
                        else -> days.toString() + " " + itemView.context.getString(R.string.days) + " " +
                                itemView.context.getString(R.string.hours_ago, hours.toString())
                    }
                    itemView.tvDate.text = dateText
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    itemView.tvDate.beGone()
                }
            }
        }
    }
}