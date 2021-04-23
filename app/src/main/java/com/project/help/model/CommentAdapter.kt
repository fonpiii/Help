package com.project.help.model

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.project.help.ConstValue
import com.project.help.R
import com.project.help.Utilities
import com.squareup.picasso.Picasso
import java.io.IOException

class CommentAdapter(
    private val commentList: List<CommentResponse>,
    private val user: UserModel,
    private val title: String
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    private lateinit var holderMaster: CommentViewHolder
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.comment_child_feed,
                parent, false)

        return CommentViewHolder(itemView)
    }

    override fun getItemCount() = commentList.size

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        var currentItem = commentList[position]

        if (currentItem.firstName + " " + currentItem.lastName == "ผู้พิการ 1") {
            holder.imageProfileFeed.setImageResource(R.drawable.user)
        }

        if (title == "SaveScore") {
            holder.layoutAssignScore.visibility = View.VISIBLE
            holder.ratingComment.rating = currentItem.scorePost.toFloat()
        }

        if (holder.ratingComment.rating != 0.0f) {
            holder.ratingComment.isEnabled = false
            holder.btnAssignScore.isEnabled = false
        }

        holder.btnAssignScore.setOnClickListener(View.OnClickListener {
            val alertDialog = SweetAlertDialog(holder.context, SweetAlertDialog.WARNING_TYPE)
            alertDialog.titleText = "คำเตือน ?"
            alertDialog.contentText = "ต้องการให้คะแนนคอมเมนต์นี้"
            alertDialog.cancelText = "ไม่"
            alertDialog.setCancelClickListener { sDialog -> sDialog.cancel() }
            alertDialog.confirmText = "ใช่"
            alertDialog.setConfirmClickListener { sDialog ->
                        updateScore(holder, currentItem, sDialog, holder.context)
                    }
            alertDialog.show()
            val btnConfirm = alertDialog.findViewById(R.id.confirm_button) as Button
            btnConfirm.setBackgroundColor(ContextCompat.getColor(holder.context!!, R.color.colorRed))
            val btnCancel = alertDialog.findViewById(R.id.cancel_button) as Button
            btnCancel.setBackgroundColor(ContextCompat.getColor(holder.context!!, R.color.lightBlack))
        })

        var databaseRating = FirebaseDatabase.getInstance().getReference("User")
        databaseRating.orderByKey().equalTo(currentItem.createBy).get().addOnSuccessListener { result ->
            var userRating = UserModel()
            for (data in result.children) {
                userRating = data.getValue(UserModel::class.java)!!
            }
            if (userRating.userType == ConstValue.UserType_Disabled) {
                holder.ratingUserFeed.rating = userRating.scoreDisabled.toFloat()
            } else if (userRating.userType == ConstValue.UserType_Volunteer) {
                holder.ratingUserFeed.rating = userRating.scoreVolunteer.toFloat()
            }

        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }

//        holder.imageProfileFeed.setImageResource(currentItem.imageProfile)
        holder.txtUsernameFeed.text = currentItem.firstName + " " + currentItem.lastName
        holder.postDetailFeed.text = currentItem.commentDesc
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

    private fun updateScore(holder: CommentViewHolder, currentItem: CommentResponse, sDialog: SweetAlertDialog, context: Context?) {
        var databaseComment = FirebaseDatabase.getInstance().getReference("Comments")
        databaseComment.child(currentItem.id).child("scorePost").setValue(holder.ratingComment.rating).addOnSuccessListener {
            var databaseUser = FirebaseDatabase.getInstance().getReference("User")
            var score = if (holder.ratingUserFeed.rating == 0.0f) {
                holder.ratingComment.rating
            } else {
                ((holder.ratingUserFeed.rating + holder.ratingComment.rating)/2)
            }
            databaseUser.child(currentItem.createBy).child("scoreVolunteer").setValue(score).addOnSuccessListener {
                holder.ratingComment.isEnabled = false
                holder.btnAssignScore.isEnabled = false
                sDialog.dismissWithAnimation()
                SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("ให้คะแนนเสร็จสิ้น")
                        .show()
            }
        }.addOnFailureListener{
            SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("ไม่สามารถให้คะแนนได้")
                    .show()
            sDialog.dismissWithAnimation()
        }
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
        val ratingComment: RatingBar = itemView.findViewById(R.id.ratingComment)
        val imagePostFeed: ImageView = itemView.findViewById(R.id.imgPostDetail_Feed)
        val videoPostFeed: VideoView = itemView.findViewById(R.id.videoPostDetail_Feed)
        val timeStampFeed: TextView = itemView.findViewById(R.id.timeStamp_Feed)
        val btnAssignScore: Button = itemView.findViewById(R.id.btnAssignScore)
        val layoutAssignScore: GridLayout = itemView.findViewById(R.id.layout_assignScore)
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