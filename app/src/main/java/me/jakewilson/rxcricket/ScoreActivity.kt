package me.jakewilson.rxcricket

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_score.*

class ScoreActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)
        val busObservable = RxBus()
        val scoreAdapter =  ScoreAdapter(busObservable)
        scores_list.layoutManager = LinearLayoutManager(this)
        scores_list.adapter = scoreAdapter
        busObservable.toObserverable().subscribe { score ->
            if(score.isP1){
                val currentScore: Int? = player_one_score_label.text.toString().toInt()
                player_one_score_label.text = ((currentScore ?: 0 ) + (score.score ?: 0)).toString()
            } else{
                val currentScore: Int? = player_two_score_label.text.toString().toInt()
                player_two_score_label.text = ((currentScore ?: 0 ) + (score.score ?: 0)).toString()
            }
        }
    }
}
