package com.example.chatappfirebase.Models

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import java.lang.Exception

class RowChat {

    var chatId: String = ""
    var chatName: String = ""
    var chatType: String = ""
    var photoUrl: String = ""

    constructor()

    constructor(chatId: String, chatName: String, chatType: String, photoUrl: String) {
        this.chatId = chatId
        this.chatName = chatName
        this.chatType = chatType
        this.photoUrl = photoUrl
    }

    fun toHash(): HashMap<String, Any> {
        var hashMap = HashMap<String, Any>()
        hashMap.put("chatId", chatName)
        hashMap.put("chatName", chatName)
        hashMap.put("chatType", chatType)
        hashMap.put("photoUrl", photoUrl)
        return hashMap
    }

    companion object {
        fun fromHash(hashMap: QueryDocumentSnapshot): RowChat {
            val rowChat = RowChat(
                hashMap["chatId"] as String,
                hashMap["chatName"] as String,
                hashMap["chatType"] as String,
                hashMap["photoUrl"] as String
            )
            return rowChat
        }

        /*
        fun getChatDetails(): RowChat {

        }*/
    }


}