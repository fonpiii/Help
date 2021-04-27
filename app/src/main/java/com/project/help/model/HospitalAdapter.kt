package com.project.help.model

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.project.help.R


class HospitalAdapter(
        private val hospitalList: List<HospitalModel>
) : RecyclerView.Adapter<HospitalAdapter.HospitalViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HospitalViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.hospital_feed,
                parent, false)

        return HospitalViewHolder(itemView)
    }

    override fun getItemCount() = hospitalList.size

    override fun onBindViewHolder(holder: HospitalViewHolder, position: Int) {
        var currentItem = hospitalList[position]

        holder.hospitalName.text = currentItem.name
        holder.address.text = currentItem.address
        holder.rating.text = "rating: " + currentItem.rating
        holder.distance.text = currentItem.distance + " km"

        holder.parentLayout.setOnClickListener(View.OnClickListener {
            var uri = "http://maps.google.com/maps?saddr=" + currentItem.currentLat + "," +
                    currentItem.currentLong + "&daddr=" + currentItem.targetLat + "," + currentItem.currentLong
            val gmmIntentUri: Uri = Uri.parse(uri)
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            holder.context?.startActivity(mapIntent)
        })
    }

    class HospitalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val hospitalName: TextView = itemView.findViewById(R.id.hospitalName)
        val address: TextView = itemView.findViewById(R.id.address)
        val rating: TextView = itemView.findViewById(R.id.rating)
        val distance: TextView = itemView.findViewById(R.id.distance)
        val parentLayout: GridLayout = itemView.findViewById(R.id.parentLayout)
        val context: Context? = itemView.context
    }
}