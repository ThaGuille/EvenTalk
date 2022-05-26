package com.example.tfg_application.ui.chat

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig.*
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
//import com.google.firebase.codelab.friendlychat.databinding.ActivityMainBinding
//import com.google.firebase.codelab.friendlychat.model.FriendlyMessage
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class ChatKotlinFragment : AppCompatActivity() {

    private lateinit var binding: ChatKotlinFragment
    private lateinit var manager: LinearLayoutManager

    // Firebase instance variables
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var adapter: MessageAdapter

    /* S'ha de crear la classe MyOpenDocumentContract, per més tard
    private val openDocument = registerForActivityResult(MyOpenDocumentContract()) { uri ->
        onImageSelected(uri)
    }*/

    /*override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {

        binding = ChatKotlinFragment.inflate()
        return super.onCreateView(name, context, attrs)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ChatKotlinFragment.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun onImageSelected(uri: Uri) {

    }*/
    }