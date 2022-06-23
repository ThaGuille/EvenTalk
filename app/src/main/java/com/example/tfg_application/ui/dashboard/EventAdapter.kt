package com.example.tfg_application.ui.dashboard

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.NavOptions
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tfg_application.R
import com.example.tfg_application.databinding.SmallEventBinding
import com.example.tfg_application.ui.dashboard.model.Event
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
            }
            if(item.location!=null){
                binding.eventButtonMap.setOnClickListener(View.OnClickListener {
                    goToMap(item)
                } )
            }
            binding.eventButtonSave.setOnClickListener(View.OnClickListener {
                saveEvent(item)
            })
            binding.eventButtonChat.setOnClickListener(View.OnClickListener {
                goToChat(item.id)
            })
            /* Parse images
            if (item.imagesUrl != null) {
                loadImageIntoView(binding.eventImageView, item.imagesUrl[0]!!)
            } else {
                binding.messengerImageView.setImageResource(R.drawable.ic_account_circle_black_36dp)
            }*/
            //Revisar: falte la direccio i la imatge, si tot va bé les posem despres
            //setTextColor(item.name, binding.messageTextView)
        }

        //Ens envie a la pantalla del mapa passant sol les variables que requerirem allí
        private fun goToMap(event: Event){
            //Log.i(TAG, "map button clicked: $location")
            val navOptions: NavOptions = NavOptions.Builder()
                .setPopUpTo(R.id.navigation_events, true)
                .build()
            val bundle = Bundle()
            bundle.putString("id", event.id)
            bundle.putString("name", event.name)
            if(event.date!=null)
                bundle.putString("date", event.date.toString())
            val imgUrlJson: JSONObject = event.images.getJSONObject(0)
            val imgUrl: String = imgUrlJson.getString("url")
            bundle.putString("image", imgUrl)
            if(event.location!=null) {
                bundle.putString("latitude", event.location.latitude.toString())
                bundle.putString("longitude", event.location.longitude.toString())
            }
            findNavController(this.itemView).navigate(
                R.id.action_navigation_events_to_navigation_map,
                bundle,
                navOptions
            )

            //Metode per anar a mapFragament en la ubicació indicada
        }
        private fun saveEvent(event: Event){
            Log.i(TAG, "save button clicked: ${event.name}")
            //Metode per guardar l'event
        }
        private fun goToChat(id: String){
            Log.i(TAG, "chat button clicked: $id")
            //Metode per guardar l'event
        }

    }

    //Es crida quan s'ha de crear un nou ViewHolder. Fa el inflate del small_event
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        //return if (viewType == MessageAdapter.VIEW_TYPE_TEXT) {
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
        //Aquest mètode serveix per agafar el model i per fer diferents EventViewHolder per les diferents opcions
       // if (options.snapshots[position].text != null) {
        // event = mMySavedEvents.get(position) o algo així
        val evento: Event

            //Revisar: et diu que ho facis com mAllEvents[position]
            evento = mAllEvents.get(position)
            holder.bind(evento)


    }


    /*fun setmAllEvents(events: Array<Event>){
        Log.i(TAG, "setAllEvents")
        mAllEvents = events
    }*/

    override fun getItemCount(): Int {
        return mAllEvents.size
    }

    companion object {
        const val TAG = "EventAdapter"
        const val VIEW_TYPE_TEXT = 1
        const val VIEW_TYPE_IMAGE = 2
    }
}