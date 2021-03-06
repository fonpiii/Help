package com.project.help.disabled.model

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.project.help.ConstValue
import com.project.help.R
import com.project.help.Utilities
import com.project.help.disabled.SaveScoreDisabledActivity
import com.project.help.model.PostHelpResponse
import com.project.help.model.UserDisabledModel
import com.project.help.volunteer.SaveScoreVolunteerActivity
import com.squareup.picasso.Picasso
import java.io.IOException

class ThankYouAdapter(private val postList: List<PostDetailsResponse>, private val userDisabled: UserDisabledModel) : RecyclerView.Adapter<ThankYouAdapter.PostViewHolder>() {

    private lateinit var holderMaster: PostViewHolder

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.thankyou_feed,
            parent, false)

        return PostViewHolder(
            itemView
        )
    }

    override fun getItemCount() = postList.size

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        var currentItem = postList[position]
        var postHelp = PostHelpResponse()

        if (userDisabled.userType == ConstValue.UserType_Disabled) {
            if (currentItem.close) {
                holder.layoutStatusHelp.visibility = View.VISIBLE
                holder.layoutNotHelp.visibility = View.GONE
            } else {
                holder.layoutStatusHelp.visibility = View.GONE
                holder.layoutNotHelp.visibility = View.VISIBLE
            }
        } else if (userDisabled.userType == ConstValue.UserType_Volunteer) {
            holder.txtAssignScore.text = "ให้คะแนนผู้ขอความช่วยเหลือ \n เมื่อได้รับการช่วยเหลือเรียบร้อยแล้ว"
            holder.btnAssignScore.text = "ให้คะแนน"

            var dbAssignScore = FirebaseDatabase.getInstance().getReference("PostHelp")
            dbAssignScore.orderByChild("postDetailId").equalTo(currentItem.id).get().addOnSuccessListener { result ->
                for (data in result.children) {
                    postHelp = data.getValue(PostHelpResponse::class.java)!!
                    postHelp.id = data.key.toString()
                }
                if (postHelp.assignScore) {
                    holder.layoutStatusHelp.visibility = View.VISIBLE
                    holder.layoutNotHelp.visibility = View.GONE
                    holder.txtStatusHelp.text = "สถานะ : ให้คะแนนแล้ว"
                }

            }.addOnFailureListener{
                Log.e("firebase", "Error getting data", it)
            }
        }

        if (currentItem.advice) {
            holder.itemAdvice.visibility = View.VISIBLE
        }

        var databaseRating = FirebaseDatabase.getInstance().getReference("User")
        databaseRating.orderByKey().equalTo(currentItem.createBy).get().addOnSuccessListener { result ->
            var userRating = UserDisabledModel()
            for (data in result.children) {
                userRating = data.getValue(UserDisabledModel::class.java)!!
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
        holder.imageProfileFeed.setImageResource(R.drawable.helplogo)
        holder.txtUsernameFeed.text = currentItem.firstName + " " + currentItem.lastName
        holder.postDetailFeed.text = currentItem.postDesc
        holder.categoryFeed.text = currentItem.categorys
        holder.timeStampFeed.text = Utilities.Converter.convertTimeToPostDetails(currentItem.createDate)

        holder.btnAssignScore.setOnClickListener(View.OnClickListener {
            if (userDisabled.userType == ConstValue.UserType_Disabled) {
                var intent = Intent(holder.context, SaveScoreDisabledActivity::class.java)
                intent.putExtra("PostDetailId", currentItem.id)
                intent.putExtra("User", userDisabled)
                holder.context?.startActivity(intent)
            } else if (userDisabled.userType == ConstValue.UserType_Volunteer) {
                var intent = Intent(holder.context, SaveScoreVolunteerActivity::class.java)
                intent.putExtra("PostUserId", currentItem.createBy)
                intent.putExtra("PostHelpId", postHelp.id)
                intent.putExtra("PostDetailId", currentItem.id)
                intent.putExtra("User", userDisabled)
                holder.context?.startActivity(intent)
            }
        })

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

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageProfileFeed: ImageView = itemView.findViewById(R.id.imgProfile_Feed)
        val txtUsernameFeed: TextView = itemView.findViewById(R.id.txtUsername_Feed)
        val postDetailFeed: TextView = itemView.findViewById(R.id.postDetail_Feed)
        val ratingUserFeed: RatingBar = itemView.findViewById(R.id.ratingUser_Feed)
        val imagePostFeed: ImageView = itemView.findViewById(R.id.imgPostDetail_Feed)
        val videoPostFeed: VideoView = itemView.findViewById(R.id.videoPostDetail_Feed)
        val categoryFeed: TextView = itemView.findViewById(R.id.category_Feed)
        val timeStampFeed: TextView = itemView.findViewById(R.id.timeStamp_Feed)
        val cardAudio: LinearLayout = itemView.findViewById(R.id.cardAudio_Feed)
        val seekBar: SeekBar = itemView.findViewById(R.id.seekBar_Feed)
        val imgViewPlay: ImageView = itemView.findViewById(R.id.imgViewPlay_Feed)
        val itemAdvice: CardView = itemView.findViewById(R.id.itemAdvice)
        val btnAssignScore: Button = itemView.findViewById(R.id.btnAssignScore)
        val layoutNotHelp: GridLayout = itemView.findViewById(R.id.layoutNotHelp)
        val layoutStatusHelp: LinearLayout = itemView.findViewById(R.id.statusHelp)
        val txtAssignScore: TextView = itemView.findViewById(R.id.txtAssignScore)
        val txtStatusHelp: TextView = itemView.findViewById(R.id.txtStatusHelp)
        val context: Context? = itemView.context
        var lastProgress = 0
        var isPlaying = false
        var mPlayer: MediaPlayer? = null
        val mHandler = Handler()
    }
}