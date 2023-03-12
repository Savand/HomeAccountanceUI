package com.fedsav.homeaccountance

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.fedsav.homeaccountance.adapters.PurchaseItemAdapter
import com.fedsav.homeaccountance.dto.PurchaseItemDto
import com.fedsav.homeaccountance.service.PurchasesRetrofitAPI
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //allow internet execute in the main thread
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val purchaseItemList = getPurchaseItemList()
        // access the listView from xml file
        val mListView = findViewById<ListView>(R.id.item_list)
        val adapter = PurchaseItemAdapter(
            this,
            purchaseItemList
        )

        mListView.adapter = adapter


        val addItemBtn: Button = findViewById(R.id.add_item_btn)

        addItemBtn.setOnClickListener {
            val intent = Intent(this, AddModifyPurchaseActivity::class.java)
            startActivity(intent)
            finish()
        }


        mListView.setOnItemClickListener { parent, view, position, id ->
            val purchaseItemDto = purchaseItemList?.get(position)
            // The item that was clicked
            val intent = Intent(this, AddModifyPurchaseActivity::class.java)
            intent.putExtra("purchaseItemDto", purchaseItemDto)
            startActivity(intent)
            finish()
        }
    }

    private fun getPurchaseItemList(): List<PurchaseItemDto>? {

        val retrofit = Retrofit.Builder()
            .baseUrl(
                "${getString(R.string._server_protocol)}://${getString(R.string._server_address)}:${
                    getString(
                        R.string._server_port
                    )
                }/homeaccountance/"
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val retrofitAPI = retrofit.create(PurchasesRetrofitAPI::class.java)

        val call : Call<List<PurchaseItemDto>?>? = retrofitAPI.getPurchaseItemList()

        return call!!.execute().body()
    }

}