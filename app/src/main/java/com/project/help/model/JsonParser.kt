package com.project.help.model

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class JsonParser {
    private fun parseJsonObject(obj: JSONObject): HashMap<String, String> {
        var dataList = HashMap<String, String>()
        try {
            var name = obj.getString("name")
            var latitude = obj.getJSONObject("geometry")
                .getJSONObject("location").getString("lat")

            var longitude = obj.getJSONObject("geometry")
                .getJSONObject("location").getString("lng")

            var addr = obj.getString("vicinity")


            if (obj.has("rating")) {
                var rating = obj.getString("rating")
                dataList["rating"] = rating
            }

            dataList["name"] = name
            dataList["vicinity"] = addr
            dataList["lat"] = latitude
            dataList["lng"] = longitude
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return dataList
    }

    private fun parseJsonArray(jsonArray: JSONArray): List<HashMap<String, String>> {
        var dataList = ArrayList<HashMap<String, String>>()
        for (i in 0 until jsonArray.length()) {
            var data = parseJsonObject(jsonArray.get(i) as JSONObject)
            dataList.add(data)
        }
        return dataList
    }

    public fun parseResult(obj: JSONObject): List<HashMap<String, String>> {
        var jsonArray = JSONArray()

        try {
            jsonArray = obj.getJSONArray("results")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return parseJsonArray(jsonArray)
    }
}