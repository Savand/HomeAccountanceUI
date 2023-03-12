package com.fedsav.homeaccountance

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.fedsav.homeaccountance.dto.DateFormatter
import com.fedsav.homeaccountance.dto.PurchaseItemDto
import com.fedsav.homeaccountance.service.PurchasesRetrofitAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class AddModifyPurchaseActivity : AppCompatActivity() {

    private lateinit var dateEditText: TextView
    private lateinit var dateFormatter: DateFormatter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_modify_purchase_activity)

        val goBackBtn: Button = findViewById(R.id.go_back_btn)

        goBackBtn.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        val nameEditText: EditText = findViewById(R.id.name_edit_text)
        val costEditText: EditText = findViewById(R.id.cost_edit_text)

        val sendButton: Button = findViewById(R.id.send_button)

        val purchaseItemDtoFromIntent =
            intent.getSerializableExtra("purchaseItemDto") as? PurchaseItemDto

        if (purchaseItemDtoFromIntent != null) {
            val viewText: TextView = findViewById(R.id.card_info_text)
            viewText.text = getString(R.string.edit_purchase)
            sendButton.text = getString(R.string.edit_purchase_btn_txt)
            nameEditText.setText(purchaseItemDtoFromIntent.name)
            costEditText.setText((purchaseItemDtoFromIntent.cost.toString().toDouble() / 100).toString())
            val dateTime = purchaseItemDtoFromIntent.dateTime
            dateFormatter = DateFormatter(dateTime)
        } else {
            dateFormatter = DateFormatter(null)
        }

        dateEditText = findViewById(R.id.date_edit_text)


        dateEditText.text = dateFormatter.getPresentationDate()

        dateEditText.setOnClickListener {
            clickDatePicker()
        }

        sendButton.setOnClickListener {

            //validate fields not empty
            val nameText = nameEditText.text.toString()

            if (nameText.isEmpty()) {
                Toast.makeText(this, "Please enter an Item name", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val costText = costEditText.text.toString()
            if (costText.isEmpty()) {
                Toast.makeText(this, "Please enter an Item cost", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            dateFormatter.modifyDateFromPresentation(dateEditText.text.toString())


            val purchaseItemDto = PurchaseItemDto(
                purchaseItemDtoFromIntent?.id,
                dateFormatter.getJsonDate(),
                nameText,
                (costText.toDouble() * 100).toInt()
            )

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

            val call: Call<Map<String, String>?>? = retrofitAPI.postPurchaseItem(purchaseItemDto)
            call!!.enqueue(
                object : Callback<Map<String, String>?> {
                    override fun onResponse(
                        call: Call<Map<String, String>?>,
                        response: Response<Map<String, String>?>
                    ) {
                        if (response.isSuccessful) {
                            nameEditText.text.clear()
                            costEditText.text.clear()
                            Toast.makeText(
                                this@AddModifyPurchaseActivity,
                                "PurchaseItem has been saved with id ${response.body()?.get("id")}",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                this@AddModifyPurchaseActivity,
                                "Client Error ${response.errorBody()}",
                                Toast.LENGTH_LONG
                            ).show()

                        }
                    }

                    override fun onFailure(call: Call<Map<String, String>?>, t: Throwable) {
                        Toast.makeText(
                            this@AddModifyPurchaseActivity,
                            "Error sending ${t.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }


            )
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun clickDatePicker() {

        val dpd = DatePickerDialog(
            this, { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                dateFormatter.modifyDateFromInput(
                    selectedYear,
                    selectedMonth,
                    selectedDayOfMonth
                )
                dateEditText.text = dateFormatter.getPresentationDate()
            },
            dateFormatter.year, dateFormatter.month, dateFormatter.day
        )

        dpd.show()
    }
}