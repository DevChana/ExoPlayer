package com.devchana.exoplayer

import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {

    // ExoPlayer Variable
    var playWhenReady = true // 재생/일시중지 정보
    var currentItemIndex = 0 // 현재 미디어 항목 index
    var playbackPosition = 0L // 현재 재생 위치


}