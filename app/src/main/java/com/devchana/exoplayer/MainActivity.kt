package com.devchana.exoplayer

import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.devchana.exoplayer.databinding.ActivityMainBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource

/**
 * https://developer.android.com/codelabs/exoplayer-intro?hl=ko#0 참고하여 개발.
 */
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private val binding: ActivityMainBinding by lazy { DataBindingUtil.setContentView(this, R.layout.activity_main) }
    private var exoPlayer: ExoPlayer? = null

    ////////// Override Methods. (Life Cycle)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initialize()
    }

    override fun onStart() {
        super.onStart()
        // SDK 24부터 멀티 윈도우 지원으로 인하여 onStart에서 초기화 해야함.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            initPlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        // SDK 24 미만은 리소스 확보까지 오랜 시간이 걸리므로 onResume에서 초기화.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N || exoPlayer == null) {
            initPlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        // SDK 24 미만은 onStop의 호출이 보장되지 않으므로 onPauese에서 release.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        // SDK 24 이상은 onStop 호출이 보장되므로 onStop에서 release.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            releasePlayer()
        }
    }

    ////////// Initialize.
    private fun initialize() {
        binding.lifecycleOwner = this

    }

    ////////// ExoPlayer.
    private fun initPlayer() {
        exoPlayer = ExoPlayer.Builder(this)
            .build()
            .also {
                binding.playerView.player = it
            }.apply {
                setMediaItem(getTestMediaItem())
                playWhenReady = viewModel.playWhenReady // 리소스 확보시 자동 재생 여부
                seekTo(viewModel.currentItemIndex, viewModel.playbackPosition) // 미디어 항목 및 재생 위치를 찾는 메소드
                prepare()
            }
    }

    private fun releasePlayer() {
        exoPlayer?.run {
            viewModel.playbackPosition = this.currentPosition
            viewModel.currentItemIndex = this.currentMediaItemIndex
            viewModel.playWhenReady = this.playWhenReady
            release()
        }
        exoPlayer = null
    }

    private fun getMediaSource(): MediaSource {
        val dataSourceFactory = DefaultDataSource.Factory(this)
        return ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(Uri.parse(TEST_URL2)))
    }

    private fun getTestMediaItem(): MediaItem = MediaItem.fromUri(TEST_URL2)

    companion object {
        private const val TEST_URL = "https://www.youtube.com/watch?v=DYlN74EX6cM"
        private const val TEST_URL2 = "https://storage.googleapis.com/exoplayer-test-media-1/mkv/android-screens-lavf-56.36.100-aac-avc-main-1280x720.mkv"
    }
}