package com.example.tfg_application.ui.chat

//import androidx.lifecycle.ViewModelProvider.get
//import com.google.firebase.codelab.friendlychat.databinding.ActivityMainBinding
//import com.google.firebase.codelab.friendlychat.model.FriendlyMessage
import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tfg_application.R
import com.example.tfg_application.databinding.FragmentChatBinding
import com.example.tfg_application.ui.chat.model.ChatMessage
import com.firebase.ui.auth.AuthUI.IdpConfig.*
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.lang.IndexOutOfBoundsException

//Pd: per alguna rao desconeguda, eventId agafe directament el valor del argument "id" del Intent sense dir-li (casi m'explote el cap)
class ChatFragment : Fragment(){
    private lateinit var binding: FragmentChatBinding
    private lateinit var manager: LinearLayoutManager
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var adapter: MessageAdapter
    private lateinit var eventId: String
    private val openDocument = registerForActivityResult(MyOpenDocument()) { uri ->
        uri?.let { onImageSelected(it) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentChatBinding.inflate(inflater, container, false)
        auth = Firebase.auth
        db = Firebase.database
        //Si el chat és d'un event, guarda la seva id, sino es posa la id predeterminada "standard"
        eventId = arguments?.getString("event")?.toString() ?: "standard"
        //Referencia a la db. [MESSAGES_CHILD ("messages") -> eventId] es el lloc a la realtime db de firebase  on es guarden els missatges
        val messagesRef: DatabaseReference = db.reference.child(MESSAGES_CHILD).child(eventId)
        val options = FirebaseRecyclerOptions.Builder<ChatMessage>()
            .setQuery(messagesRef, ChatMessage::class.java)
            .build()
        adapter = MessageAdapter(options, getUserName())
        manager = LinearLayoutManager(this.context)


        binding.progressBar.visibility = ProgressBar.INVISIBLE
        //Revisar: aqui podrie haver un error
        manager.stackFromEnd = true
        binding.messageRecyclerView.layoutManager = manager
        binding.messageRecyclerView.adapter = adapter


        //Si el chat és d'un event, guarda el seu nom, sino es posa el nom predeterminat "Chat"
        var eventName: String? = arguments?.getString("eventName")?.toString() ?: "Chat"
        if(eventName.equals("Chat"))
         eventName = activity?.intent?.extras?.getString("eventName")?: "Chat"
        (activity as AppCompatActivity).supportActionBar?.title = eventName


        //Observer que fa scroll a la pantalla quan s'afegeix un missatge per a veure'l
        adapter.registerAdapterDataObserver(
            MyScrollToBottomObserver(binding.messageRecyclerView, adapter, manager)
        )
        //Listener que canvie el color del botó d'enviar quan hi ha text
        binding.messageEditText.addTextChangedListener(MyButtonObserver(binding.sendButton))

        binding.sendButton.setOnClickListener {
            val friendlyMessage = ChatMessage(
                binding.messageEditText.text.toString(),
                getUserName(),
                getPhotoUrl(),
                null
            )
            db.reference.child(MESSAGES_CHILD).child(eventId).push().setValue(friendlyMessage)
            binding.messageEditText.setText("")
        }

        // When the image button is clicked, launch the image picker
        binding.addMessageImageView.setOnClickListener {
            openDocument.launch(arrayOf("image/*"))
        }

        return binding.root
    }



    public override fun onPause() {
        adapter.stopListening()
        super.onPause()
    }
    public override fun onResume() {
        super.onResume()
        adapter.startListening()
    }



    //El mètode es una mica diferent perque és un fragment I TOT HA DE SER DIFERENT AL MINIM CANVI, NO VAGI A SER TANT FACIL
    //A més a més, amb kotlin i sent fragment no es pot canviar el títol del menu, pk RES POT SER SENZILL EN AQUEST PROGRAMA
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.chat_toolbar, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun onImageSelected(uri: Uri) {
        Log.i(TAG, "Image selected, Uri: $uri")
        val user = auth.currentUser
        val tempMessage = ChatMessage(null, getUserName(), getPhotoUrl(), LOADING_IMAGE_URL)
        db.reference
            .child(MESSAGES_CHILD)
            .child(eventId)
            .push()
            .setValue(
                tempMessage,
                DatabaseReference.CompletionListener { databaseError, databaseReference ->
                    if (databaseError != null) {
                        Log.i(TAG, "Unable to write message to database.", databaseError.toException())
                        return@CompletionListener
                    }
                    Log.i(TAG, "going to write image in storage")
                    // Build a StorageReference and then upload the file
                    val key = databaseReference.key
                    val storageReference = Firebase.storage
                        .getReference(user!!.uid)
                        .child(key!!)
                        .child(uri.lastPathSegment!!)
                    putImageInStorage(storageReference, uri, key)
                })
    }

    private fun putImageInStorage(storageReference: StorageReference, uri: Uri, key: String?) {
        // First upload the image to Cloud Storage
        Log.i(TAG, "putImageInStorage")
        try {
            storageReference.putFile(uri)
                .addOnSuccessListener(
                    (this.activity as Activity)
                    //b //this.requireActivity() //Revisar: 99% segur que aqui hi ha un error
                    //Pues al final es un bug, res que fer
                ) { taskSnapshot -> // After the image loads, get a public downloadUrl for the image
                    // and add it to the message.
                    taskSnapshot.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener { uri ->
                            val friendlyMessage =
                                ChatMessage(null, getUserName(), getPhotoUrl(), uri.toString())
                            db.reference
                                .child(MESSAGES_CHILD)
                                .child(eventId)
                                .child(key!!)
                                .setValue(friendlyMessage)
                        }
                }
                .addOnFailureListener(this.requireActivity()) { e ->
                    Log.w(TAG, "Image upload task was unsuccessful.", e)
                }
        }catch (e: Exception){
            //Per lo que he pogut averiguar, en alguns dispositius done error
            //Es un bug de RecyclerView 23.1.1 que no s'ha corregit encara  (dies i dies intentant-ho...)
            // Caused by: java.io.IOException: {  "error": {    "code": 404,    "message": "Not Found."  }}
            e.printStackTrace()

        }
    }

    private fun getPhotoUrl(): String? {
        val user = auth.currentUser
        return user?.photoUrl?.toString()
    }

    private fun getUserName(): String? {
        val user = auth.currentUser
        return if (user != null) {
            user.displayName
        } else ANONYMOUS
    }

    /*private fun getDbName(): String?{
        var name: String
        if(eventId.isNullOrBlank()) name = MESSAGES_CHILD
        else name = eventId
    }*/
        /* No es pot posar binding a null al ser lateinit...
        override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }*/

    companion object {
        private const val TAG = "ChatFragment"
        const val MESSAGES_CHILD = "messages"
        const val ANONYMOUS = "anonymous"
        private const val LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif"
    }
}