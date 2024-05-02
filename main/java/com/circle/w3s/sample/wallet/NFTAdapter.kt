package com.circle.w3s.sample.wallet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NFTAdapter(private val nfts: List<NFT>) : RecyclerView.Adapter<NFTAdapter.NFTViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NFTViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.nft_item, parent, false)
        return NFTViewHolder(view)
    }

    override fun onBindViewHolder(holder: NFTViewHolder, position: Int) {
        val nft = nfts[position]
        holder.bind(nft)
    }

    override fun getItemCount(): Int {
        return nfts.size
    }

    inner class NFTViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageViewNFT: ImageView = itemView.findViewById(R.id.imageViewNFT)
        private val textViewNFTName: TextView = itemView.findViewById(R.id.textViewNFTName)
        private val textViewNFTDescription: TextView = itemView.findViewById(R.id.textViewNFTDescription)

        fun bind(nft: NFT) {
            imageViewNFT.setImageResource(nft.imageResId)
            textViewNFTName.text = nft.name
            textViewNFTDescription.text = nft.description
        }
    }
}
