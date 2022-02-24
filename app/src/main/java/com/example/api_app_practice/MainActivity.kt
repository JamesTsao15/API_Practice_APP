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
class MainActivity : AppCompatActivity() {
    private lateinit var btn_query: Button
    private fun showListdialog(airData: AirData){
        val item= arrayOfNulls<String>(airData.records.size)
        airData.records.forEachIndexed { index, record ->
           Log.e("JAMES","地區:${record.sitename},狀態:${record.status}")
        }
    }
    private fun sendRequest(){
        val req=Request.Builder()
            .url("https://data.epa.gov.tw/api/v2/aqx_p_432?offset=0&limit=100&api_key=a1d60db2-c3c5-4a33-bf8c-a2a17aa8261d")
            .build()
        OkHttpClient().newCall(req).enqueue(object:Callback{
            override fun onResponse(call: Call, response: Response) {
                val json=response.body?.string()
                val json_delete_first_block=json?.replace("<br />\n" +
                        "<b>Warning</b>:  get_browser(): browscap ini directive not set in <b>/var/www/html/Ap/app/Module/Office/JsonModuleImpl.php</b> on line <b>49</b><br />","")
                if (json_delete_first_block != null && json_delete_first_block!="錯誤訊息: 請勿頻繁索取資料")
                {
//                    Log.e("JAMES",json_delete_first_block)
                    val airData=Gson().fromJson(json_delete_first_block,AirData::class.java)
//                    Log.e("JAMES",airData.records.size.toString())
                    showListdialog(airData)
                }
                else if(json_delete_first_block=="錯誤訊息: 請勿頻繁索取資料"){
                    runOnUiThread {
                        Toast.makeText(this@MainActivity,"資料已為最新，請稍後在更新!!",Toast.LENGTH_SHORT).show()
                    }
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