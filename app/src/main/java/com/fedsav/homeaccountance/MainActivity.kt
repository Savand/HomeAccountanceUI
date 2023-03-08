package com.fedsav.homeaccountance

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.fedsav.homeaccountance.dto.DatePickerHelper
import com.fedsav.homeaccountance.dto.SendPurchaseItemRequest
import com.fedsav.homeaccountance.service.PurchasesRetrofitAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var costEditText: EditText
    private lateinit var dateEditText: TextView
    private lateinit var datePickerHelper: DatePickerHelper

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        nameEditText = findViewById(R.id.name_edit_text)
        costEditText = findViewById(R.id.cost_edit_text)
        dateEditText = findViewById(R.id.date_edit_text)
        datePickerHelper = DatePickerHelper()

        dateEditText.text = datePickerHelper.getPresentationDate()

        dateEditText.setOnClickListener {
            clickDatePicker()
        }

        val sendButton: Button = findViewById(R.id.send_button)

        sendButton.setOnClickListener {
            // Perform action when send button is clicked
            datePickerHelper.modifyDateFromPresentation(dateEditText.text.toString())
            val sendPurchaseItemRequest = SendPurchaseItemRequest(
                datePickerHelper.getJsonDate(),
                nameEditText.text.toString(),
                Integer.parseInt(costEditText.text.toString())
            )
            print("\n\n\n$sendPurchaseItemRequest\n\n\n")


            val retrofit = Retrofit.Builder()
                .baseUrl("${getString(R.string._server_protocol)}://${getString(R.string._server_address)}:${getString(R.string._server_port)}/homeaccountance/")
                // as we are sending data in json format so
                // we have to add Gson converter factory
                .addConverterFactory(GsonConverterFactory.create())
                // at last we are building our retrofit builder.
                .build()
            val retrofitAPI = retrofit.create(PurchasesRetrofitAPI::class.java)

            val call: Call<Map<String, String>?>? = retrofitAPI.postData(sendPurchaseItemRequest)


            call!!.enqueue(
                object : Callback<Map<String, String>?> {
                    override fun onResponse(
                        call: Call<Map<String, String>?>,
                        response: Response<Map<String, String>?>
                    ) {
                        if(response.isSuccessful){
                            nameEditText.text.clear()
                            costEditText.text.clear()
                            Toast.makeText(this@MainActivity, "PurchaseItem has been saved with id ${response.body()?.get("id")}", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this@MainActivity, "Client Error ${response.errorBody()}", Toast.LENGTH_LONG).show()

                        }
                    }

                    override fun onFailure(call: Call<Map<String, String>?>, t: Throwable) {
                        Toast.makeText(this@MainActivity, "Error sending ${t.message}", Toast.LENGTH_LONG).show()
                        println(t)
                    }
                }



            )
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun clickDatePicker() {

        val dpd = DatePickerDialog(
            this, { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                datePickerHelper.modifyDateFromInput(selectedYear, selectedMonth, selectedDayOfMonth)
                dateEditText.text = datePickerHelper.getPresentationDate()
            },
            datePickerHelper.year, datePickerHelper.month, datePickerHelper.day
        )

        dpd.show()
    }
}