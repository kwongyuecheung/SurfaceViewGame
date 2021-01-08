package com.e.surfaceviewgame;

import android.media.SoundPool;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
public class SoundManager {
    public static final int SOUND_GUN = 0;
    public static final int SOUND_EXPLO = 1;
    // 利用する効果音を列挙する
    private static final int[] SoundList = {
            R.raw.gun, R.raw.explo
    };
    // 効果音を鳴らすSoundPool
    private SoundPool mSoundPool;
    // 効果音のテーブル
    private int mSoundTable[] = new int[SoundList.length];
    // コンストラクタで効果音の読み込み
    public SoundManager(Context context) {
        // SoundPoolの初期化
        mSoundPool = new SoundPool(SoundList.length, AudioManager.STREAM_MUSIC, 0);
        // SoundPoolを使って効果音をロードし、戻り値のIDをmSoundTableに保存する
        for (int i = 0; i < SoundList.length; i++) {
            mSoundTable[i] = mSoundPool.load(context, SoundList[i], 1);
        }
    }
    // 効果音の再生
    public void play(int no, int vol) {
        if (no < 0 || no >= mSoundTable.length) {
            return;
        }
        float fvol = vol / 100;

        mSoundPool.play(mSoundTable[no], fvol, fvol, 0, 0, 1.0f);
    }
    // 効果音の解放
    public void release() {
        // 読み込まれていた効果音を解放する
        for (int i = 0; i < mSoundTable.length; i++) {
            mSoundPool.unload(mSoundTable[i]);
        }
        mSoundPool.release();
    }
}
