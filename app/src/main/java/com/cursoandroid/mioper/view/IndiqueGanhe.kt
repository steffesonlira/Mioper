package com.cursoandroid.mioper.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterViewFlipper
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.cursoandroid.mioper.R
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth

class IndiqueGanhe : AppCompatActivity() {
    private lateinit var adapterViewFlipper: AdapterViewFlipper
    private lateinit var mAuth: FirebaseAuth
    private lateinit var btnInvite: Button
    private var analytics: FirebaseAnalytics? = null
    lateinit var txtCodigo: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_indique_ganhe)
        btnInvite = findViewById(R.id.btnInvite)
        mAuth = FirebaseAuth.getInstance()
        adapterViewFlipper = findViewById(R.id.idAdapterVF)
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        txtCodigo = findViewById(R.id.txtCodigo)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.title = "Indique e Ganhe"

        val adapter = FlipperAdapter(this, TEXT, IMAGES)
        adapterViewFlipper.adapter = adapter
        adapterViewFlipper.isAutoStart = true

        btnInvite.setOnClickListener {
            onInviteClicked()
        }
    }

    inner class FlipperAdapter(
        var ctx: Context,
        var text: Array<String>,
        var images: IntArray
    ) : BaseAdapter() {
        var inflater: LayoutInflater = LayoutInflater.from(ctx)

        override fun getCount(): Int = text.size

        override fun getItem(position: Int): Any? = null

        override fun getItemId(position: Int): Long = 0

        override fun getView(i: Int, view: View?, viewGroup: ViewGroup?): View {
            val itemView = inflater.inflate(R.layout.sliderimages, null)
            val txtName = itemView.findViewById<TextView>(R.id.idTxtImageView)
            val txtImage = itemView.findViewById<ImageView>(R.id.idImgView)
            txtName.text = text[i]
            txtName.textSize = 18f
            txtImage.setImageResource(images[i])
            return itemView
        }
    }

    private fun onInviteClicked() {
        val text = txtCodigo.text.toString()
        val mSharingIntent = Intent(Intent.ACTION_SEND)
        mSharingIntent.type = "text/plain"
        mSharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Write your subject here")
        mSharingIntent.putExtra(Intent.EXTRA_TEXT, text)
        startActivity(Intent.createChooser(mSharingIntent, "Share text via"))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.principal, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }

    companion object {
        private const val TAG = "Mioper"
        private val TEXT = arrayOf("Entre no App do Mioper", "Procure pelo Mioper mais próximo", "O Motorista aceitará sua solicitação", "Partiu Viajar com o Mioper")
        private val IMAGES = intArrayOf(R.drawable.indique1, R.drawable.indique2, R.drawable.indique3, R.drawable.indique4)
    }
}
