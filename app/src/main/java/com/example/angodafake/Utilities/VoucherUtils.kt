package com.example.angodafake.Utilities

import com.example.angodafake.db.Voucher
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

object VoucherUtils {
    private lateinit var database: DatabaseReference

    init {
        database = Firebase.database.reference
    }

    fun getAllVouchers(ownerID: String, listener: (List<Voucher>) -> Unit) {
        val vouchersQuery = database.child("vouchers")

        vouchersQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val vouchersList = mutableListOf<Voucher>()
                for (voucherSnapshot in dataSnapshot.children) {
                    val voucher = voucherSnapshot.getValue(Voucher::class.java)
                    if (voucher?.ID_Hotel == ownerID) {
                        voucher.ID = voucherSnapshot.key
                        voucher.let { vouchersList.add(it) }
                    }
                }
                listener(vouchersList)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}