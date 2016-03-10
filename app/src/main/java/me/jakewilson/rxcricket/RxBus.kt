package me.jakewilson.rxcricket

import rx.Observable
import rx.subjects.PublishSubject
import rx.subjects.SerializedSubject

/**
 * Created by jakewilson on 3/9/16.
 */
class RxBus {
    private val _bus = SerializedSubject(PublishSubject.create<ScoreAdapter.Total>())

    fun send(o: ScoreAdapter.Total) {
        _bus.onNext(o)
    }

    fun toObserverable(): Observable<ScoreAdapter.Total> {
        return _bus
    }
}
