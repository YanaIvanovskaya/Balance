package com.example.balance

class Event<T>(
    private val value: T
) {

    private var mIsConsumed = false

    fun consume(action: (T) -> Unit) {
        if (!mIsConsumed) {
            action(value)
            mIsConsumed = true
        }
    }

}