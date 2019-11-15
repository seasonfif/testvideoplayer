package com.example.ijkplayertest;

import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class SinglePlayerActivity extends AppCompatActivity {

    private IjkMediaPlayer mPlayer;
    private List<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single);

        textureView = findViewById(R.id.surface_view);
//        textureView.setSurfaceTextureListener(listener);


        initData();


        ////////////////////////播放////////////////////////////////
        Button goOn = findViewById(R.id.go_on);
        goOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayer.setSurface(mSurface);
                mPlayer.start();
            }
        });

        ////////////////////////初始化////////////////////////////////
        Button play = findViewById(R.id.play);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPlayer();
            }
        });

        ////////////////////////暂停////////////////////////////////
        Button pause = findViewById(R.id.pause);
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayer.pause();
            }
        });

        ////////////////////////下一个////////////////////////////////
        Button next = findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                long start = System.currentTimeMillis();
                mPlayer.reset();
                long end = System.currentTimeMillis();

                Log.e("xxxx reset",(end-start)+"ms");

            }
        });

        ////////////////////////上一个////////////////////////////////
        Button pree = findViewById(R.id.pre);
        pree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }


    private void createPlayer() {
        if (mPlayer == null) {
            startTime = System.currentTimeMillis();
            mPlayer = new IjkMediaPlayer();
            //需要准备好后自动播放0-不自动 1-自动
            mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);
            mPlayer.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(IMediaPlayer iMediaPlayer) {
                    mPlayer.start();
                    endTime = System.currentTimeMillis();
                    Log.e("xxxxxxxprepare", (endTime-startTime)+"ms");

                    ViewGroup.LayoutParams layoutParams = textureView.getLayoutParams();
                    layoutParams.height = textureView.getWidth() * mPlayer.getVideoHeight() / mPlayer.getVideoWidth();
                    textureView.setLayoutParams(layoutParams);

                }
            });

            mPlayer.setOnBufferingUpdateListener(new IMediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {
                    Log.e("xxxxxxxplaying", "percent="+i);
                    if (i==0){
//                        mPlayer.pause();
                    }
                }
            });

            mPlayer.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(IMediaPlayer mp) {
                    Log.e("Complete", "onCompletion");
                    mp.seekTo(0);
                }
            });

            mPlayer.setOnSeekCompleteListener(new IMediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(IMediaPlayer mp) {
                    Log.e("Complete", "onSeekComplete");
                }
            });

            mSurface = new Surface(textureView.getSurfaceTexture());
//            mPlayer.setSurface(mSurface);
            mPlayer.setLooping(false);
            try {
                    mPlayer.setDataSource(list.get(1));
                    mPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }

        }
    }

    private void release() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

    private TextureView textureView;
    private Surface mSurface;
    private boolean isFirst = true;
    private long startTime = 0;
    private long endTime = 0;
    private TextureView.SurfaceTextureListener listener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            mSurface = new Surface(surface);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            textureView.setSurfaceTextureListener(null);
            textureView = null;
            mSurface = null;
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            endTime = System.currentTimeMillis();
            Log.e("xxxxxxxTextTureView", (endTime-startTime)+"ms");
            if (isFirst){
                mPlayer.pause();
                isFirst = false;
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        release();
    }

    private void initData() {
        list.add("http://resource.yaokan.sogoucdn.com/videodown/5e9f/726/ba726d1978c0d0acf6af24547d0e67a3.mp4");
        list.add("http://resource.yaokan.sogoucdn.com/videodown/5e9f/953/d9538564a807e2da945464fce9ea888e.mp4");
        list.add("http://resource.yaokan.sogoucdn.com/videodown/5e9f/438/4eef3879c109aab328fff7b65df9e6a0.mp4");
    }

}
