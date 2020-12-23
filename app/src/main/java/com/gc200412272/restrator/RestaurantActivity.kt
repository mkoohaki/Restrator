package com.gc200412272.restrator

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.*
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_restaurant.*
import kotlinx.android.synthetic.main.item_comment.view.*
import kotlinx.android.synthetic.main.toolbar_main.*
import java.text.SimpleDateFormat
import java.util.*

class RestaurantActivity : AppCompatActivity() {

    // connect to Firestore
    val db = FirebaseFirestore.getInstance().collection("comments")
    private var adapter: CommentAdapter? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant)

        val successMessage = "Comment Added"
        val unSuccessMessage = "Enter information"

        val restaurantId = intent.getStringExtra("restaurantId")
        val restaurantName = intent.getStringExtra("restaurantName")

        restaurantNameTextView.text = "Restaurant " + restaurantName

        saveCommentButton.setOnClickListener {
            try {
                // caoture inputs into on instance of our Restaurant class
                val comment = Comment()
                comment.name = usernameEditText.text.toString().trim()
                comment.comment = bodyEditText.text.toString().trim()
                comment.time = getTime()
                comment.restaurantId = restaurantId


                // connect & save to Firebase. collection  will be created if it doesn't exist already
                //val db = FirebaseFirestore.getInstance().collection("comments")
                comment.id = db.document().id
                db.document(comment.id!!).set(comment)

                // show confirmation & clear inputs
                usernameEditText.setText("")
                bodyEditText.setText("")

                Toast.makeText(this, successMessage, Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(this, unSuccessMessage + " :" + e, Toast.LENGTH_LONG).show()
            }
        }

        // set our recyclerview to use LinearLayour
        commentsRecyclerView.layoutManager = LinearLayoutManager(this)


        // query the db comments of each restaurant
        //val query = db.collection("comments").whereEqualTo("restaurantId", restaurantId)
        val query = db.whereEqualTo("restaurantId", restaurantId)

        // pass query results to RecyclerAdapter for display in RecyclerView
        val options =
                FirestoreRecyclerOptions.Builder<Comment>().setQuery(query, Comment::class.java)
                        .build()

        adapter = CommentAdapter(options)

        commentsRecyclerView.adapter = adapter

        backFab.setOnClickListener {
            // navigate to Main Activity to add a Restaurant
            val intent = Intent(application, ListActivity::class.java)
            startActivity(intent)
        }

        // load the mapsactivity and pass in the restaurant name
        mapFab.setOnClickListener {
            val intent = Intent(applicationContext, MapsActivity::class.java)
            intent.putExtra("name", restaurantName + ", Barrie, ON, Canada")
            startActivity(intent)
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
                startActivity(Intent(applicationContext, ListActivity::class.java))
                return true

            }
            R.id.action_profile -> {
                startActivity(Intent(applicationContext, ProfileActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // tell adapter to start watching data for changes
    override fun onStart() {
        super.onStart()
        adapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        if (adapter != null) {
            adapter!!.stopListening()
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getTime(): String {
        val sdf = SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z")
        val currentDateandTime: String = sdf.format(Date())

        return currentDateandTime
    }

    // create inner classes needed to bind the data to the recyclerview
    private inner class CommentViewHolder internal constructor(private val view: View) :
            RecyclerView.ViewHolder(view) {}

    private inner class CommentAdapter internal constructor(options: FirestoreRecyclerOptions<Comment>) :
            FirestoreRecyclerAdapter<Comment, CommentViewHolder>(options) {
        override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
        ): CommentViewHolder {

            // inflate the item_comment.xml layout template to populate the recyclerview
            val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
            return CommentViewHolder(view)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(
                holder: CommentViewHolder,
                position: Int,
                model: Comment
        ) {
            // populate the customer name, comment, and restaurant name into the matching TextView for each comment in the list
            holder.itemView.usernameTextView.text = model.name + " :"
            holder.itemView.bodyTextView.text = model.comment // convert to float to match RatingBar.rating type

            //Restaurant selection when Recycler item touched
            holder.itemView.setOnClickListener {
                val intent = Intent(applicationContext, EditCommentActivity::class.java)
                intent.putExtra("commentId", model.id)
                intent.putExtra("comment", model.comment)
                intent.putExtra("name", model.name)
                intent.putExtra("restaurantId", model.restaurantId)

                startActivity(intent)
            }
        }
    }
}