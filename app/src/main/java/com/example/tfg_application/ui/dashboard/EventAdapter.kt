package com.example.tfg_application.ui.dashboard

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tfg_application.R
import com.example.tfg_application.databinding.ImageMessageBinding
import com.example.tfg_application.databinding.MessageBinding
import com.example.tfg_application.databinding.SmallEventBinding
import com.example.tfg_application.ui.chat.ChatFragment
import com.example.tfg_application.ui.chat.MessageAdapter
import com.example.tfg_application.ui.chat.model.ChatMessage
import com.example.tfg_application.ui.dashboard.model.Event
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import org.json.JSONObject

class EventAdapter (private val mAllEvents: List<Event>) : RecyclerView.Adapter<EventAdapter.ViewHolder>() {

    //RecyclerView.ViewHolder són cada un dels contenidos als que hem de definir els valors
    //RecyclerView.Adapter cree objectes viewHolder i associe les dades a aquests (les vincula)

    //private var mAllEvents2: List<Event>? = null
    //private var mAllEvents: Array<Event>? = null


    //Aquesta es la classe que guarde tots els elements de la UI que usarem, comuns per a tots els Contenidors
    class ViewHolder(private val binding: SmallEventBinding) : RecyclerView.ViewHolder(binding.root){
        val titulo: TextView
        init{
            titulo = binding.eventTextName
        }
        fun bind(item: Event) {
            binding.eventTextName.text = item.name
            if(item.date != null)
                binding.eventTextDate.text = item.date.toString()
            if(item.place != null)
                binding.eventTextAdress.text = item.place
            if(item.images!=null){
                val imgUrlJson: JSONObject = item.images.getJSONObject(0)
                val imgUrl: String = imgUrlJson.getString("url")
                Glide.with(binding.eventImageView.context).load(imgUrl).into(binding.eventImageView)
                Log.i(TAG, imgUrl)
            }

            /* Parse images
            if (item.imagesUrl != null) {
                loadImageIntoView(binding.eventImageView, item.imagesUrl[0]!!)
            } else {
                binding.messengerImageView.setImageResource(R.drawable.ic_account_circle_black_36dp)
            }*/
            //Revisar: falte la direccio i la imatge, si tot va bé les posem despres
            //setTextColor(item.name, binding.messageTextView)
        }
    }

    //Es crida quan s'ha de crear un nou ViewHolder. Fa el inflate del small_event
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        //return if (viewType == MessageAdapter.VIEW_TYPE_TEXT) {
        Log.i(TAG, "onCreateViewHolder")
        val view = inflater.inflate(R.layout.small_event, parent, false)
            val binding = SmallEventBinding.bind(view)
            return ViewHolder(binding)
            //return EventViewHolder(binding)
        /*}/* else {
            val view = inflater.inflate(R.layout.image_message, parent, false)
            val binding = ImageMessageBinding.bind(view)
            ImageMessageViewHolder(binding)
        }*/*/
    }

    //Aquest mètode es crida cada cop que es mostra per pantalla una nova view, amb la seva posició. Serveix per establir les dades
    // Per tant hem d'obtenir l'event a partir de la posicio o crear un RecyclerAdapter personalitzat que ho faci automaticament com ho fa el de firebase.
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.i(TAG, "onBindViewHolder")
        //Aquest mètode serveix per agafar el model i per fer diferents EventViewHolder per les diferents opcions
       // if (options.snapshots[position].text != null) {
        // event = mMySavedEvents.get(position) o algo així
        val evento: Event

            //Revisar: et diu que ho facis com mAllEvents[position]
            evento = mAllEvents.get(position)
            holder.bind(evento)


    }

    private fun loadImageIntoView(view: ImageView, url: String) {
        if (url.startsWith("gs://")) {
            val storageReference = Firebase.storage.getReferenceFromUrl(url)
            storageReference.downloadUrl
                .addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    Glide.with(view.context)
                        .load(downloadUrl)
                        .into(view)
                }
                .addOnFailureListener { e ->
                    Log.w(
                        MessageAdapter.TAG,
                        "Getting download url was not successful.",
                        e
                    )
                }
        } else {
            Glide.with(view.context).load(url).into(view)
        }
    }

    /*fun setmAllEvents(events: Array<Event>){
        Log.i(TAG, "setAllEvents")
        mAllEvents = events
    }*/

    override fun getItemCount(): Int {
        Log.i(TAG, " getItemCount: "+ mAllEvents.size)
        return mAllEvents.size
    }

    companion object {
        const val TAG = "EventAdapter"
        const val VIEW_TYPE_TEXT = 1
        const val VIEW_TYPE_IMAGE = 2
    }
}