package com.example.ijkplayertest

import android.graphics.SurfaceTexture
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Surface
import android.view.TextureView
import kotlinx.android.synthetic.main.activity_some.*
import tv.danmaku.ijk.media.player.IMediaPlayer
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import java.io.IOException

class SomePlayerActivity : AppCompatActivity(){

    val urls = arrayListOf(
            "http://resource.yaokan.sogoucdn.com/videodown/5e9f/726/ba726d1978c0d0acf6af24547d0e67a3.mp4",
            "http://resource.yaokan.sogoucdn.com/videodown/5e9f/953/d9538564a807e2da945464fce9ea888e.mp4",
            "http://resource.yaokan.sogoucdn.com/videodown/5e9f/438/4eef3879c109aab328fff7b65df9e6a0.mp4",
            "http://resource.yaokan.sogoucdn.com/videodown/5e9f/726/ba726d1978c0d0acf6af24547d0e67a3.mp4",
            "http://resource.yaokan.sogoucdn.com/videodown/5e9f/953/d9538564a807e2da945464fce9ea888e.mp4",
            "http://resource.yaokan.sogoucdn.com/videodown/5e9f/438/4eef3879c109aab328fff7b65df9e6a0.mp4")
    var ijkMediaPlayer : IjkMediaPlayer? = null
    var textureView : TextureView ? = null
    var surface : Surface ? = null
    var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_some)

        initView(index)
        pre.setOnClickListener {
            index--
            index = if (index < 0) 0 else index

            initView(index)
        }

        next.setOnClickListener {
            index++
            index = if (index > urls.size-1) urls.size-1 else index

            initView(index)
        }
    }

    override fun onDestroy() {
        release()
        urls.clear()
        super.onDestroy()
//        Process.killProcess(Process.myPid())
    }

    private fun initView(index: Int){
        container.removeAllViews()
        release()
        configPlayer(index)
        container.addView(textureView)
    }

    private fun release() {

        if (surface != null){
            surface?.release()
            surface = null
        }

        if (textureView != null){
            textureView?.surfaceTextureListener = null
            textureView?.surfaceTexture?.release()
            textureView = null
        }

        if (ijkMediaPlayer != null){
            ijkMediaPlayer?.apply {
                this.stop()
                this.reset()
                this.resetListeners()
                this.setSurface(null)
                this.release()
            }
            ijkMediaPlayer = null
        }
    }

    private fun configPlayer(index: Int) {
//        startTime = System.currentTimeMillis()
        ijkMediaPlayer = IjkMediaPlayer()

        ijkMediaPlayer?.also { play->
            //需要准备好后自动播放0-不自动 1-自动
            play.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0)

            textureView = TextureView(SomePlayerActivity@this)

            play.setOnPreparedListener(IMediaPlayer.OnPreparedListener {
                play.start()
//            endTime = System.currentTimeMillis()
//            Log.e("xxxxxxxprepare", endTime - startTime + "ms")

                val layoutParams = textureView?.getLayoutParams()
                layoutParams?.height = textureView?.getWidth()?:0 * play.getVideoHeight() / play.getVideoWidth()
                textureView?.setLayoutParams(layoutParams)
            })

            play.setOnBufferingUpdateListener(IMediaPlayer.OnBufferingUpdateListener { iMediaPlayer, i ->
                Log.e("xxxxxxxplaying", "percent=$i")
                if (i == 0) {
                }
            })

            play.setOnCompletionListener(IMediaPlayer.OnCompletionListener { mp ->
                Log.e("Complete", "onCompletion")
                mp.seekTo(0)
            })

            play.setOnSeekCompleteListener(IMediaPlayer.OnSeekCompleteListener { Log.e("Complete", "onSeekComplete") })

            textureView?.surfaceTextureListener = object : TextureView.SurfaceTextureListener{
                override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
                }

                override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
                }

                override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
                    Log.e("textureView", "onSurfaceTextureDestroyed")
                    if (surface != null){
                        surface.detachFromGLContext()
                        surface.releaseTexImage()
                        surface.release()
                    }
                    return true
                }

                override fun onSurfaceTextureAvailable(surface0: SurfaceTexture?, width: Int, height: Int) {
                    Log.e("textureView", "onSurfaceTextureAvailable")
                    surface = Surface(surface0)
                    play.setSurface(surface)
                }

            }

            play.isLooping = true
            try {
                play.dataSource = urls[index]
                play.prepareAsync()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}