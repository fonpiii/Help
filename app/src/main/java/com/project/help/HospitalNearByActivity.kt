package com.project.help

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.project.help.model.HospitalAdapter
import com.project.help.model.HospitalModel
import com.project.help.model.JsonParser
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.math.RoundingMode
import java.net.URL
import java.text.DecimalFormat
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class HospitalNearByActivity : AppCompatActivity() {

    private lateinit var shimmer: ShimmerFrameLayout
    private lateinit var recyclerFeed: RecyclerView
    private lateinit var iconLeft: ImageView
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var currentLat = 0.0
    private var currentLong = 0.0
    var context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hospital_near_by)

        recyclerFeed = findViewById(R.id.recycler_feed)
        shimmer = findViewById(R.id.shimmerFrameLayout)

        shimmer.startShimmerAnimation()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        setToolbar()

        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            var tasks = fusedLocationProviderClient.lastLocation.addOnSuccessListener() {
                getCurrentLocation(it)
            }
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 44)
        }
    }

    private fun setToolbar() {
        iconLeft = findViewById(R.id.iconLeft)
        iconLeft.setOnClickListener(View.OnClickListener {
            finish()
        })
    }

    private fun getCurrentLocation(location: Location?) {
        if (location != null) {
            currentLat = location.latitude
            currentLong = location.longitude

            findHospital()
        }
    }

    private fun findHospital() {
        var url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" + "?location=" + currentLat +
                ","+ currentLong + "&radius=5000" + "&type=" + "hospital" + "&sensor=true" +
                "&key=" + resources.getString(R.string.google_map_key)

        PlaceTask().execute(url)
    }

    inner class PlaceTask(): AsyncTask<String, Int,  String>() {
        override fun doInBackground(vararg params: String?): String {
            var data = ""
            try {
                data = downloadUrl(params[0])
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return data
        }

        override fun onPostExecute(result: String?) {
            ParserTask().execute(result)
        }

        @Throws(IOException::class)
        private fun downloadUrl(string: String?): String {
            var url = URL(string)
            var connection = url.openConnection()
            connection.connect()
            var stream = connection.getInputStream()
            var reader = BufferedReader(InputStreamReader(stream))
            var builder = StringBuilder()

            run {
                var line = reader.readLine()
                while (line != null) {
                    builder.append(line)
                    line = reader.readLine()
                }
            }

            var data = builder.toString()
            reader.close()
            return data
        }

    }

    inner class ParserTask(): AsyncTask<String, Int, List<HashMap<String, String>>>() {
        override fun doInBackground(vararg params: String?): List<HashMap<String, String>> {
            var jsonParser = JsonParser()
            var mapList: List<HashMap<String, String>> = emptyList()
            var obj = JSONObject()
            try {
                obj = JSONObject(params[0])
                mapList = jsonParser.parseResult(obj)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return mapList
        }

        override fun onPostExecute(result: List<HashMap<String, String>>?) {
//            map.clear()
            var hospitals = ArrayList<HospitalModel>()
            for (i in result!!.indices) {
                var hospital = HospitalModel()
                var hashMapList = result[i]
                var targetLat = hashMapList["lat"]!!.toDouble()
                var targetLng = hashMapList["lng"]!!.toDouble()
                hospital.name = hashMapList["name"]!!
                hospital.currentLat = currentLat.toString()
                hospital.currentLong = currentLong.toString()
                hospital.targetLat = targetLat.toString()
                hospital.targetLng = targetLng.toString()

                if (!hashMapList["rating"].isNullOrEmpty()) {
                    hospital.rating = hashMapList["rating"]!!
                } else {
                    hospital.rating = "0.0"
                }

                hospital.address = hashMapList["vicinity"]!!
                hospital.distance = calculationByDistance(currentLat, currentLong, targetLat, targetLng).toString()
                hospitals.add(hospital)
            }

            // Sort hospitals by km
            hospitals.sortByDescending { it.distance }
            hospitals.reverse()

            recyclerFeed.adapter = HospitalAdapter(hospitals)
            recyclerFeed.layoutManager = LinearLayoutManager(context)
            recyclerFeed.setHasFixedSize(true)
            closeShimmer()
        }

    }

    private fun closeShimmer() {
        shimmer.stopShimmerAnimation()
        shimmer.visibility = View.GONE
        recyclerFeed.visibility = View.VISIBLE
    }

    fun calculationByDistance(currentLat: Double, currentLong: Double, targetLat: Double, targetLng: Double): Double {

        val results = FloatArray(1)
        Location.distanceBetween(currentLat, currentLong,
                targetLat, targetLng,
                results)
        var df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        var distance = (results[0].toDouble()) / 1000
        return df.format(distance).toDouble()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == 44) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    var task = fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                        getCurrentLocation(it)
                    }
                }
            }
        }
    }
}