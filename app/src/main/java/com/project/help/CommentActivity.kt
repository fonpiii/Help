package com.project.help

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.project.help.disabled.DisabledMainActivity
import com.project.help.disabled.PostAdapter
import com.project.help.disabled.model.PostDetailsRequest
import com.project.help.disabled.model.PostDetailsResponse
import com.project.help.model.CommentAdapter
import com.project.help.model.CommentRequest
import com.project.help.model.CommentResponse
import com.project.help.model.UserModel
import com.project.help.volunteer.VolunteerMainActivity
import com.squareup.picasso.Picasso
import java.io.IOException
import java.util.*

class CommentActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var iconLeft: ImageView
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var imageProfileFeed: ImageView
    private lateinit var txtUsernameFeed: TextView
    private lateinit var postDetailFeed: TextView
    private lateinit var countCommentFeed: TextView
    private lateinit var ratingUserFeed: RatingBar
    private lateinit var imagePostFeed: ImageView
    private lateinit var videoPostFeed: VideoView
    private lateinit var categoryFeed: TextView
    private lateinit var timeStampFeed: TextView
    private lateinit var cardAudio: LinearLayout
    private lateinit var recyclerFeed: RecyclerView
    private lateinit var seekBar: SeekBar
    private lateinit var imgViewPlay: ImageView
    private lateinit var editComment: EditText
    private lateinit var btnSpeechToText: ImageButton
    private lateinit var btnSendMessage: ImageButton
    private lateinit var shimmer: ShimmerFrameLayout
    private lateinit var user: UserModel
    private lateinit var postId: String
    private lateinit var dialog: Dialog
    var lastProgress = 0
    var isPlaying = false
    var mPlayer: MediaPlayer? = null
    val mHandler = Handler()
    private val RQ_SPEECH_REC = 102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)

        imageProfileFeed = findViewById(R.id.imgProfile_Feed)
        txtUsernameFeed = findViewById(R.id.txtUsername_Feed)
        postDetailFeed = findViewById(R.id.postDetail_Feed)
        countCommentFeed = findViewById(R.id.countComment_Feed)
        ratingUserFeed = findViewById(R.id.ratingUser_Feed)
        imagePostFeed = findViewById(R.id.imgPostDetail_Feed)
        videoPostFeed = findViewById(R.id.videoPostDetail_Feed)
        categoryFeed = findViewById(R.id.category_Feed)
        timeStampFeed = findViewById(R.id.timeStamp_Feed)
        cardAudio = findViewById(R.id.cardAudio_Feed)
        seekBar = findViewById(R.id.seekBar_Feed)
        imgViewPlay= findViewById(R.id.imgViewPlay_Feed)
        editComment= findViewById(R.id.editComment)
        recyclerFeed = findViewById(R.id.recycler_feed)
        btnSpeechToText= findViewById(R.id.btnSpeechToText)
        btnSendMessage= findViewById(R.id.btnSendMessage)
        shimmer = findViewById(R.id.shimmerFrameLayout)

        btnSpeechToText.setOnClickListener(this)
        btnSendMessage.setOnClickListener(this)

        //region On init
        setFirebaseDatabase()
        setToolbar()

        if (!intent.getStringExtra("PostDetailId").toString().isNullOrEmpty()) {
            postId = intent.getStringExtra("PostDetailId").toString()
            getPostDetailsById(postId)
        }

        if ((intent.getParcelableExtra("User") as? UserModel) != null) {
            user = (intent.getParcelableExtra("User") as? UserModel)!!
        }
        //endregion On init
    }

    private fun setFirebaseDatabase() {
        database = FirebaseDatabase.getInstance()
        reference = database.getReference("PostDetails")
    }

    private fun getPostDetailsById(postId: String) {
        reference.orderByKey().equalTo(postId).get().addOnSuccessListener { result ->
            var postDetail = PostDetailsResponse()
            for (data in result.children) {
                postDetail = data.getValue(PostDetailsResponse::class.java)!!
            }
            setPostDetail(postDetail)
            getComments()
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
    }

    private fun setPostDetail(postDetail: PostDetailsResponse) {
        imageProfileFeed.setImageResource(R.drawable.helplogo)
        txtUsernameFeed.text = postDetail.firstName + " " + postDetail.lastName
        postDetailFeed.text = postDetail.postDesc
        ratingUserFeed.rating = postDetail.rating.toFloat()
        categoryFeed.text = postDetail.categorys
        timeStampFeed.text = Utilities.Converter.convertTimeToPostDetails(postDetail.createDate)

        if (postDetail.imageUrl != "") {
            imagePostFeed.visibility = View.VISIBLE
            Picasso.get().load(postDetail.imageUrl).into(imagePostFeed)
        } else {
            imagePostFeed.visibility = View.GONE
        }

        if (postDetail.videoUrl != "") {
            var mediaController = MediaController(this)
            videoPostFeed.setMediaController(mediaController)
            mediaController.setAnchorView(videoPostFeed)
            videoPostFeed.visibility = View.VISIBLE
            videoPostFeed.setVideoPath(postDetail.videoUrl)
            videoPostFeed.start()

            videoPostFeed.setOnPreparedListener(MediaPlayer.OnPreparedListener {
                it.setOnVideoSizeChangedListener(MediaPlayer.OnVideoSizeChangedListener { _, _, _ ->
                    mediaController = MediaController(this)
                    videoPostFeed.setMediaController(mediaController)
                    mediaController.setAnchorView(videoPostFeed)
                })
            })
        } else {
            videoPostFeed.visibility = View.GONE
        }

        if (postDetail.audioUrl != "") {
            cardAudio.visibility = View.VISIBLE

            imgViewPlay.setOnClickListener(View.OnClickListener {
                if (!isPlaying) {
                    isPlaying = true
                    startPlaying(postDetail.audioUrl)
                } else {
                    isPlaying = false
                    stopPlaying()
                }
            })

        } else {
            cardAudio.visibility = View.GONE
        }
    }

    private fun stopPlaying() {
        try {
            mPlayer!!.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mPlayer = null
        //showing the play button
        imgViewPlay.setImageResource(R.drawable.play_button)
    }

    private fun startPlaying(audioUrl: String) {
        mPlayer = MediaPlayer()
        try {
            mPlayer!!.setDataSource(audioUrl)
            mPlayer!!.prepare()
            mPlayer!!.start()
        } catch (e: IOException) {
            Log.e("LOG_TAG", "prepare() failed")
        }

        //making the imageView pause button
        imgViewPlay.setImageResource(R.drawable.pause)

        seekBar.progress = lastProgress
        mPlayer!!.seekTo(lastProgress)
        seekBar.max = mPlayer!!.duration
        seekBarUpdate()
//        chronometer.start()

        mPlayer!!.setOnCompletionListener(MediaPlayer.OnCompletionListener {
            imgViewPlay.setImageResource(R.drawable.play_button)
            isPlaying = false
//            chronometer.stop()
//            chronometer.base = SystemClock.elapsedRealtime()
            mPlayer!!.seekTo(0)
        })

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (mPlayer != null && fromUser) {
                    mPlayer!!.seekTo(progress)
//                    chronometer.base = SystemClock.elapsedRealtime() - mPlayer!!.currentPosition
                    lastProgress = progress
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    private fun seekBarUpdate() {
        if (mPlayer != null) {
            val mCurrentPosition = mPlayer!!.currentPosition
            seekBar.progress = mCurrentPosition
            lastProgress = mCurrentPosition
        }
        mHandler.postDelayed(runnable, 100)
    }

    private var runnable: Runnable = Runnable { seekBarUpdate() }

    private fun setToolbar() {
        iconLeft = findViewById(R.id.iconLeft)
        iconLeft.setOnClickListener(this)
    }

    private fun getPermissionToRecordAudio(recordAudioRequestCode: Int) {
        // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid checking the build version since Context.checkSelfPermission(...) is only available in Marshmallow
        // 2) Always check for permission (even if permission has already been granted) since the user can revoke permissions at any time through Settings
        if (recordAudioRequestCode == RQ_SPEECH_REC) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), recordAudioRequestCode)
            } else {
                askSpeechInput()
            }
        }
    }

    private fun askSpeechInput() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Toast.makeText(this, "Speech recognition is not available", Toast.LENGTH_SHORT).show()
        } else {
            val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something!")
            startActivityForResult(i, RQ_SPEECH_REC)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RQ_SPEECH_REC && resultCode == Activity.RESULT_OK) {
            val result:ArrayList<String>? = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            editComment.text = Editable.Factory.getInstance().newEditable(result?.get(0).toString())
        }
    }

    // Callback with the request from calling requestPermissions(...)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == RQ_SPEECH_REC) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                askSpeechInput()
                Toast.makeText(this, "Record Audio permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "You must give permissions to use this app. App is exiting.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendMessageToDb() {
        var database = FirebaseDatabase.getInstance().getReference("Comments")
        var commentModel = CommentRequest(user.firstName!!, user.lastName!!, user.profileUrl!!,
                "", "", "", "", "",
                "", editComment.text.toString(), postId, user.scoreDisabled,
                ServerValue.TIMESTAMP, user.userId, ServerValue.TIMESTAMP, user.userId)
        var id = database.push().key
        database.child(id!!).setValue(commentModel).addOnCompleteListener {
            getComments()
            editComment.text = null
            dialog.dismiss()
            getComments()
        }
    }

    private fun getComments() {
        if (!postId.isNullOrEmpty()) {
            shimmer.startShimmerAnimation()
            var database = FirebaseDatabase.getInstance().getReference("Comments")
            database.get().addOnSuccessListener { result ->
                var comments = ArrayList<CommentResponse>()
                for (data in result.children) {
                    var comment: CommentResponse = data.getValue(CommentResponse::class.java)!!
                    comments.add(comment)
                }

                // Sort postDetails by date
                comments.sortByDescending { it.createDate }

                if (comments.size != 0) {
                    recyclerFeed.adapter = CommentAdapter(comments, user)
                    recyclerFeed.layoutManager = LinearLayoutManager(this)
                    recyclerFeed.setHasFixedSize(true)
                    closeShimmer()
                } else {
                    closeShimmer()
                }
            }.addOnFailureListener{
                Log.e("firebase", "Error getting data", it)
            }
        }
    }

    private fun closeShimmer() {
        shimmer.stopShimmerAnimation()
        shimmer.visibility = View.GONE
        recyclerFeed.visibility = View.VISIBLE
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnSendMessage -> {
                dialog = Utilities.Alert.loadingDialog(this, "กำลังแสดงความคิดเห็น")
                dialog.apply { show() }
                sendMessageToDb()
            }
            R.id.btnSpeechToText -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    getPermissionToRecordAudio(RQ_SPEECH_REC)
                }
            }
            R.id.iconLeft -> finish()
        }
    }
}