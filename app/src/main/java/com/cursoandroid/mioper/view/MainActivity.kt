package com.cursoandroid.mioper.view

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PixelFormat
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.cursoandroid.mioper.R
import com.google.firebase.auth.FirebaseAuth
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class MainActivity : AppCompatActivity() {

    private lateinit var splashThread: Thread
    private lateinit var mAuth: FirebaseAuth
    private lateinit var logo: ImageView
    private lateinit var topanimation: Animation
    private lateinit var bottom_animation: Animation
    private lateinit var middle_animation: Animation

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        window.setFormat(PixelFormat.RGBA_8888)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        StartAnimations()
        logo = findViewById(R.id.logoMioperId)
        topanimation = AnimationUtils.loadAnimation(this, R.anim.top_animation)
        bottom_animation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation)
        middle_animation = AnimationUtils.loadAnimation(this, R.anim.middle_animation)

        try {
            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures.orEmpty()) {
                val messageDigest = MessageDigest.getInstance("SHA")
                messageDigest.update(signature.toByteArray())
                Log.d("KeyHash:", Base64.encodeToString(messageDigest.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {
        } catch (e: NoSuchAlgorithmException) {
        }

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val animation = AnimationUtils.loadAnimation(this, R.anim.alpha)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
            }

            override fun onAnimationEnd(animation: Animation) {
                val intent = Intent(this@MainActivity, Login::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }

            override fun onAnimationRepeat(animation: Animation) {
                logo.animation = topanimation
            }
        })

        StartAnimations()
    }

    override fun onBackPressed() {
    }

    override fun onStop() {
        super.onStop()
        finish()
    }

    private fun StartAnimations() {
        splashThread = object : Thread() {
            override fun run() {
                try {
                    var pause = 0
                    while (pause < 1700) {
                        Thread.sleep(200)
                        pause += 100
                    }

                    mAuth = FirebaseAuth.getInstance()
                    val user = mAuth.currentUser
                    val intent = Intent(this@MainActivity, Login::class.java)
                    intent.flags = if (user != null) {
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                    } else {
                        Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
                    }
                    startActivity(intent)
                } catch (e: InterruptedException) {
                } finally {
                    this@MainActivity.finish()
                }
            }
        }
        splashThread.start()
    }
}
