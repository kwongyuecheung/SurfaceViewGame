package com.e.surfaceviewgame;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.GestureDetector.OnGestureListener;
public class SurfaceViewGame extends SurfaceView implements SurfaceHolder.Callback, Runnable, OnTouchListener,OnGestureListener {
    private SurfaceHolder mHolder;
    private Bitmap mBitmapDroid;
    private int mLocateX;
    private int mLocateY;
    private boolean mAttached;
    private Thread mThread;
    private int mIncreaseX = 10;
    private int mIncreaseY = 10;
    private GestureDetector mGestureDetector;
    private SoundManager mSoundManager;
    // 蜂の数
    private static final int BEES = 10;
    private Bitmap mBitmapBee;
    private GameWorld mGameWorld;
    private boolean mGamePause;
    // 1/60 毎に画面書き換え
    private static final int FRAME_RATE = 60;

    public SurfaceViewGame(Context context) {

        super(context);

        mHolder = getHolder();
        mHolder.addCallback(this);
        // タッチリスナーを登録する
        setOnTouchListener(this);
        // ジェスチャーデテクターを生成
        mGestureDetector = new GestureDetector(context, this);
        // クリック可能属性を設定
        setClickable(true);
        mSoundManager = new SoundManager(context);
    }
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // GameWorld に画面サイズを通知
        mGameWorld.setGageWorldSize(width, height);
        // ゲーム開始
        mGamePause = false;
    }
    public void surfaceCreated(SurfaceHolder holder) {
        Resources rsc = getResources();
        // ドロイド君の画像 Bitmap を生成
        mBitmapDroid = BitmapFactory.decodeResource(rsc, R.drawable.droid);
        // 蜂の画像 Bitmap を生成
        mBitmapBee = BitmapFactory.decodeResource(rsc, R.drawable.bee);
        // GameWorld を生成
        mGameWorld = new GameWorld(BEES, mBitmapDroid.getWidth(), mBitmapDroid.getHeight(),
                mBitmapBee.getWidth(), mBitmapBee.getHeight(), mSoundManager);
        mAttached = true;
        mGamePause = true;
        // 描画スレッドを生成、起動する
        mThread = new Thread(this);
        mThread.start();

    }
    public void surfaceDestroyed(SurfaceHolder holder) {
        // スレッドを終了させる
        mAttached = false;
        // スレッド終了待ち
        while (mThread.isAlive())
            ;
        // 使用したリソースを開放
        if (mBitmapDroid != null) {
            mBitmapDroid.recycle();
            mBitmapDroid = null;
        }
        if (mBitmapBee != null) {
            mBitmapBee.recycle();
            mBitmapBee = null;
        }
        // 効果音管理インスタンスを破棄
        mSoundManager.release();
    }
    /**
     * 描画処理
     */
    private void doDraw() {
        Canvas canvas = mHolder.lockCanvas();
        if (canvas != null) {
            try {
                // 背景を白く塗りつぶす
                canvas.drawColor(Color.WHITE);
                // 蜂を描画
                GameWorld.Bee[] bees = mGameWorld.getBees();
                for (GameWorld.Bee bee : bees) {
                    if (bee.isVisible()) {
                        canvas.drawBitmap(mBitmapBee, bee.getX(), bee.getY(), null);
                    }
                }
                // ドロイド君を描画
                GameWorld.Droid droid = mGameWorld.getDroid();
                canvas.drawBitmap(mBitmapDroid, droid.getX(), droid.getY(), null);
            } finally {
                mHolder.unlockCanvasAndPost(canvas);
            }
        }
    }
    //@Override
    public void run() {
        while (mAttached) {
            if (mGamePause) {
                continue;
            }
            // 処理開始時刻を取得
            long starttime = System.currentTimeMillis();
            // 描画処理
            doDraw();
            // ゲーム内処理
            mGameWorld.process();
            // フレームレート処理
            // FRAME_RATE にしたがって sleep する時間を算出
            long sleeptime = 1000 / FRAME_RATE;
            long amounttime = System.currentTimeMillis() - starttime;
            sleeptime -= amounttime;
            if (sleeptime > 0) {
                try {
                    // sleep する
                    Thread.sleep(sleeptime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    //@Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }
    //@Override
    public boolean onDown(MotionEvent e) {
        return false;
    }
    //@Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        // フリック方向と強さで速度を算出する
        mIncreaseX = (int) (e2.getX() - e1.getX()) / 20;
        mIncreaseY = (int) (e2.getY() - e1.getY()) / 20;
        // ドロイド君に速度を設定
        mGameWorld.setDroidSpeed(mIncreaseX, mIncreaseY);
        // 音を鳴らす
        mSoundManager.play(SoundManager.SOUND_GUN, 100);
        return true;
    }
    //@Override
    public void onLongPress(MotionEvent e) {
    }
    //@Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }
    //@Override
    public void onShowPress(MotionEvent e) {
    }
    //@Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }
    // ゲーム得点を取得
    public int getScore() {
        if (mGameWorld == null) {
            return 0;
        }
        return mGameWorld.getScore();
    }
    // ゲーム停止
    public void pause() {
        mGamePause = true;
    }
    // ゲーム再開
    public void restart() {
        mGamePause = false;
        if (mGameWorld == null) {
            return;
        }
        mGameWorld.resetScore();
    }

}
