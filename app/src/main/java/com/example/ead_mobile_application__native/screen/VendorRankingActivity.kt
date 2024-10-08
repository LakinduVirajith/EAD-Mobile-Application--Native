package com.example.ead_mobile_application__native.screen

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ead_mobile_application__native.R
import com.example.ead_mobile_application__native.service.VendorApiService
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class VendorRankingActivity : AppCompatActivity() {
    // API SERVICE INSTANCE
    private val vendorApiService = VendorApiService(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_vendor_ranking)

        // SET THE STATUS BAR BACKGROUND COLOR
        window.statusBarColor = getColor(R.color.black20)

        // SET UP THE TOOLBAR
        val toolbar: Toolbar = findViewById(R.id.vrToolbar)
        setSupportActionBar(toolbar)

        // ENABLE THE DEFAULT BACK BUTTON IN THE ACTION BAR
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // HANDLE WINDOW INSETS FOR EDGE-TO-EDGE DISPLAY
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.vendorRankingActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // INITIALIZE VIEWS
        val commentEditText = findViewById<EditText>(R.id.vrComment)
        val ratingBar = findViewById<RatingBar>(R.id.vrRatingBar)
        val btnSend = findViewById<Button>(R.id.vrBtnSend)

        // SETUP VIEW DETAILS
        val orderItemId = intent.getStringExtra("order_item_id")
        setupViewDetails(orderItemId)

        // HANDLE SEND BUTTON CLICK
        btnSend.setOnClickListener {
            handleSend(orderItemId, commentEditText, ratingBar)
        }
    }

    // HANDLE THE DEFAULT BACK BUTTON PRESS
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    // FUNCTION TO SETUP VIEW DETAILS
    private fun setupViewDetails(orderItemId: String?) {
        // GET THE ORDER ID FROM INTENT AND SET THE ORDER ID
        supportActionBar?.title = ""
        val orderItemIDTextView: TextView = findViewById(R.id.vrOrderItemID)
        orderItemIDTextView.text = orderItemId
    }

    // FUNCTION TO HANDLE SEND LOGIC
    private fun handleSend(orderItemId: String?, commentEditText: EditText, ratingBar: RatingBar) {
        // RETRIEVE INPUT VALUES
        val comment = commentEditText.text.toString()
        val rating = ratingBar.rating.toString()

        // CHECK IF BOTH FIELDS ARE FILLED
        if (comment.isEmpty()) {
            Toast.makeText(this, "Please Fill Comment Fields", Toast.LENGTH_SHORT).show()
        } else if(rating.isEmpty()){
            Toast.makeText(this, "Please Select Ratings", Toast.LENGTH_SHORT).show()
        }else {
            // GET TODAY'S DATE AND FORMAT IT TO "2000-10-31" STYLE
            val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            // CALL RANKING METHOD FROM API SERVICE
            vendorApiService.addRanking(orderItemId, comment, rating.toInt(), todayDate) { status, message ->
                runOnUiThread {
                    // DISPLAY FEEDBACK BASED ON RESPONSE
                    if (status != null) {
                        when (status) {
                            200 -> {
                                Toast.makeText(this, "$status: $message", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, OrderActivity::class.java)).also { finish() }
                            }
                            401 -> {
                                Toast.makeText(this, "$status: Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, OrderActivity::class.java)).also { finish() }
                            }
                            else -> {
                                Toast.makeText(this, "$status: $message", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this, "Sending failed: Please check your internet connection.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}