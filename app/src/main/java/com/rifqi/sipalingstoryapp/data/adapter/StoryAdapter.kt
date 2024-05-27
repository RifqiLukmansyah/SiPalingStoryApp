package com.rifqi.sipalingstoryapp.data.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rifqi.sipalingstoryapp.R
import com.rifqi.sipalingstoryapp.data.model.Story
import com.rifqi.sipalingstoryapp.databinding.ItemstoryBinding
import com.rifqi.sipalingstoryapp.ui.detail.DetailActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StoryAdapter (
    private var items: List<Story>
) : RecyclerView.Adapter<StoryAdapter.HomeViewHolder>() {
    class HomeViewHolder(private val binding: ItemstoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: Story) {
            binding.apply {

                tvUserName.text = user.name
                tvDescription.text = user.description
                tvUploadAt.text = user.createdAt.getTimeAgo(root.context)

                Glide.with(root)
                    .load(user.photoUrl)
                    .into(imgStory)

                root.setOnClickListener {
                    val intent = Intent(root.context, DetailActivity::class.java)
                    intent.putExtra("user", user)

                    val optionCompact: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            root.context as Activity,
                            Pair(tvUserName, "shared_name"),
                            Pair(imgStory, "shared_image"),
                            Pair(tvUploadAt, "shared_created"),
                            Pair(tvDescription, "shared_desc"),
                        )

                    root.context.startActivity(intent, optionCompact.toBundle())

                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val binding = ItemstoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun updateItem(newList: List<Story>) {
        val diffResult = DiffUtil.calculateDiff(myDiffCB(items, newList))
        items = newList
        diffResult.dispatchUpdatesTo(this)
    }

    companion object {
        fun myDiffCB(oldList: List<Story>, newList: List<Story>) =
            object : DiffUtil.Callback() {
                override fun getOldListSize(): Int {
                    return oldList.size
                }

                override fun getNewListSize(): Int {
                    return newList.size
                }

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return oldList[oldItemPosition].id == newList[newItemPosition].id
                }

                override fun areContentsTheSame(
                    oldItemPosition: Int,
                    newItemPosition: Int
                ): Boolean {
                    return oldList[oldItemPosition] == newList[newItemPosition]
                }

            }

    }

}

fun String.getTimeAgo(context: Context): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    val date = dateFormat.parse(this)
    val now = Date()
    val seconds = ((now.time - (date?.time ?: Date().time)) / 1000).toInt()
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24
    return when {
        days > 0 -> context.getString(R.string.label_day, days)
        hours > 0 -> context.getString(R.string.label_hour, hours)
        minutes > 0 -> context.getString(R.string.label_minutes, minutes)
        else -> context.getString(R.string.label_seconds, seconds)
    }
}