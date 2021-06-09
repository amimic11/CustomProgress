  package com.graveno.alphalab.app.customprogress

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.graveno.alphalab.app.customprogress.databinding.ActivityMainBinding
import java.util.concurrent.ThreadLocalRandom

  class MainActivity : AppCompatActivity() {

      private var bind : ActivityMainBinding? = null
      private val binder get() = bind!!
      private val max = 100f
      private val min = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binder.root)

        binder.cirProgress.setMax(max)
        binder.progressLeft.setMax(max)
        binder.progressCenter.setMax(max)
        binder.progressRight.setMax(max)

        binder.btnClick.setOnClickListener {
            binder.cirProgress.progress = getProgress()
            binder.progressCenter.progress = getProgress()
            binder.progressLeft.progress = getProgress()
            binder.progressRight.progress = getProgress()
        }
    }

      private fun getProgress(): Float {
          return ThreadLocalRandom.current().nextInt(min.toInt(), max.toInt()).toFloat()
      }

      override fun onDestroy() {
          bind = null
          super.onDestroy()
      }
}