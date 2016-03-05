package me.jakewilson.rxcricket

import android.graphics.drawable.LayerDrawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    override fun onCreateViewHolder(parent: ViewGroup, itemType: Int): ScoreViewHolder {
        return ScoreViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.score_item, parent, false))
    }

    override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
        holder.bindScore(scores[position], this)
    }

    class ScoreViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindScore(score: ScoreAdapter.ScoreItem, adapter: ScoreAdapter) {
            with(score) {
                itemView.which_target.text = score.label
                val layer = (itemView.context.resources.getDrawable(R.drawable.chalk_marks) as LayerDrawable)
                processStrikeImage(score.strikeP1, layer)
                processStrikeImage(score.strikeP2, layer)
                itemView.player_one_score.background = layer
                itemView.player_two_score.background = layer

                itemView.player_one_score.setOnClickListener {
                    score.strikeP1 = incrementClick(score.strikeP1)
                    adapter.notifyDataSetChanged()
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
            return STRIKE_LEVEL.NONE
        }


        fun processStrikeImage(strike: STRIKE_LEVEL, score: LayerDrawable) {
            when (strike) {
                STRIKE_LEVEL.NONE -> {
                    //                score.findDrawableByLayerId(R.id.first_strike).alpha = 0
                    //                score.findDrawableByLayerId(R.id.second_strike).alpha = 0
                    //                score.findDrawableByLayerId(R.id.third_strike).alpha = 0
                }
                STRIKE_LEVEL.FIRST -> {
                    score.findDrawableByLayerId(R.id.second_strike).alpha = 0
                    score.findDrawableByLayerId(R.id.third_strike).alpha = 0
                }
                STRIKE_LEVEL.SECOND -> {
                    score.findDrawableByLayerId(R.id.third_strike).alpha = 0
                }
                else -> {
                    // NO OP
                }
            }
         }
    }
}
