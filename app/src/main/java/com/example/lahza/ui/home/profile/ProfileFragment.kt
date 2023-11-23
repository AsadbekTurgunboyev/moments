package com.example.lahza.ui.home.profile

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.lahza.databinding.FragmentProfileBinding
import com.example.lahza.domain.models.FollowersHistoryModel
import com.example.lahza.domain.models.LoginModel
import com.example.lahza.domain.models.post.PostModel
import com.example.lahza.domain.preference.UserPreferenceManager
import com.example.lahza.ui.home.profile.adapter.ProfilePostAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date

class ProfileFragment : Fragment() {


    lateinit var viewBinding: FragmentProfileBinding
    lateinit var databaseReference: DatabaseReference
    lateinit var profileDatabaseReference: DatabaseReference
    var key: String? = null
    lateinit var userPreferenceManager: UserPreferenceManager
    val listener by lazy {
        object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val model = snapshot.getValue(LoginModel::class.java)
                viewBinding.textView2.text = "@${model?.username}"

                viewBinding.namePersonTextView.text = model?.name
            }

            override fun onCancelled(error: DatabaseError) {

            }

        }
    }
    val isFollowOrUnfollow by lazy {
        object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    val customColor = Color.parseColor("#C9CACC")
                    viewBinding.buttonFollow.setCardBackgroundColor(
                        ColorStateList.valueOf(
                            customColor
                        )
                    )
                    viewBinding.textForFollowing.text = "Unfollow"
                } else {
                    val customColor = Color.parseColor("#42A5F5")
                    viewBinding.buttonFollow.setCardBackgroundColor(
                        ColorStateList.valueOf(
                            customColor
                        )
                    )
                    viewBinding.textForFollowing.text = "Follow"
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        }
    }

    val buttonFollow by lazy {
        object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    for (childSnapshot in snapshot.children) {
                        val userKey = childSnapshot.child("user").getValue(String::class.java)
                        if (userKey == userPreferenceManager.getUserKey()) {
                            childSnapshot.ref.removeValue()
                            break // Exit the loop once the correct child is found and removed
                        }
                    }

                    key?.let { removeFollowingOwn(it) }
                    viewBinding.textForFollowing.text = "Follow"

                } else {
                    val map = HashMap<String, String>()
                    userPreferenceManager.getUserKey()?.let { map.put("user", it) }
                    key?.let {

                        addFollowingOwn(it)
                        addFollowingHistory(it)
                        databaseReference.child(it).child("followers").push().setValue(map)

                    }

                    viewBinding.textForFollowing.text = "Unfollow"
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        }
    }

    val followingCount by lazy {
        object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val number = snapshot.childrenCount
                    viewBinding.followingNumbersTextView.text = number.toString()
                } else {
                    viewBinding.followingNumbersTextView.text = "0"

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        }
    }

    val postModelList = arrayListOf<PostModel>()

    val profilePostValue by lazy {
        object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                postModelList.clear()
                for (ds in snapshot.children) {
                    val model = ds.getValue(PostModel::class.java)

                    if (model != null) {
                        postModelList.add(model)
                    }
                }
                viewBinding.photosNumberTextView.text = "${snapshot.childrenCount}"
                viewBinding.ownProfileRecy.adapter = ProfilePostAdapter(postModelList)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        }
    }
    val followersCount by lazy {
        object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val number = snapshot.childrenCount
                    viewBinding.followersNumberTextView.text = number.toString()
                } else {
                    viewBinding.followersNumberTextView.text = "0"

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        viewBinding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = arguments
        key = bundle?.getString("key")
        databaseReference = FirebaseDatabase.getInstance().getReference("users")
        profileDatabaseReference = FirebaseDatabase.getInstance().getReference("posts")
        userPreferenceManager = UserPreferenceManager(requireContext())


        if (key == null) {
            viewBinding.buttonFollow.visibility = View.GONE
            viewBinding.buttonEdit.visibility = View.VISIBLE
            userPreferenceManager.getUserKey()?.let {
                databaseReference.child(it).addListenerForSingleValueEvent(listener)
                databaseReference.child(it).child("followers").addValueEventListener(followersCount)
                profileDatabaseReference.child(it).addValueEventListener(profilePostValue)
                databaseReference.child(it).child("following").addValueEventListener(followingCount)
            }
        } else {
            viewBinding.buttonFollow.visibility = View.VISIBLE
            viewBinding.buttonEdit.visibility = View.GONE


            key?.let {
                databaseReference.child(it).addValueEventListener(listener)
                val query: Query =
                    databaseReference.child(it).child("followers").orderByChild("user")
                        .equalTo(userPreferenceManager.getUserKey())
                query.addValueEventListener(isFollowOrUnfollow)
                profileDatabaseReference.child(it).addValueEventListener(profilePostValue)

                databaseReference.child(it).child("followers").addValueEventListener(followersCount)
                databaseReference.child(it).child("following").addValueEventListener(followingCount)
            }
        }



        viewBinding.buttonFollow.setOnClickListener {
            key?.let {
                val query: Query =
                    databaseReference.child(it).child("followers").orderByChild("user")
                        .equalTo(userPreferenceManager.getUserKey())
                query.addListenerForSingleValueEvent(buttonFollow)
            }
        }
    }


    fun addFollowingOwn(key: String) {
        val map = HashMap<String, String>()
        map["user"] = key


        userPreferenceManager.getUserKey()
            ?.let {
                databaseReference.child(it).child("following").push().setValue(map)
            }
    }

    fun addFollowingHistory(key: String) {
        val followingHistoryModel = FollowersHistoryModel(
            key = userPreferenceManager.getUserKey(),
            time = getCurrentTime(),
            follow = true,
            like = false
        )
        databaseReference.child(key).child("history").push().setValue(followingHistoryModel)
        databaseReference.child(key).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){
                    val model = snapshot.getValue<LoginModel>()
                    model?.name?.let {
                        model.fcm_token?.let { it1 ->
                            Log.d("jonatma", "onDataChange: $it1")
                            sendNotification(
                                it1,
                                it,"${model.username} started following you")
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    fun removeFollowingOwn(key: String) {

        val query: Query? = userPreferenceManager.getUserKey()
            ?.let {
                databaseReference.child(it).child("following").orderByChild("user").equalTo(key)
            }
        query?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (childSnapshot in snapshot.children) {
                        val userKey = childSnapshot.child("user").getValue(String::class.java)
                        if (userKey == key) {
                            childSnapshot.ref.removeValue()
                            break // Exit the loop once the correct child is found and removed
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }


    private fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm")
        val currentDate = sdf.format(Date())

        return currentDate.toString()
    }

    fun sendNotification(targetToken: String, title: String, message: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("https://fcm.googleapis.com/fcm/send")
                val httpURLConnection =
                    withContext(Dispatchers.IO) {
                        url.openConnection()
                    } as HttpURLConnection
                httpURLConnection.apply {
                    doOutput = true
                    requestMethod = "POST"
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("Authorization", "key=AAAA_KEbvbE:APA91bEYlaumMW6GBxYypDYB8mL8Mk3hkt-znrgwIr6Weyhr6oV3HXNCSRUDbKs8E8b6KAABCpUKPAKJ2MU4OnwhnXY1y3VJukOaAikpaLmWUf57gHWiJsTwfyNqcaa27leIz9K_sccY")
                }

                val notificationPayload = """
                {
                  "to": "$targetToken",
                  "notification": {
                    "title": "$title",
                    "body": "$message"
                  }
                }
            """.trimIndent()

                OutputStreamWriter(httpURLConnection.outputStream).apply {
                    write(notificationPayload)
                    flush()
                    close()
                }

                val responseCode = httpURLConnection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Handle success
                    val response = httpURLConnection.inputStream.bufferedReader().use { it.readText() }
                    Log.d("tekshirish", "sendNotification: $response")
                    withContext(Dispatchers.Main) {
                        // Update UI or notify user
                    }
                } else {
                    // Handle error
                }
            } catch (e: Exception) {
                // Handle exception
            }
        }
    }
}