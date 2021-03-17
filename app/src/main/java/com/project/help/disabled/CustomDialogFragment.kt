package com.project.help.disabled

import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Chronometer
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.project.help.R
import java.io.File
import java.io.IOException

class CustomDialogFragment: DialogFragment(), View.OnClickListener {

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
    private lateinit var rootView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragmen_custom_dialog, container, false)

        imgBtRecord = rootView.findViewById(R.id.imgBtRecord)
        imgBtStop = rootView.findViewById(R.id.imgBtStop)
        chronometer = rootView.findViewById(R.id.chronometer)
        llRecorder = rootView.findViewById(R.id.llRecorder)

        imgBtRecord.setOnClickListener(this)
        imgBtStop.setOnClickListener(this)

        rootView.findViewById<ImageButton>(R.id.btnCancel).setOnClickListener {
            dismiss()
        }

        return rootView
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.imgBtRecord -> {
                prepareRecording()
                startRecording()
            }
            R.id.imgBtStop -> {
                prepareStop()
                stopRecording()
            }
        }
    }

    private fun prepareStop() {
        TransitionManager.beginDelayedTransition(llRecorder)
        imgBtRecord.visibility = View.VISIBLE
        imgBtStop.visibility = View.GONE
//        llPlay.visibility = View.VISIBLE
    }


    private fun prepareRecording() {
        TransitionManager.beginDelayedTransition(llRecorder)
        imgBtRecord.visibility = View.GONE
        imgBtStop.visibility = View.VISIBLE
//        llPlay.visibility = View.GONE
    }

    private fun startRecording() {
        mRecorder = MediaRecorder()
        mRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        mRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        val root = android.os.Environment.getExternalStorageDirectory()
        val file = File(root.absolutePath + "/AndroidCodility/Audios")
        if (!file.exists()) {
            file.mkdirs()
        }

        fileName = root.absolutePath + "/AndroidCodility/Audios/" + (System.currentTimeMillis().toString() + ".mp3")
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
        dismiss()
        Toast.makeText(rootView.context, "Recording saved successfully.", Toast.LENGTH_SHORT).show()
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
//        imgViewPlay.setImageResource(R.drawable.ic_play_circle)
        chronometer.stop()
    }
}