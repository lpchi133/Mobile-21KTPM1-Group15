package com.example.angodafake.Adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Html
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.angodafake.R
import com.example.angodafake.db.Voucher
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class VoucherHotelAdapter(private val context: Context, private var voucher: MutableList<Voucher>) : RecyclerView.Adapter<VoucherHotelAdapter.MyViewHolder>() {
    var onItemClick: ((Voucher) -> Unit)? = null
    private var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(voucher: Voucher)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val voucherItem = LayoutInflater.from(parent.context).inflate(R.layout.custom_voucher_hotel_adapter, parent, false)
        return MyViewHolder(voucherItem)
    }

    override fun getItemCount(): Int {
        return voucher.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = voucher[position]

        val max_discount = format1(currentItem.max_discount!!)
        val money_discount = format1(currentItem.money_discount!!)
        val limit_price = format1(currentItem.limit_price!!)

        holder.name.text = format(currentItem.percentage.toString(), max_discount, money_discount)
        holder.limit.text = format2(limit_price)
        holder.quantity.text = currentItem.quantity.toString()

        holder.btnSee.setOnClickListener {
            val dialog = Dialog(context)
            val inflater = LayoutInflater.from(context)
            val dialogView = inflater.inflate(R.layout.custom_my_voucher_detail, null)

            val percentageDialog: TextView = dialogView.findViewById(R.id.percentage)
            val limit_priceDialog: TextView = dialogView.findViewById(R.id.limited_price)
            val max_discountDialog: TextView = dialogView.findViewById(R.id.max_discount)
            val quantityDialog: TextView = dialogView.findViewById(R.id.quantity)
            val textView: TextView = dialogView.findViewById(R.id.textView4)
            val useVoucher: Button = dialogView.findViewById(R.id.button)

            quantityDialog.text = currentItem.quantity.toString()
            limit_priceDialog.text = format3(currentItem.limit_price!!)

            if (currentItem.money_discount == 0.0) {
                percentageDialog.text = "Up to ${currentItem.percentage}%"
                max_discountDialog.text = format3(currentItem.max_discount!!)
            } else {
                percentageDialog.text = "Giảm giá ${format3(currentItem.money_discount!!)}"
                max_discountDialog.visibility = View.GONE
                textView.visibility = View.GONE
            }

            useVoucher.setOnClickListener {
                onItemClick?.invoke(currentItem)
                dialog.dismiss()
            }

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(dialogView)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            dialog.show()
        }

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(currentItem)
        }
    }

    fun format(percentage: String, max_discount: String, money_discount: String): Spanned? {
        val temp: String = if (money_discount == "0") {
            "Giảm $percentage% Giảm tối đa &#8363;$max_discount"
        } else {
            "Giảm &#8363;$money_discount"
        }

        return Html.fromHtml(temp, Html.FROM_HTML_MODE_COMPACT)
    }

    fun format1(money: Double): String {
        val formatSymbols = DecimalFormatSymbols()
        formatSymbols.groupingSeparator = '.'

        val decimalFormat = DecimalFormat("#,##0", formatSymbols)
        return decimalFormat.format(money)
    }

    fun format2(limit_price: String): Spanned? {
        val temp = "Đơn tối thiểu &#8363;$limit_price"
        return Html.fromHtml(temp, Html.FROM_HTML_MODE_COMPACT)
    }

    fun format3(amount: Double): String {
        val suffixes = listOf("", "k", "k", "k", "k", "k")
        val number = amount.toLong()

        val digits = (number.toString().length - 1) / 3

        val formattedAmount = DecimalFormat("#,##0.#").format(amount / Math.pow(1000.0, digits.toDouble()))
        return "$formattedAmount${suffixes[digits.toInt()]}"
    }
    class MyViewHolder(voucherItem: View) : RecyclerView.ViewHolder(voucherItem) {
        val name: TextView = voucherItem.findViewById(R.id.voucher_name)
        val limit: TextView = voucherItem.findViewById(R.id.voucher_limited)
        val quantity: TextView = voucherItem.findViewById(R.id.voucher_quantity)
        val btnSee: Button = voucherItem.findViewById(R.id.seedetail)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<Voucher>) {
        voucher = newList.toMutableList()
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: VoucherHotelAdapter.OnItemClickListener) {
        this.listener = listener
    }
}