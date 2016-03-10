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
class ScoreAdapter(_bus: RxBus) : RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder>() {
    val targets = mapOf("20" to 20, "19" to 19, "18" to 18, "17" to 17, "16" to 16, "15" to 15, "B" to 25)
    val scores: List<ScoreItem> = targets.map { it -> ScoreItem(STRIKE_LEVEL.NONE, STRIKE_LEVEL.NONE, it.key.toString(), 0) }
    val _bus = _bus
    enum class STRIKE_LEVEL { NONE, FIRST, SECOND, THIRD, OPEN }

    data class ScoreItem(var strikeP1: STRIKE_LEVEL, var strikeP2: STRIKE_LEVEL, val label: String, val score: Int)

    data class ScoreHolder(var score: ScoreItem, var scores: List<ScoreItem>)

    data class Total(var isP1: Boolean, var score: Int?)
    override fun getItemCount(): Int = scores.size

    override fun onCreateViewHolder(parent: ViewGroup, itemType: Int): ScoreViewHolder = ScoreViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.score_item, parent, false))

    override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) = holder.bindScore(this, _bus, ScoreHolder(scores[position], scores))

    class ScoreViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindScore(adapter: ScoreAdapter, _bus: RxBus, scoreHolder: ScoreHolder) {
            with(scoreHolder) {
                val springSystemP1 = SpringSystem.create()
                val springSystemP2 = SpringSystem.create()
                val springListenerP1 = DartsSpringListener(adapter, itemView.player_one_score, true, _bus, scoreHolder)
                val springListenerP2 = DartsSpringListener(adapter, itemView.player_two_score, false, _bus, scoreHolder)

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
                STRIKE_LEVEL.THIRD -> {
                    return STRIKE_LEVEL.OPEN
                }
                else -> {
                    //NO OP
                }
            }
            return STRIKE_LEVEL.OPEN;
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
                else ->{
                    score.findDrawableByLayerId(R.id.empty).alpha = 0
                }
            }
         }

        inner class DartsSpringListener(scoreAdapter: ScoreAdapter, view: View, isPlayerOne: Boolean, _bus: RxBus, scoreHolder: ScoreHolder) : SimpleSpringListener() {
            val scoreAdapter = scoreAdapter
            val whichView = view
            val isPlayerOne = isPlayerOne
            val _bus = _bus
            val scoreHolder = scoreHolder
            val targets = mapOf("20" to 20, "19" to 19, "18" to 18, "17" to 17, "16" to 16, "15" to 15, "B" to 25)

            override fun onSpringUpdate(spring: Spring){
                var mappedValue = SpringUtil.mapValueFromRangeToRange(spring.currentValue, 0.0, 1.0, 1.0, 0.5)
                whichView.scaleX = mappedValue.toFloat()
                whichView.scaleY = mappedValue.toFloat()
            }

            override fun onSpringAtRest(spring: Spring){
                var score: Int? = 0
                if(isPlayerOne) {
                    scoreHolder.score.strikeP1 = incrementClick(scoreHolder.score.strikeP1)
                    if(scoreHolder.score.strikeP1 == ScoreAdapter.STRIKE_LEVEL.OPEN && scoreHolder.score.strikeP2 != ScoreAdapter.STRIKE_LEVEL.OPEN){
                        score = targets.get(scoreHolder.score.label)
                    }
                }else {
                    scoreHolder.score.strikeP2 = incrementClick(scoreHolder.score.strikeP2)
                    if(scoreHolder.score.strikeP2 == ScoreAdapter.STRIKE_LEVEL.OPEN && scoreHolder.score.strikeP1 != ScoreAdapter.STRIKE_LEVEL.OPEN){
                        score = targets.get(scoreHolder.score.label)
                    }
                }

                _bus.send(Total(isPlayerOne,score))
                scoreAdapter.notifyDataSetChanged()
            }
        }
    }
}
