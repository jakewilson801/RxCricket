package me.jakewilson.rxcricket

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_score.*

class ScoreActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)
        val scoreAdapter =  ScoreAdapter()
        scores_list.layoutManager = LinearLayoutManager(this)
        scores_list.adapter = scoreAdapter
    }
}
