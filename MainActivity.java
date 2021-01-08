package com.e.surfaceviewgame;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private SurfaceViewGame mGameView;
    private TextView mTxtTime;
    private TextView mTxtScore;
    private Handler mHandler;//android.os.Handler
    private MediaPlayer mMediaPlayer = null;
    private Timer mTimer;
    private long mStartTime;
    // 30秒
    private static final int GAME_TIME = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        setContentView(new SurfaceViewGame(this));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGameView = new SurfaceViewGame(this);
        FrameLayout framelayout = (FrameLayout) this.findViewById(R.id.gameview);
        framelayout.addView(mGameView);
        mTxtTime = (TextView) findViewById(R.id.txtTime);
        mTxtScore = (TextView) findViewById(R.id.txtScore);
        mHandler = new Handler();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        // メディアプレイヤーの作成
        mMediaPlayer = MediaPlayer.create(this, R.raw.bgm);
        mMediaPlayer.setLooping(true);


    }
    // タイマー処理
    private void timerProc() {
        // 経過時間と得点をテキストラベルに表示し、経過時間を見てタイムオーバーの処理を行う
        // 経過時間を算出
        long elapsedTime = System.currentTimeMillis() - mStartTime;
        boolean isGameover = false;
        // タイムオーバーかチェックする
        if (elapsedTime > (GAME_TIME * 1000)) {
            elapsedTime = GAME_TIME * 1000;
            // タイムオーバーならゲーム終了
            isGameover = true;
        }
        // 得点を取得
        int score = mGameView.getScore();
        // 得点表示
        String dispscore = String.format("Score:%04d", score);
        mTxtScore.setText(dispscore);
        // 経過時間表示
        String disptime = String.format("Time:%02d.%02d", elapsedTime / 1000, (elapsedTime % 1000) / 10);
        mTxtTime.setText(disptime);
        if (isGameover) {
            // ゲーム停止
            mGameView.pause();
            // タイマー停止
            timerStop();
            // BGM停止
            mMediaPlayer.pause();
            // ゲームオーバーのダイアログを表示する
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Flirking Droid");
            alertDialogBuilder.setMessage("GAME OVER Score:" + score);
            alertDialogBuilder.setPositiveButton("OK", null);
            alertDialogBuilder.setCancelable(true);
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                public void onDismiss(DialogInterface dialog) {
                    // ダイアログを閉じたらゲーム再開
                    mGameView.restart();
                    // タイマー再開
                    timerStart();
                    // BGM再開
                    mMediaPlayer.seekTo(0);
                    mMediaPlayer.start();
                }
            });
            alertDialog.show();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        // タイマー開始
        timerStart();
        // BGM再生
        mMediaPlayer.start();
    }
    @Override
    protected void onPause() {
        super.onPause();
        // タイマー停止
        timerStop();
        // BGM停止
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            // MediaPlayer解放
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
    // タイマー開始処理
    private void timerStart() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        mTimer = new Timer();
        // 開始時刻を取得
        mStartTime = System.currentTimeMillis();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                // UIスレッドで行う処理を呼び出す
                mHandler.post(new Runnable() {
                    public void run() {
                        timerProc();
                    }
                });
            }
        }, 0, 51);
    }
    // タイマー停止処理
    private void timerStop(){
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    };
}
