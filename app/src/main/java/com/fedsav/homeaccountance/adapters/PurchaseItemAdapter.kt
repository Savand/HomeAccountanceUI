package com.fedsav.homeaccountance.adapters

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.fedsav.homeaccountance.R
import com.fedsav.homeaccountance.dto.DateFormatter
import com.fedsav.homeaccountance.dto.PurchaseItemDto
import com.fedsav.homeaccountance.service.PurchasesRetrofitAPI
import retrofit2.Retrofit
import java.text.NumberFormat
import java.util.*

class PurchaseItemAdapter(
    private val context: Context,
    private val dataSource: List<PurchaseItemDto>?
) : BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        if (dataSource == null) {
            return 0
        }
        return dataSource.size
    }

    override fun getItem(position: Int): Any {
        if (dataSource == null) {
            return 0
        }
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Get view for row item
        val rowView = inflater.inflate(R.layout.list_purchase_item, parent, false)

        val nameTextView = rowView.findViewById(R.id.item_name) as TextView
        val costTextView = rowView.findViewById(R.id.item_cost) as TextView
//        val dateTextView = rowView.findViewById(R.id.item_date) as TextView

        val purchaseItem = getItem(position) as PurchaseItemDto

        val format: NumberFormat = NumberFormat.getCurrencyInstance()
        format.maximumFractionDigits = 2

        nameTextView.text = purchaseItem.name
        costTextView.text = format.format(purchaseItem.cost * 1.00 / 100)
//        dateTextView.text = DateFormatter(purchaseItem.dateTime).getPresentationDate()


        val removeBtn = rowView.findViewById(R.id.remove_button) as Button

        removeBtn.setOnClickListener {

            val retrofit = Retrofit.Builder()
                .baseUrl(
                    "${context.getString(R.string._server_protocol)}://${context.getString(R.string._server_address)}:${
                        context.getString(
                            R.string._server_port
                        )
                    }/homeaccountance/"
                )
                .build()

            val retrofitAPI = retrofit.create(PurchasesRetrofitAPI::class.java)

            val purchaseItemToRemove = getItem(position) as PurchaseItemDto
            retrofitAPI.removePurchaseItem(purchaseItemToRemove.id!!).execute()

            dataSource as MutableList
            dataSource.remove(purchaseItemToRemove)

            notifyDataSetChanged()

            Toast.makeText(
                parent.context,
                "Purchase item '${purchaseItemToRemove.name}' was successfully removed",
                Toast.LENGTH_SHORT
            ).show()
        }


        return rowView
    }

}