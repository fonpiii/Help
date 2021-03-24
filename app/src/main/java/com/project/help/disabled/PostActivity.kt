package com.project.help.disabled

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.text.Editable
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.project.help.R
import com.project.help.disabled.model.PostDetails
import java.io.File
import java.io.IOException
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

@RequiresApi(api = Build.VERSION_CODES.M)
class PostActivity : AppCompatActivity(), View.OnClickListener {

    //region Global variable
    private lateinit var iconLeft: ImageView
    private lateinit var editPost: EditText
    private lateinit var btnPost: TextView
    private lateinit var btnIsAdvice: LinearLayout
    private lateinit var btnImgGallery: LinearLayout
    private lateinit var btnAudio: LinearLayout
    private lateinit var btnSpeechToText: LinearLayout
    private lateinit var itemAdvice: TextView
    private lateinit var layout_imagePost: LinearLayout
    private lateinit var imagePost: ImageView
    private lateinit var layout_menu_bottom: LinearLayout
    private lateinit var layout_audio_bottom: LinearLayout
    private lateinit var btnCloseImg: ImageButton
    private var mRecorder: MediaRecorder? = null
    private var mPlayer: MediaPlayer? = null
    private var fileName: String? = null
    private var lastProgress = 0
    private val mHandler = Handler()
    private var isPlaying = false
    private lateinit var imgBtRecord: ImageButton
    private lateinit var imgBtStop: ImageButton
    private lateinit var chronometer: Chronometer
    private lateinit var llRecorder: LinearLayout
    private lateinit var layout_audio: LinearLayout
    private lateinit var seekBar: SeekBar
    private lateinit var imgViewPlay: ImageView
    private lateinit var btnCancelAudio: ImageButton
    private lateinit var btnCloseAudio: ImageButton
    private val pickImage = 100
    private var imageUri: Uri? = null
    private var isAdvice: Boolean = false
    private val RECORD_AUDIO_REQUEST_CODE = 101
    private val RQ_SPEECH_REC = 102
    private lateinit var spinnerCategory: Spinner
    //endregion Global variable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        //region Set variable
        editPost = findViewById(R.id.editPost_Post)
        btnPost = findViewById(R.id.btnPost_Post)
        btnIsAdvice = findViewById(R.id.btnIsAdvice)
        itemAdvice = findViewById(R.id.itemAdvice)
        btnImgGallery = findViewById(R.id.btnImgGallery)
        btnAudio = findViewById(R.id.btnAudio)
        btnSpeechToText = findViewById(R.id.btnSpeechToText)
        layout_imagePost = findViewById(R.id.layout_imagePost)
        imagePost = findViewById(R.id.imagePost)
        layout_menu_bottom = findViewById(R.id.layout_menu_bottom)
        layout_audio_bottom = findViewById(R.id.layout_audio_bottom)
        imgBtRecord = findViewById(R.id.imgBtRecord)
        imgBtStop = findViewById(R.id.imgBtStop)
        chronometer = findViewById(R.id.chronometer)
        llRecorder = findViewById(R.id.llRecorder)
        layout_audio = findViewById(R.id.layout_audio)
        seekBar = findViewById(R.id.seekBar)
        imgViewPlay = findViewById(R.id.imgViewPlay)
        btnCancelAudio = findViewById(R.id.btnCancelAudio)
        btnCloseImg = findViewById(R.id.btnCloseImg)
        btnCloseAudio = findViewById(R.id.btnCloseAudio)
        spinnerCategory = findViewById(R.id.spinnerCategory)

        imgBtRecord.setOnClickListener(this)
        imgBtStop.setOnClickListener(this)
        editPost.setOnClickListener(this)
        btnPost.setOnClickListener(this)
        btnSpeechToText.setOnClickListener(this)
        btnIsAdvice.setOnClickListener(this)
        btnImgGallery.setOnClickListener(this)
        btnAudio.setOnClickListener(this)
        btnCancelAudio.setOnClickListener(this)
        imgViewPlay.setOnClickListener(this)
        btnCloseImg.setOnClickListener(this)
        btnCloseAudio.setOnClickListener(this)
        //endregion Set variable

