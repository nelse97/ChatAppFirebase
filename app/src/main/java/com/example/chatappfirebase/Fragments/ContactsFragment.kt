package com.example.chatappfirebase.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatappfirebase.Models.User
import com.example.chatappfirebase.R
import com.example.chatappfirebase.databinding.FragmentContactsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import de.hdodenhof.circleimageview.CircleImageView
import java.lang.Exception

import com.example.chatappfirebase.Activities.MessagesActivity


class ContactsFragment : Fragment() {

    private lateinit var binding: FragmentContactsBinding
    var users = mutableListOf<User>()
    private lateinit var db: FirebaseFirestore
    private lateinit var dbReference: ListenerRegistration
    private lateinit var adapter: ContactsAdapter
    private lateinit var currentUserEmail: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentContactsBinding.inflate(layoutInflater)

        db = FirebaseFirestore.getInstance()

        currentUserEmail = FirebaseAuth.getInstance().currentUser!!.email.toString()

        db.collection("users").get().addOnSuccessListener { documents ->
            for (document in documents) {
                users.add(document.toObject(User::class.java))
            }
        }

        adapter = ContactsAdapter() {
            Toast.makeText(activity, it.name, Toast.LENGTH_SHORT).show()
            var intent = Intent(activity, MessagesActivity::class.java)

        }
        binding.recyclerViewContacts.layoutManager = LinearLayoutManager(activity)
        binding.recyclerViewContacts.adapter = adapter

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        users.clear()

        //.whereEqualTo("state", "CA")
        dbReference = db.collection("users").addSnapshotListener { value, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            users.clear()
            for (doc in value!!) {
                val user = User()
                doc.getString("name")?.let {
                    user.name = it
                }
                doc.getString("email")?.let {
                    user.email = it
                }
                doc.getString("photoUrl")?.let {
                    user.photoUrl = it
                }
                if(currentUserEmail != user.email) {
                    users.add(user)
                }
            }
            adapter.notifyDataSetChanged()
        }

    }

    override fun onStop() {
        super.onStop()
        dbReference.remove()
    }

    inner class ContactsAdapter(private val clickListener: (User) -> Unit):
        RecyclerView.Adapter<ContactsFragment.ContactsAdapter.MyViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val row = LayoutInflater.from(activity).inflate(R.layout.row_contact, parent, false)
            return MyViewHolder(row)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.bindViewHolder(users[position])
            holder.itemView.setOnClickListener {
                clickListener(users[position])
            }
        }

        override fun getItemCount(): Int {
            return users.size
        }

        inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            var contactImage: CircleImageView = itemView.findViewById(R.id.circleImageViewRowContacto)
            var contactName: TextView = itemView.findViewById(R.id.tvContactName)
            var contactEmail: TextView = itemView.findViewById(R.id.tvContactEmail)

            fun bindViewHolder(user: User) {
                if (user.photoUrl.isNotEmpty()) {
                    try {
                        Glide.with(activity!!).load(user.photoUrl).into(contactImage)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }

                contactName.text = user.name
                contactEmail.text = user.email
            }
        }
    }
}