package com.example.ijkplayertest

import android.graphics.SurfaceTexture
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_demo_player.*

class DemoPlayerActivity : AppCompatActivity() {

    //定义一个媒体播发对象
    private var mMediaPlayer: MediaPlayer? = null
    //定义一个缓冲区句柄（由屏幕合成程序管理）
    private var surface: Surface? = null

    var textureView : TextureView ? = null

    //封面
    private var videoImage: ImageView? = null
    //进度条
    private var seekBar: SeekBar? = null

    //为多线程定义Handler
    private val handler = Handler()

    /**
     * 定义一个Runnable对象
     * 用于更新播发进度
     */
    private val mTicker = object : Runnable {
        override fun run() {
            //延迟200ms再次执行runnable,就跟计时器一样效果
            handler.postDelayed(this, 200)

            if (mMediaPlayer != null && mMediaPlayer!!.isPlaying) {
                //更新播放进度
                seekBar!!.progress = mMediaPlayer!!.currentPosition
            }
        }
    }

    /**
     * 当装载流媒体完毕的时候回调
     */
    private val onPreparedListener = MediaPlayer.OnPreparedListener {
        //隐藏图片
        videoImage!!.visibility = View.GONE
        //开始播放
        mMediaPlayer!!.start()
        //设置总进度
        seekBar!!.max = mMediaPlayer!!.duration
        Log.e(Tag, Integer.toString(mMediaPlayer!!.duration))
        //用线程更新进度
        handler.post(mTicker)
    }

    /**
     * 流媒体播放结束时回调类
     */
    private val onCompletionListener = MediaPlayer.OnCompletionListener {
        videoImage!!.visibility = View.VISIBLE
        seekBar!!.progress = 0
        //删除执行的Runnable 终止计时器
        handler.removeCallbacks(mTicker)
        mMediaPlayer!!.start()
    }

    /**
     * 定义TextureView监听类SurfaceTextureListener
     * 重写4个方法
     */
    private val surfaceTextureListener = object : TextureView.SurfaceTextureListener {

        /**
         * 初始化好SurfaceTexture后调用
         * @param surfaceTexture
         * @param i
         * @param i1
         */
        override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture, i: Int, i1: Int) {
            surface = Surface(surfaceTexture)
            //开启一个线程去播放视频
            PlayerVideoThread().start()
        }

        /**
         * 视频尺寸改变后调用
         * @param surfaceTexture
         * @param i
         * @param i1
         */
        override fun onSurfaceTextureSizeChanged(surfaceTexture: SurfaceTexture, i: Int, i1: Int) {

        }

        /**
         * SurfaceTexture即将被销毁时调用
         * @param surfaceTexture
         * @return
         */
        override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
            Log.e(Tag, "onSurfaceTextureDestroyed")
            surface = null
            mMediaPlayer?.stop()
            mMediaPlayer?.reset()
            mMediaPlayer?.release()
            mMediaPlayer = null
            return true
        }

        /**
         * 通过SurfaceTexture.updateteximage()更新指定的SurfaceTexture时调用
         * @param surfaceTexture
         */
        override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {

        }
    }

    /**
     * 定义SeekBar监听类OnSeekBarChangeListener
     * 重写3个方法
     */
    private val onSeekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {

        /**
         * 进度级别已经更改的通知
         * @param seekBar
         * @param i
         * @param b
         */
        override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {

        }

        /**
         * 用户已经开始了一个触摸手势的通知
         * @param seekBar
         */
        override fun onStartTrackingTouch(seekBar: SeekBar) {
            //如果在播放中，指定视频播发位置
            if (mMediaPlayer != null && mMediaPlayer!!.isPlaying) {
                mMediaPlayer!!.pause()
            }
        }

        /**
         * 用户已经结束了一个触摸手势的通知
         * @param seekBar
         */
        override fun onStopTrackingTouch(seekBar: SeekBar) {
            Log.e(Tag, Integer.toString(seekBar.progress))
            //如果在播放中，指定视频播发位置
            if (mMediaPlayer != null && mMediaPlayer!!.isPlaying) {
                mMediaPlayer!!.seekTo(seekBar.progress)
            } else {
                mMediaPlayer!!.seekTo(seekBar.progress)
                mMediaPlayer!!.start()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo_player)

        textureView = TextureView(DemoPlayerActivity@this)
        cont.addView(textureView)
        //为textureView设置监听
        textureView?.surfaceTextureListener = surfaceTextureListener
        videoImage = findViewById(R.id.video_image)

        seekBar = findViewById(R.id.seekbar)
        //为seekbar设置监听
        seekBar!!.setOnSeekBarChangeListener(onSeekBarChangeListener)
    }

    /**
     * 定义一个线程，用于播发视频
     */
    private inner class PlayerVideoThread : Thread() {
        override fun run() {
            try {
                mMediaPlayer = MediaPlayer()
                //把res/raw的资源转化为Uri形式访问(android.resource://)
                //                Uri uri = Uri.parse("android.resource://com.github.davidji80.videoplayer/"+R.raw.ansen);
                //设置播放资源(可以是应用的资源文件／url／sdcard路径)
                mMediaPlayer!!.setDataSource("http://resource.yaokan.sogoucdn.com/videodown/5e9f/953/d9538564a807e2da945464fce9ea888e.mp4")
                //设置渲染画板
                mMediaPlayer!!.setSurface(surface)
                //设置播放类型
                mMediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
                //播放完成监听
                mMediaPlayer!!.setOnCompletionListener(onCompletionListener)
                //预加载监听
                mMediaPlayer!!.setOnPreparedListener(onPreparedListener)
                //设置是否保持屏幕常亮
                mMediaPlayer!!.setScreenOnWhilePlaying(true)
                //同步的方式装载流媒体文件
                mMediaPlayer!!.prepare()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        release()
        /*if (mMediaPlayer != null) {
            mMediaPlayer?.stop()

            //关键语句
            mMediaPlayer?.reset()

            mMediaPlayer?.release()
            mMediaPlayer = null
        }*/
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



        if (mMediaPlayer != null){
            mMediaPlayer?.apply {
            if (this.isPlaying) {
                this.stop()
                this.reset()
                this.setSurface(null)
                this.release()
                mMediaPlayer = null
            }
            }
//            ijkMediaPlayer = null
        }
    }

    companion object {

        private val Tag = "DemoPlayerActivity"
    }

}
