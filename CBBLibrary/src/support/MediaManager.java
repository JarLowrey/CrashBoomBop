package support;

import android.content.Context;
import android.media.MediaPlayer;

//source: http://stackoverflow.com/questions/18254870/play-a-sound-from-res-raw
public class MediaManager {

    private MediaPlayer mMediaPlayer;
    public boolean donePlayingSoundClip=true;

    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();//this should be release, not reset
            mMediaPlayer = null;
        }
    }

    public void playSoundClip(Context c, int rid) {
        stop();
        donePlayingSoundClip=false;

        mMediaPlayer = MediaPlayer.create(c, rid);
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stop();
                donePlayingSoundClip=true;
            }
        });

        mMediaPlayer.start();
    }

}