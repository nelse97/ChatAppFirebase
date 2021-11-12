package com.example.chatappfirebase.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatappfirebase.Models.RowChat
import com.example.chatappfirebase.Models.User
import com.example.chatappfirebase.R
import com.example.chatappfirebase.databinding.FragmentChatsBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import de.hdodenhof.circleimageview.CircleImageView
import java.lang.Exception

class ChatsFragment : Fragment() {

    private lateinit var binding: FragmentChatsBinding
    var chats = mutableListOf<RowChat>()
    private lateinit var db: FirebaseFirestore
    private lateinit var dbReference: ListenerRegistration
    private lateinit var adapter: ChatsFragment.ChatsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChatsBinding.inflate(layoutInflater)

        db = FirebaseFirestore.getInstance()



        return binding.root
    }

    inner class ChatsAdapter(private val clickListener: (RowChat) -> Unit):
        RecyclerView.Adapter<ChatsFragment.ChatsAdapter.MyViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val row = LayoutInflater.from(activity).inflate(R.layout.row_user_chat, parent, false)
            return MyViewHolder(row)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.bindViewHolder(chats[position])
            holder.itemView.setOnClickListener {
                clickListener(chats[position])
            }

        }

        override fun getItemCount(): Int {
            return chats.size
        }

        inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            var chatContactImage: CircleImageView = itemView.findViewById(R.id.circleImageViewRowContacto)
            var chatContactName: TextView = itemView.findViewById(R.id.tvRowUserChatName)
            var chatContactEmail: TextView = itemView.findViewById(R.id.tvRowUserChatEmail)

            fun bindViewHolder(rowChat: RowChat) {
                if (rowChat.photoUrl.isNotEmpty()) {
                    try {
                        Glide.with(activity!!).load(rowChat.photoUrl).into(chatContactImage)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }

                chatContactName.text = rowChat.chatName
                //chatContactEmail.text = rowChat.
            }
        }

    }
}