package me.jakewilson.rxcricket

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.darts
import kotlinx.android.synthetic.main.activity_main.forms

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    darts.setOnClickListener { startActivity(Intent(it.context, ScoreActivity::class.java)) }
    forms.setOnClickListener { startActivity(Intent(it.context, FormsActivity::class.java)) }
  }


}
