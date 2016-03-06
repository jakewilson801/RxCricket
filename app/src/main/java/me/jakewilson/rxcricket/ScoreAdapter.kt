package me.jakewilson.rxcricket

import android.graphics.drawable.LayerDrawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.facebook.rebound.SimpleSpringListener
import com.facebook.rebound.Spring
import com.facebook.rebound.SpringSystem
import com.facebook.rebound.SpringUtil
import kotlinx.android.synthetic.main.score_item.view.*

/**
 * Created by jakewilson on 3/4/16.
 */
class ScoreAdapter : RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder>() {
    val targets = mapOf("20" to 20, "19" to 19, "18" to 18, "17" to 17, "16" to 16, "15" to 15, "B" to 25)
    val scores: List<ScoreItem> = targets.map { it -> ScoreItem(STRIKE_LEVEL.NONE, STRIKE_LEVEL.NONE, it.key.toString(), 0) }

    enum class STRIKE_LEVEL { NONE, FIRST, SECOND, THIRD }

    data class ScoreItem(var strikeP1: STRIKE_LEVEL, var strikeP2: STRIKE_LEVEL, val label: String, val score: Int)

    override fun getItemCount(): Int = scores.size

    override fun onCreateViewHolder(parent: ViewGroup, itemType: Int): ScoreViewHolder = ScoreViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.score_item, parent, false))

    override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) = holder.bindScore(scores[position], this)

    class ScoreViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindScore(score: ScoreAdapter.ScoreItem, adapter: ScoreAdapter) {
            with(score) {
                val springSystemP1 = SpringSystem.create()
                val springSystemP2 = SpringSystem.create()
                val springListenerP1 = DartsSpringListener(score, adapter, itemView.player_one_score, true)
                val springListenerP2 = DartsSpringListener(score, adapter, itemView.player_two_score, false)

                val scaleSpringP1 = springSystemP1.createSpring()
                val scaleSpringP2 = springSystemP2.createSpring()

                scaleSpringP1.addListener(springListenerP1)
                scaleSpringP2.addListener(springListenerP2)
                itemView.which_target.text = score.label

                val layer1 = (itemView.context.resources.getDrawable(R.drawable.chalk_marks) as LayerDrawable)
                val layer2 = (itemView.context.resources.getDrawable(R.drawable.chalk_marks) as LayerDrawable)

                processStrikeImage(score.strikeP1, layer1)
                processStrikeImage(score.strikeP2, layer2)

                itemView.player_one_score.background = layer1
                itemView.player_two_score.background = layer2
                itemView.player_one_score.setOnTouchListener {
                    view, motionEvent ->
                    when (motionEvent.action) {
                        MotionEvent.ACTION_DOWN -> scaleSpringP1.endValue = 1.0
                        MotionEvent.ACTION_UP -> scaleSpringP1.endValue = 0.0
                        MotionEvent.ACTION_CANCEL -> scaleSpringP1.endValue = 0.0
                    }
                    true
                }

                itemView.player_two_score.setOnTouchListener {
                    view, motionEvent ->
                    when (motionEvent.action) {
                        MotionEvent.ACTION_DOWN -> scaleSpringP2.endValue = 1.0
                        MotionEvent.ACTION_UP -> scaleSpringP2.endValue = 0.0
                        MotionEvent.ACTION_CANCEL -> scaleSpringP2.endValue = 0.0
                    }
                    true
                }

                itemView.player_two_score.setOnClickListener {
                    score.strikeP2 = incrementClick(score.strikeP2)
                    adapter.notifyDataSetChanged()
                }
            }
        }

        fun incrementClick(strike: STRIKE_LEVEL): STRIKE_LEVEL {
            when (strike) {
                STRIKE_LEVEL.NONE -> {
                    return STRIKE_LEVEL.FIRST
                }
                STRIKE_LEVEL.FIRST -> {
                    return STRIKE_LEVEL.SECOND
                }
                STRIKE_LEVEL.SECOND -> {
                    return STRIKE_LEVEL.THIRD
                }
                else -> {
                    //NO OP
                }
            }
            return STRIKE_LEVEL.THIRD;
        }


        fun processStrikeImage(strike: STRIKE_LEVEL, score: LayerDrawable) {
            when (strike) {
                STRIKE_LEVEL.NONE -> {
                    score.findDrawableByLayerId(R.id.empty).alpha = 255
                    score.findDrawableByLayerId(R.id.first_strike).alpha = 0
                    score.findDrawableByLayerId(R.id.second_strike).alpha = 0
                    score.findDrawableByLayerId(R.id.third_strike).alpha = 0
                }
                STRIKE_LEVEL.FIRST -> {
                    score.findDrawableByLayerId(R.id.empty).alpha = 0
                    score.findDrawableByLayerId(R.id.first_strike).alpha = 255
                    score.findDrawableByLayerId(R.id.second_strike).alpha = 0
                    score.findDrawableByLayerId(R.id.third_strike).alpha = 0
                }
                STRIKE_LEVEL.SECOND -> {
                    score.findDrawableByLayerId(R.id.empty).alpha = 0
                    score.findDrawableByLayerId(R.id.first_strike).alpha = 255
                    score.findDrawableByLayerId(R.id.second_strike).alpha = 255
                    score.findDrawableByLayerId(R.id.third_strike).alpha = 0
                }
                STRIKE_LEVEL.THIRD -> {
                    score.findDrawableByLayerId(R.id.empty).alpha = 0
                    score.findDrawableByLayerId(R.id.first_strike).alpha = 255
                    score.findDrawableByLayerId(R.id.second_strike).alpha = 255
                    score.findDrawableByLayerId(R.id.third_strike).alpha = 255
                }
            }
         }

        inner class DartsSpringListener(score: ScoreItem, scoreAdapter: ScoreAdapter, view: View, isPlayerOne: Boolean) : SimpleSpringListener() {
            val score = score
            val scoreAdapter = scoreAdapter
            val whichView = view
            val isPlayerOne = isPlayerOne

            override fun onSpringUpdate(spring: Spring){
                var mappedValue = SpringUtil.mapValueFromRangeToRange(spring.currentValue, 0.0, 1.0, 1.0, 0.5)
                whichView.scaleX = mappedValue.toFloat()
                whichView.scaleY = mappedValue.toFloat()
            }

            override fun onSpringAtRest(spring: Spring){
                if(isPlayerOne)
                    score.strikeP1 = incrementClick(score.strikeP1)
                else
                    score.strikeP2 = incrementClick(score.strikeP2)
                scoreAdapter.notifyDataSetChanged()
            }
        }
    }
}
