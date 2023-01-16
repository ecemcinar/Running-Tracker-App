package com.ecemsevvalcinar.runningtracker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ecemsevvalcinar.runningtracker.db.Run
import com.example.runningtracker.R
import com.example.runningtracker.databinding.ItemRunBinding

class RunAdapter: RecyclerView.Adapter<RunAdapter.RunViewHolder>() {

    class RunViewHolder(var view: ItemRunBinding): RecyclerView.ViewHolder(view.root)


    val diffCallBack = object: DiffUtil.ItemCallback<Run>() {

        override fun areItemsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    val differ = AsyncListDiffer(this, diffCallBack)

    fun submitList(list: List<Run>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = DataBindingUtil.inflate<ItemRunBinding>(
            inflater,
            R.layout.item_run,
            parent,
            false
        )
        return RunViewHolder(view)
    }

    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
        val run = differ.currentList[position]
        holder.view.model = run
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}