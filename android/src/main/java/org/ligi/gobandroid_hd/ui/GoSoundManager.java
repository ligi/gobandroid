package org.ligi.gobandroid_hd.ui;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;
import org.ligi.gobandroid_hd.ui.application.GobandroidSettings;
import org.ligi.tracedroid.logging.Log;

import java.util.HashMap;

/**
 * Class to care about the Board-Sounds
 *
 * @author <a href="http://ligi.de">Marcus -LiGi- Bueschleb </a>
 *         <p/>
 *         This software is licenced with GPLv3
 */
public class GoSoundManager {

    private final GobandroidFragmentActivity context;
    private final GobandroidSettings settings;

    private SoundPool mSoundPool;
    private HashMap<Integer, Integer> mSoundPoolMap;
    private AudioManager mAudioManager;


    public final static int SOUND_START = 1;
    public final static int SOUND_END = 2;
    public final static int SOUND_PLACE1 = 3;
    public final static int SOUND_PLACE2 = 4;
    public final static int SOUND_PICKUP1 = 5;
    public final static int SOUND_PICKUP2 = 6;

    public GoSoundManager(GobandroidFragmentActivity context, GobandroidSettings settings) {
        this.settings = settings;
        Log.i("sound_man init");
        this.context = context;
        mSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
        mSoundPoolMap = new HashMap<>();
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        addSound(SOUND_START, R.raw.go_start);
        addSound(SOUND_END, R.raw.go_clearboard);
        addSound(SOUND_PLACE1, R.raw.go_place1);
        addSound(SOUND_PLACE2, R.raw.go_place2);
        addSound(SOUND_PICKUP1, R.raw.go_pickup1);
        addSound(SOUND_PICKUP2, R.raw.go_pickup2);
    }

    public void addSound(int index, int SoundID) {
        mSoundPoolMap.put(index, mSoundPool.load(context, SoundID, 1));
    }

    public void playSound(int index) {
        if (!settings.isSoundEnabled())
            return; // user does not want sound

        float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        streamVolume = streamVolume / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mSoundPool.play(mSoundPoolMap.get(index), streamVolume, streamVolume, 1, 0, 1f);
    }

}
