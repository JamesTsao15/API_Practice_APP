package com.example.api_app_practice

import android.icu.text.AlphabeticIndex
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException
class MyObject{
    var record: Array<Record> = arrayOf()
    class Record{
       var siteName=""
       var status=""
    }
}
class MainActivity : AppCompatActivity() {
    private lateinit var btn_query: Button
    private fun sendRequest(){
        val req=Request.Builder()
            .url("https://data.epa.gov.tw/api/v2/aqx_p_432?offset=0&limit=100&api_key=a1d60db2-c3c5-4a33-bf8c-a2a17aa8261d")
            .build()
        OkHttpClient().newCall(req).enqueue(object:Callback{
            override fun onResponse(call: Call, response: Response) {
                val json=response.body?.string()
                val json_delete_first_block=json?.replace("<br />\n" +
                        "<b>Warning</b>:  get_browser(): browscap ini directive not set in <b>/var/www/html/Ap/app/Module/Office/JsonModuleImpl.php</b> on line <b>49</b><br />","")
                if (json_delete_first_block != null) {
                    Log.e("JAMES",json_delete_first_block)
                    val myObject:MyObject=Gson().fromJson(json_delete_first_block,MyObject::class.java)
                    Log.e("JAMES",myObject.record.size.toString())
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    btn_query.isEnabled=true
                    Toast.makeText(this@MainActivity,"查詢失敗$e",Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_query=findViewById(R.id.button_query)
        btn_query.setOnClickListener {
            sendRequest()
        }
    }
}