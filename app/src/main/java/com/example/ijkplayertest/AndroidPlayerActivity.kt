package com.example.ijkplayertest

import android.graphics.SurfaceTexture
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Surface
import android.view.TextureView
import kotlinx.android.synthetic.main.activity_some.*

class AndroidPlayerActivity : AppCompatActivity(){

    val urls = arrayListOf(
            "http://resource.yaokan.sogoucdn.com/videodown/5e9f/726/ba726d1978c0d0acf6af24547d0e67a3.mp4",
            "http://resource.yaokan.sogoucdn.com/videodown/5e9f/953/d9538564a807e2da945464fce9ea888e.mp4",
            "http://resource.yaokan.sogoucdn.com/videodown/5e9f/438/4eef3879c109aab328fff7b65df9e6a0.mp4",
            "http://resource.yaokan.sogoucdn.com/videodown/5e9f/726/ba726d1978c0d0acf6af24547d0e67a3.mp4",
            "http://resource.yaokan.sogoucdn.com/videodown/5e9f/953/d9538564a807e2da945464fce9ea888e.mp4",
            "http://resource.yaokan.sogoucdn.com/videodown/5e9f/438/4eef3879c109aab328fff7b65df9e6a0.mp4")
    var ijkMediaPlayer : MediaPlayer? = null
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
        initPlayer(index)
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

                if (this.isPlaying){
                    this.stop()
                    this.reset()
                    this.setSurface(null)
                    this.release()
                    ijkMediaPlayer = null
                }
                Thread(Runnable {
                }).start()
            }
//            ijkMediaPlayer = null
        }
    }

    private fun initPlayer(index: Int) {

        textureView = TextureView(SomePlayerActivity@this)
        textureView?.surfaceTextureListener = object : TextureView.SurfaceTextureListener{
            override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
                Log.e("textureView", "onSurfaceTextureDestroyed")
                if (surface != null){
//                    surface.detachFromGLContext()
                    surface.releaseTexImage()
                    surface.release()
                }
                return true
            }

            override fun onSurfaceTextureAvailable(surface0: SurfaceTexture?, width: Int, height: Int) {
                Log.e("textureView", "onSurfaceTextureAvailable")
                surface = Surface(surface0)
                //开启一个线程去播放视频
                PlayerVideoThread().start()
            }
        }
    }

    /**
     * 定义一个线程，用于播发视频
     */
    private inner class PlayerVideoThread : Thread() {

        override fun run() {
            try {
                ijkMediaPlayer = MediaPlayer()

                ijkMediaPlayer?.also { player ->
                    //把res/raw的资源转化为Uri形式访问(android.resource://)
                    //                Uri uri = Uri.parse("android.resource://com.github.davidji80.videoplayer/"+R.raw.ansen);
                    //设置播放资源(可以是应用的资源文件／url／sdcard路径)
//                    player.setDataSource("http://resource.yaokan.sogoucdn.com/videodown/5e9f/953/d9538564a807e2da945464fce9ea888e.mp4")
                    player.setDataSource(urls[index])
                    //设置渲染画板
                    player.setSurface(surface)
                    //设置播放类型
                    player.setAudioStreamType(AudioManager.STREAM_MUSIC)
                    //播放完成监听
                    player.setOnCompletionListener {
                        it.start()
                    }
                    //预加载监听
                    player.setOnPreparedListener{
                        val layoutParams = textureView?.layoutParams
                        layoutParams?.height = textureView?.width ?:0 * player.videoHeight / player.videoWidth
                        textureView?.layoutParams = layoutParams
                        it.start()
                    }
                    //设置是否保持屏幕常亮
                    player.setScreenOnWhilePlaying(true)
                    //同步的方式装载流媒体文件
                    player.prepare()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }
}