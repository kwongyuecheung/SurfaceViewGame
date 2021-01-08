package com.e.surfaceviewgame;


import java.util.Random;

public class GameWorld {
    private int mWorldWidth; // ゲーム画面の幅
    private int mWorldHeight; // ゲーム画面の高さ
    private Droid mDroid;
    private Bee[] mBees;
    private Random mRand;
    private SoundManager mSoundManager;
    private int mShotDownCount; // 撃墜数
    public GameWorld(int beecount, int droidWidth, int droidHeight, int beeWidth, int beeHeight,
                     SoundManager sm) {
        // 乱数の準備
        mRand = new Random();
        // ドロイド君を生成
        mDroid = new Droid(droidWidth, droidHeight);
        // 蜂インスタンスをbeecount分生成
        mBees = new Bee[beecount];
        for (int i = 0; i < mBees.length; i++) {
            mBees[i] = new Bee(beeWidth, beeHeight);
        }
        // 音声管理のインスタンスを受け取る
        mSoundManager = sm;
    }
    // ゲーム画面のサイズ設定
    public void setGageWorldSize(int w, int h) {
        mWorldWidth = w; // 画面の幅
        mWorldHeight = h; // 画面の高さ
    }
    // ドロイド君情報を取得
    public Droid getDroid() {
        return mDroid;
    }
    // 蜂情報を取得
    public Bee[] getBees() {
        return mBees;
    }
    // ドロイド君の速度を設定
    public void setDroidSpeed(int dx, int dy) {
        mDroid.setSpeed(dx, dy);
    }
    // 蜂を生成
    private void makeNewBee() {
        for (Bee bee : mBees) {
            if (!bee.isVisible()) {
                // 蜂の発生位置を乱数で決める
                int x = mRand.nextInt(mWorldWidth - 20);
                bee.setLocate(x, mWorldHeight);
                // 蜂の速度を乱数で決める
                int dy = -mRand.nextInt(5) - 3;
                bee.setSpeed(0, dy);
                // 表示する
                bee.setVisible(true);
            }
        }
    }
    // ゲーム内処理
    public void process() {
        // ドロイド君を動かす
        mDroid.move();
        // 蜂を動かす
        for (Bee bee : mBees) {
            bee.move();
        }
        // 蜂を新たに生成
        makeNewBee();
    }
    // ゲーム中のオブジェクトclass 移動や衝突判定のメソッドを提供する
    private class GameObject {
        private int mX;
        private int mY;
        private int mWidth;
        private int mHeight;
        private int mDx;
        private int mDy;
        private boolean mVisible;
        // オブジェクトのコンストラクタ
        public GameObject(int x, int y, int w, int h, int dx, int dy, boolean visible) {
            setLocate(x, y);
            mWidth = w;
            mHeight = h;
            setSpeed(dx, dy);
            setVisible(visible);
        }
        // 表示中フラグ設定
        public void setVisible(boolean v) {
            mVisible = v;
        }
        // 表示中か判定
        public boolean isVisible() {
            return mVisible;
        }
        // X座標取得
        public int getX() {
            return mX;
        }
        // Y座標取得
        public int getY() {
            return mY;
        }
        // 座標設定
        public void setLocate(int x, int y) {
            mX = x;
            mY = y;
        }
        // 右端のX座標取得
        public int getRight() {
            return mX + mWidth;
        }
        // 底辺のY座標取得
        public int getBottom() {
            return mY + mHeight;
        }
        // 速度設定
        public void setSpeed(int dx, int dy) {
            mDx = dx;
            mDy = dy;
        }
        // 移動
        public void move() {
            mX += mDx;
            if (mX < 0) {
                mX = mWorldWidth;
            } else if (mX > mWorldWidth) {
                mX = 0;
            }
            mY += mDy;
            if (mY < 0) {
                mY = mWorldHeight;
            } else if (mY > mWorldHeight) {
                mY = 0;
            }
        }
        // 衝突判定 (return true:衝突した)
        public boolean collisionCheck(GameObject other) {
            if (other == null) {
                return false;
            }
            if (!other.isVisible()) {
                return false;
            }
            if ((this.getX() < other.getRight()) && (this.getY() < other.getBottom())
                    && (this.getRight() > other.getX()) && (this.getBottom() > other.getY())) {
                return true;
            }
            return false;
        }
    }
    // ドロイド君クラス
    public class Droid extends GameObject {
        public Droid(int w, int h) {
            super(0, 0, w, h, 5, 5, true);
        }
 // ドロイド君の移動処理
        public void move() {
            if (!isVisible()) {
                // 非表示なら処理しない
                return;
            }
            // ドロイド君を移動
            super.move();
            // 蜂との衝突チェック
            for (Bee bee : mBees) {
                if (collisionCheck(bee)) {
                    // 当たった
                    // 爆発音を鳴らす
                    // 撃墜数加算
                    scoreAdd(1);
                    mSoundManager.play(SoundManager.SOUND_EXPLO, 100);
                    // 当たった蜂を消す
                    bee.setVisible(false);
                }
            }
        }
    }
    // 蜂クラス
    public class Bee extends GameObject {
        public Bee(int w, int h) {
            super(0, 0, w, h, 0, 0, false);
        }
        // 蜂の移動処理
        public void move() {
            if (!isVisible()) {
                // 非表示なら処理しない
                return;
            }
            // 蜂を移動
            super.move();
            // ドロイド君との衝突チェック
            if (collisionCheck(mDroid)) {
                // 当たった
                // 爆発音を鳴らす
                scoreAdd(1);
                mSoundManager.play(SoundManager.SOUND_EXPLO, 100);
                // 当たったので蜂を消す
                setVisible(false);
            }
        }
    }
    // 撃墜数加算
    private void scoreAdd(int n) {
        mShotDownCount += n;
    }
    // ゲーム得点を取得
    public int getScore() {
        return mShotDownCount;
    }
    // 撃墜数初期化
    public void resetScore() {
        mShotDownCount = 0;
    }

}
