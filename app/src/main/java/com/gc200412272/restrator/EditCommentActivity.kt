package com.gc200412272.restrator

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_restaurant.*
import kotlinx.android.synthetic.main.toolbar_main.*
import java.text.SimpleDateFormat
import java.util.*

class EditCommentActivity : AppCompatActivity() {

    val db = FirebaseFirestore.getInstance().collection("comments")
    val database = FirebaseDatabase.getInstance().reference
    var mFirebaseDatabase: DatabaseReference? = null
    var mFirebaseInstance: FirebaseDatabase? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_comment)

        val commentId = intent.getStringExtra("commentId")
        val comment = intent.getStringExtra("comment")
        val name = intent.getStringExtra("name")
        val restaurantId = intent.getStringExtra("restaurantId")

        val successMessage = "Comment updated successfully"
        val unSuccessMessage_information = "Enter information"
        val unSuccessMessage_error = "Comment did not updated"

        usernameEditText.setText(name)
        bodyEditText.setText(comment)

        mFirebaseInstance = FirebaseDatabase.getInstance()

        // get reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance!!.getReference("comments")

        saveCommentButton.setOnClickListener {
            try {
                var user = usernameEditText.text
                var review = bodyEditText.text

                val commentRef = db.document(commentId.toString())
                if (user.isNotEmpty() && review.isNotEmpty()) {

                    commentRef
                        .update(
                            mapOf(
                                "name" to user.toString(),
                                "comment" to review.toString(),
                                "time" to getTime()
                            )
                        )
                        .addOnSuccessListener {
                            Toast.makeText(applicationContext, successMessage, Toast.LENGTH_SHORT)
                                .show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                applicationContext,
                                unSuccessMessage_error,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                } else {
                    Toast.makeText(
                        applicationContext,
                        unSuccessMessage_information,
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                Toast.makeText(this, "Error :" + e, Toast.LENGTH_LONG).show()
            }
        }
        backFab.setOnClickListener {
            finish()
        }

        //instantiate toolbar
        setSupportActionBar(topToolbar)
    }

    // 2 overrides to display menu and handle its action
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        // inflate the main menu to add the items to the toolbar
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // navigate based on which menu item was clicked
        when (item.itemId) {
            R.id.action_add -> {
                startActivity(Intent(applicationContext, MainActivity::class.java))
                return true
            }
            R.id.action_list -> {
                return true

            }
            R.id.action_profile -> {
                startActivity(Intent(applicationContext, ProfileActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("SimpleDateFormat")
    fun getTime(): String {
        val sdf = SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z")
        val currentDateandTime: String = sdf.format(Date())

        return currentDateandTime
    }
}