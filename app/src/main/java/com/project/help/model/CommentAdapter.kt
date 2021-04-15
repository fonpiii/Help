package com.project.help.model

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.project.help.CommentActivity
import com.project.help.R
import com.project.help.Utilities
import com.project.help.disabled.PostAdapter
import com.project.help.disabled.model.PostDetailsResponse
import com.squareup.picasso.Picasso
import java.io.IOException

class CommentAdapter(private val commentList: List<CommentResponse>, private val user: UserModel) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {
    private lateinit var holderMaster: CommentViewHolder

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.comment_child_feed,
                parent, false)

        return CommentViewHolder(itemView)
    }

    override fun getItemCount() = commentList.size

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        var currentItem = commentList[position]

//        holder.imageProfileFeed.setImageResource(currentItem.imageProfile)
        holder.imageProfileFeed.setImageResource(R.drawable.helplogo)
        holder.txtUsernameFeed.text = currentItem.firstName + " " + currentItem.lastName
        holder.postDetailFeed.text = currentItem.commentDesc
        holder.ratingUserFeed.rating = currentItem.rating.toFloat()
//        holder.categoryFeed.text = currentItem.categorys
        holder.timeStampFeed.text = Utilities.Converter.convertTimeToPostDetails(currentItem.createDate)

        if (currentItem.imageUrl != "") {
            holder.imagePostFeed.visibility = View.VISIBLE
            Picasso.get().load(currentItem.imageUrl).into(holder.imagePostFeed)
        } else {
            holder.imagePostFeed.visibility = View.GONE
        }

        if (currentItem.videoUrl != "") {
            var mediaController = MediaController(holder.itemView.context)
            holder.videoPostFeed.setMediaController(mediaController)
            mediaController.setAnchorView(holder.videoPostFeed)
            holder.videoPostFeed.visibility = View.VISIBLE
            holder.videoPostFeed.setVideoPath(currentItem.videoUrl)
            holder.videoPostFeed.start()

            holder.videoPostFeed.setOnPreparedListener(MediaPlayer.OnPreparedListener {
                it.setOnVideoSizeChangedListener(MediaPlayer.OnVideoSizeChangedListener { _, _, _ ->
                    mediaController = MediaController(holder.itemView.context)
                    holder.videoPostFeed.setMediaController(mediaController)
                    mediaController.setAnchorView(holder.videoPostFeed)
                })
            })
        } else {
            holder.videoPostFeed.visibility = View.GONE
        }

        if (currentItem.audioUrl != "") {
            holder.cardAudio.visibility = View.VISIBLE

            holder.imgViewPlay.setOnClickListener(View.OnClickListener {
                holderMaster = holder
                if (!holder.isPlaying) {
                    holder.isPlaying = true
                    startPlaying(currentItem.audioUrl)
                } else {
                    holder.isPlaying = false
                    stopPlaying()
                }
            })

        } else {
            holder.cardAudio.visibility = View.GONE
        }
    }

    private fun stopPlaying() {
        try {
            holderMaster.mPlayer!!.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        holderMaster.mPlayer = null
        //showing the play button
        holderMaster.imgViewPlay.setImageResource(R.drawable.play_button)
    }

    private fun startPlaying(audioUrl: String) {
        holderMaster.mPlayer = MediaPlayer()
        try {
            holderMaster.mPlayer!!.setDataSource(audioUrl)
            holderMaster.mPlayer!!.prepare()
            holderMaster.mPlayer!!.start()
        } catch (e: IOException) {
            Log.e("LOG_TAG", "prepare() failed")
        }

        //making the imageView pause button
        holderMaster.imgViewPlay.setImageResource(R.drawable.pause)

        holderMaster.seekBar.progress = holderMaster.lastProgress
        holderMaster.mPlayer!!.seekTo(holderMaster.lastProgress)
        holderMaster.seekBar.max = holderMaster.mPlayer!!.duration
        seekBarUpdate()
//        chronometer.start()

        holderMaster.mPlayer!!.setOnCompletionListener(MediaPlayer.OnCompletionListener {
            holderMaster.imgViewPlay.setImageResource(R.drawable.play_button)
            holderMaster.isPlaying = false
//            chronometer.stop()
//            chronometer.base = SystemClock.elapsedRealtime()
            holderMaster.mPlayer!!.seekTo(0)
        })

        holderMaster.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (holderMaster.mPlayer != null && fromUser) {
                    holderMaster.mPlayer!!.seekTo(progress)
//                    chronometer.base = SystemClock.elapsedRealtime() - mPlayer!!.currentPosition
                    holderMaster.lastProgress = progress
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    private fun seekBarUpdate() {
        if (holderMaster.mPlayer != null) {
            val mCurrentPosition = holderMaster.mPlayer!!.currentPosition
            holderMaster.seekBar.progress = mCurrentPosition
            holderMaster.lastProgress = mCurrentPosition
        }
        holderMaster.mHandler.postDelayed(runnable, 100)
    }

    private var runnable: Runnable = Runnable { seekBarUpdate() }

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageProfileFeed: ImageView = itemView.findViewById(R.id.imgProfile_Feed)
        val txtUsernameFeed: TextView = itemView.findViewById(R.id.txtUsername_Feed)
        val postDetailFeed: TextView = itemView.findViewById(R.id.postDetail_Feed)
        val ratingUserFeed: RatingBar = itemView.findViewById(R.id.ratingUser_Feed)
        val imagePostFeed: ImageView = itemView.findViewById(R.id.imgPostDetail_Feed)
        val videoPostFeed: VideoView = itemView.findViewById(R.id.videoPostDetail_Feed)
        val timeStampFeed: TextView = itemView.findViewById(R.id.timeStamp_Feed)
        val cardAudio: LinearLayout = itemView.findViewById(R.id.cardAudio_Feed)
        val seekBar: SeekBar = itemView.findViewById(R.id.seekBar_Feed)
        val imgViewPlay: ImageView = itemView.findViewById(R.id.imgViewPlay_Feed)
        val context: Context? = itemView.context
        var lastProgress = 0
        var isPlaying = false
        var mPlayer: MediaPlayer? = null
        val mHandler = Handler()
    }
}