        //region On init
        setToolbar()
        setSpinnerCategory()
        //endregion On init

        editPost.doAfterTextChanged {
            if (editPost.length() > 0) {
                btnPost.setTextColor(ContextCompat.getColor(this, R.color.white));
            } else {
                btnPost.setTextColor(ContextCompat.getColor(this, R.color.lightGray));
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.editPost_Post -> {
                openSoftKeyboard(this, editPost)
            }
            R.id.btnPost_Post -> {
                if (editPost.length() > 0) {
                    val postDetail = PostDetails()
                    postDetail.txtPost = editPost.text.toString()
                    postDetail.isAdvice = isAdvice
                    val intent = launchNextScreen(this, postDetail)
                    startActivity(intent)
                    finish()
                }
            }
            R.id.btnIsAdvice -> {
                if (!isAdvice) {
                    itemAdvice.visibility = View.VISIBLE
                    isAdvice = true
                } else {
                    itemAdvice.visibility = View.GONE
                    isAdvice = false
                }
            }
            R.id.btnImgGallery -> {
                val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                startActivityForResult(gallery, pickImage)
            }
            R.id.btnAudio -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    getPermissionToRecordAudio(RECORD_AUDIO_REQUEST_CODE)
                    layout_menu_bottom.visibility = View.GONE
                    layout_audio_bottom.visibility = View.VISIBLE
                }
            }
            R.id.btnCancelAudio -> {
                layout_menu_bottom.visibility = View.VISIBLE
                layout_audio_bottom.visibility = View.GONE
            }
            R.id.imgBtRecord -> {
                prepareRecording()
                startRecording()
            }
            R.id.imgBtStop -> {
                prepareStop()
                stopRecording()
            }
            R.id.imgViewPlay -> {
                if (!isPlaying && fileName != null) {
                    isPlaying = true
                    startPlaying()
                } else {
                    isPlaying = false
                    stopPlaying()
                }
            }
            R.id.btnCloseImg -> {
                setFilesToEmpty(getString(R.string.image_type))
            }
            R.id.btnCloseAudio -> {
                setFilesToEmpty(getString(R.string.audio_type))
            }
            R.id.btnSpeechToText -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    getPermissionToRecordAudio(RQ_SPEECH_REC)
                }
            }
            R.id.iconLeft -> finish()
        }
    }

    private fun setFilesToEmpty(type: String) {
        when (type) {
            getString(R.string.image_type) -> {
                layout_imagePost.visibility = View.GONE
                imageUri = null
                imagePost.setImageURI(imageUri)
            }
            getString(R.string.audio_type) -> {
                layout_audio.visibility = View.GONE
                mRecorder = null
                File(fileName).delete()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            layout_imagePost.visibility = View.VISIBLE
            imageUri = data?.data
            imagePost.setImageURI(imageUri)
        } else if (requestCode == RQ_SPEECH_REC && resultCode == Activity.RESULT_OK) {
            val result:ArrayList<String>? = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            editPost.text = Editable.Factory.getInstance().newEditable(result?.get(0).toString())
        }
    }

    private fun setToolbar() {
        iconLeft = findViewById(R.id.iconLeft)
        iconLeft.setOnClickListener(this)
    }

    private fun launchNextScreen(context: Context, postDetails: PostDetails): Intent {
        val intent = Intent(context, DisabledMainActivity::class.java)
        intent.putExtra("PostDetail", postDetails as Serializable)
        return intent
    }

    private fun openSoftKeyboard(context: Context, view: View) {
        view.requestFocus()
        // open the soft keyboard
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun getPermissionToRecordAudio(recordAudioRequestCode: Int) {
        // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid checking the build version since Context.checkSelfPermission(...) is only available in Marshmallow
        // 2) Always check for permission (even if permission has already been granted) since the user can revoke permissions at any time through Settings
        if (recordAudioRequestCode == RECORD_AUDIO_REQUEST_CODE) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE), recordAudioRequestCode)
            }
        } else if (recordAudioRequestCode == RQ_SPEECH_REC) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), recordAudioRequestCode)
            } else {
                askSpeechInput()
            }
        }
    }

    // Callback with the request from calling requestPermissions(...)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == RECORD_AUDIO_REQUEST_CODE) {
            if (grantResults.size == 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "Record Audio permission granted", Toast.LENGTH_SHORT).show();
            } else {
                layout_menu_bottom.visibility = View.VISIBLE
                layout_audio_bottom.visibility = View.GONE
                Toast.makeText(this, "You must give permissions to use this app. App is exiting.", Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == RQ_SPEECH_REC) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                askSpeechInput()
                Toast.makeText(this, "Record Audio permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "You must give permissions to use this app. App is exiting.", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun prepareStop() {
        TransitionManager.beginDelayedTransition(llRecorder)
        imgBtRecord.visibility = View.VISIBLE
        imgBtStop.visibility = View.GONE
        layout_audio.visibility = View.VISIBLE
    }


    private fun prepareRecording() {
        TransitionManager.beginDelayedTransition(llRecorder)
        imgBtRecord.visibility = View.GONE
        imgBtStop.visibility = View.VISIBLE
        layout_audio.visibility = View.GONE
    }

    private fun startRecording() {
        mRecorder = MediaRecorder()
        mRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        mRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        val root = android.os.Environment.getExternalStorageDirectory()
//        val file = File(root.absolutePath + "/AndroidCodility/Audios")
        val file = File(this.externalCacheDir!!.absolutePath, "/your_file_name")
        if (!file.exists()) {
            file.mkdirs()
        }

//        fileName = root.absolutePath + "/AndroidCodility/Audios/" + (System.currentTimeMillis().toString() + ".mp3")
        fileName = this.externalCacheDir!!.absolutePath + "/your_file_name/" + (System.currentTimeMillis().toString() + ".mp3")
        Log.d("filename", fileName)
        mRecorder!!.setOutputFile(fileName)
        mRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

        try {
            mRecorder!!.prepare()
            mRecorder!!.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        lastProgress = 0
        seekBar.progress = 0
        stopPlaying()
        // making the imageView a stop button starting the chronometer
        chronometer.base = SystemClock.elapsedRealtime()
        chronometer.start()
    }

    private fun stopRecording() {
        try {
            mRecorder!!.stop()
            mRecorder!!.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mRecorder = null
        //starting the chronometer
        chronometer.stop()
        chronometer.base = SystemClock.elapsedRealtime()
        layout_audio.visibility = View.VISIBLE
        Toast.makeText(this, "Recording saved successfully.", Toast.LENGTH_SHORT).show()
        //showing the play button
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
        chronometer.stop()
    }

    private fun startPlaying() {
        mPlayer = MediaPlayer()
        try {
            mPlayer!!.setDataSource(fileName)
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

    private var runnable: Runnable = Runnable { seekBarUpdate() }

    private fun seekBarUpdate() {
        if (mPlayer != null) {
            val mCurrentPosition = mPlayer!!.currentPosition
            seekBar.progress = mCurrentPosition
            lastProgress = mCurrentPosition
        }
        mHandler.postDelayed(runnable, 100)
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

    private fun setSpinnerCategory() {
        var categories = arrayOf("หมวดหมู่ผู้พิการ", "การมองเห็น", "การได้ยิน", "การเคลื่อนไหวร่างกาย", "สติปัญญา",
                "ออทิสติก", "ผู้สูงอายุ")

        var adapter = ArrayAdapter(this, R.layout.color_spinner_layout, categories)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout)
        spinnerCategory.adapter = adapter

        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            }
        }
    }
}