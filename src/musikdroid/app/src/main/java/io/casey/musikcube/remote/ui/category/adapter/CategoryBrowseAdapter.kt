package io.casey.musikcube.remote.ui.category.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.casey.musikcube.remote.R
import io.casey.musikcube.remote.service.websocket.model.ICategoryValue
import io.casey.musikcube.remote.ui.category.constant.NavigationType
import io.casey.musikcube.remote.ui.shared.extension.fallback
import io.casey.musikcube.remote.ui.shared.extension.getColorCompat
import io.casey.musikcube.remote.ui.shared.mixin.PlaybackMixin

class CategoryBrowseAdapter(private val listener: EventListener,
                            private val playback: PlaybackMixin,
                            private val navigationType: NavigationType,
                            private val category: String)
    : RecyclerView.Adapter<CategoryBrowseAdapter.ViewHolder>()
{
    interface EventListener {
        fun onItemClicked(value: ICategoryValue)
        fun onActionClicked(view: View, value: ICategoryValue)
    }

    private var model: List<ICategoryValue> = ArrayList()

    internal fun setModel(model: List<ICategoryValue>?) {
        this.model = model ?: ArrayList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.simple_list_item, parent, false)
        val action = view.findViewById<View>(R.id.action)
        view.setOnClickListener({ v -> listener.onItemClicked(v.tag as ICategoryValue) })
        action.setOnClickListener({ v -> listener.onActionClicked(v, v.tag as ICategoryValue) })
        return ViewHolder(view, playback, navigationType, category)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(model[position])
    }

    override fun getItemCount(): Int = model.size

    class ViewHolder internal constructor(
            itemView: View,
            private val playback: PlaybackMixin,
            private val navigationType: NavigationType,
            private val category: String) : RecyclerView.ViewHolder(itemView)
    {
        private val title: TextView = itemView.findViewById(R.id.title)
        private val action: View = itemView.findViewById(R.id.action)

        init {
            itemView.findViewById<View>(R.id.subtitle).visibility = View.GONE
        }

        internal fun bind(categoryValue: ICategoryValue) {
            action.tag = categoryValue
            action.visibility = if (navigationType == NavigationType.Select) View.GONE else View.VISIBLE

            val playing = playback.service.playingTrack
            val playingId = playing.getCategoryId(category)

            var titleColor = R.color.theme_foreground
            if (playingId > 0 && categoryValue.id == playingId) {
                titleColor = R.color.theme_green
            }

            title.text = fallback(categoryValue.value, R.string.unknown_value)
            title.setTextColor(getColorCompat(titleColor))
            itemView.tag = categoryValue
        }
    }
}