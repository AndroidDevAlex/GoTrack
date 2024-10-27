package com.example.tracker

enum class TrackerStatusState(val textId: Int, val colorId: Int, val showProgressBar: Boolean) {
    ENABLED(R.string.tracker_is_running, R.color.black, true),
    DISABLED(R.string.tracker_is_of, R.color.red, false),
    NO_INTERNET(R.string.stopped_tracking, R.color.red, false)
}