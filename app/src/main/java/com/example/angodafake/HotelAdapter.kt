package com.example.angodafake

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.angodafake.db.Bookmarks
import com.example.angodafake.db.Hotel
import com.example.angodafake.db.HotelDatabase
import com.example.angodafake.db.Picture

class HotelAdapter(private val context: Context, private var hotels: List<Hotel>, private var idUser: Int) : RecyclerView.Adapter<HotelAdapter.ViewHolder>() {
    private lateinit var hotel_db: HotelDatabase
    private lateinit var Picture: Picture
    private var listener: HotelAdapter.OnItemClickListener? = null

    // Interface cho sự kiện click
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        val nameTextView = listItemView.findViewById<TextView>(R.id.hotelName)
        val locationTextView = listItemView.findViewById<TextView>(R.id.Location)
        val pointView = listItemView.findViewById<TextView>(R.id.point)
        val img: ImageView = listItemView.findViewById(R.id.imageView)
        val rateStatus: TextView = listItemView.findViewById(R.id.rateStatus)
        val quaCM: TextView = listItemView.findViewById(R.id.quaCM)
        val convenience: TextView = listItemView.findViewById(R.id.convenience)
        val buttonFav: Button = listItemView.findViewById(R.id.fav)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotelAdapter.ViewHolder {

        val context = parent.context
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val hotelsView = inflater.inflate(R.layout.custom_hotel, parent, false)
        // Return a new holder instance
        return ViewHolder(hotelsView)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hotel : Hotel = hotels[position]

        hotel_db = HotelDatabase.getInstance(context)
        Picture = hotel_db.PictureDAO().getPictureByHotelID(hotel.id)

        val idPicture = context.resources.getIdentifier(Picture.picture, "drawable", context.packageName)
        holder.img.setImageResource(idPicture)
        holder.nameTextView.text = hotel.name
        holder.locationTextView.text = hotel.locationDetail
        holder.pointView.text = hotel.point.toString()
        holder.quaCM.text = hotel.description
        holder.convenience.text = hotel.convenience

        // Khởi tạo SharedPreferences
        val sharedPref = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        // Lấy trạng thái yêu thích từ SharedPreferences, mặc định là false
        val isFavourite = sharedPref.getBoolean("isFavourite_${hotel.id}", false)
        // Thiết lập trạng thái của nút từ SharedPreferences
        holder.buttonFav.isSelected = isFavourite

        if(hotel_db.BookmarkDAO().checkIfExistHotelId(hotel.id) > 0){
            val drawableResId = context.resources.getIdentifier("baseline_favorite_24", "drawable", context.packageName)
            holder.buttonFav.setBackgroundResource(drawableResId)
        }

        holder.buttonFav.setOnClickListener {
            // Đảo ngược trạng thái khi nút được nhấn
            holder.buttonFav.isSelected = !holder.buttonFav.isSelected
            // Lưu trạng thái vào SharedPreferences
            val editor = sharedPref.edit()
            editor.putBoolean("isFavourite_${hotel.id}", holder.buttonFav.isSelected)
            editor.apply()
            if(holder.buttonFav.isSelected){
                val drawableResId = context.resources.getIdentifier("baseline_favorite_24", "drawable", context.packageName)
                holder.buttonFav.setBackgroundResource(drawableResId)
                val saveBookmark = Bookmarks(idUser, hotel.id)
                hotel_db.BookmarkDAO().insertBookmark(saveBookmark)
            } else {
                val drawableResId = context.resources.getIdentifier("baseline_favorite", "drawable", context.packageName)
                holder.buttonFav.setBackgroundResource(drawableResId)
                hotel_db.BookmarkDAO().deleteBookmarkByHotelId(hotel.id)
            }
        }

        holder.rateStatus.text = when (hotel.point.toInt()){
            in 0 until 3 -> { "Cực tệ" }
            in 3 until 5 -> { "Tệ" }
            in 5 until 6 -> { "Trung bình" }
            in 6 until 8 -> { "Tốt" }
            in 8 until 9 -> { "Rất tốt" }
            else -> { "Tuyệt vời" }
        }
    }

    override fun getItemCount(): Int {
        return hotels.size
    }

    fun getStudents(): List<Hotel> {
        return hotels
    }

    fun updateDataGradually(newData: List<Hotel>) {
        hotels = newData
        notifyDataSetChanged()
    }
}