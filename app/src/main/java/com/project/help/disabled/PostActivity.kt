package com.project.help.disabled

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.project.help.R
import com.project.help.disabled.model.PostDetails
import java.io.Serializable
import com.project.help.disabled.CustomDialogFragment as CustomDialogFragment1

@RequiresApi(api = Build.VERSION_CODES.M)
class PostActivity : AppCompatActivity(), View.OnClickListener {

    //region Global variable
    private lateinit var iconLeft: ImageView
    private lateinit var editPost: EditText
    private lateinit var btnPost: TextView
    private lateinit var btnIsAdvice: LinearLayout
    private lateinit var btnImgGallery: LinearLayout
    private lateinit var btndAudio: LinearLayout
    private lateinit var itemAdvice: TextView
    private lateinit var layout_imagePost: LinearLayout
    private lateinit var imagePost: ImageView
    private lateinit var dialog: CustomDialogFragment1
    private lateinit var imgBtRecord: ImageButton
    private lateinit var imgBtStop: ImageButton
    private lateinit var chronometer: Chronometer
    private val pickImage = 100
    private var imageUri: Uri? = null
    private var isAdvice: Boolean = false
    private val RECORD_AUDIO_REQUEST_CODE = 101
    //endregion Global variable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        editPost = findViewById(R.id.editPost_Post)
        btnPost = findViewById(R.id.btnPost_Post)
        btnIsAdvice = findViewById(R.id.btnIsAdvice)
        itemAdvice = findViewById(R.id.itemAdvice)
        btnImgGallery = findViewById(R.id.btnImgGallery)
        btndAudio = findViewById(R.id.btndAudio)
        layout_imagePost = findViewById(R.id.layout_imagePost)
        imagePost = findViewById(R.id.imagePost)

        editPost.setOnClickListener(this)
        btnPost.setOnClickListener(this)
        btnIsAdvice.setOnClickListener(this)
        btnImgGallery.setOnClickListener((this))
        btndAudio.setOnClickListener((this))

        //region On init
        setToolbar()
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
                    Log.d("Test", editPost.text.toString())
                    postDetail.txtPost = editPost.text.toString()
                    postDetail.isAdvice = isAdvice
                    val intent = launchNextScreen(this, postDetail)
                    startActivity(intent)
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
            R.id.btndAudio -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    getPermissionToRecordAudio()
                    dialog = CustomDialogFragment1()
                    dialog.show(supportFragmentManager, "customDialog")
                }
            }
            R.id.iconLeft -> finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            layout_imagePost.visibility = View.VISIBLE
            imageUri = data?.data
            imagePost.setImageURI(imageUri)
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

    private fun getPermissionToRecordAudio() {
        // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid checking the build version since Context.checkSelfPermission(...) is only available in Marshmallow
        // 2) Always check for permission (even if permission has already been granted) since the user can revoke permissions at any time through Settings
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE), RECORD_AUDIO_REQUEST_CODE)
        }
    }

    // Callback with the request from calling requestPermissions(...)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == RECORD_AUDIO_REQUEST_CODE) {
            if (grantResults.size == 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "Record Audio permission granted", Toast.LENGTH_SHORT).show();
            } else if (grantResults.size == 3 && grantResults[0] != PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] != PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(this, "You must give permissions to use this app. App is exiting.", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }
    }
}