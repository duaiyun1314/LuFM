package com.andy.LuFM;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MergeCursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Toast;

import com.andy.LuFM.test.AudioPlaybackService;
import com.andy.LuFM.test.NowPlayingActivity;
import com.andy.LuFM.test.PlaybackKickstarter;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Singleton class that provides access to common objects
 * and methods used in the application.
 *
 * @author Saravan Pantham
 */
public class TestApplication extends LuFmApplication {

    public static TestApplication mApp;
    //Context.
    private Context mContext;

    //Service reference and flags.
    private AudioPlaybackService mService;
    private boolean mIsServiceRunning = false;

    //Playback kickstarter object.
    private PlaybackKickstarter mPlaybackKickstarter;

    //NowPlayingActivity reference.
    private NowPlayingActivity mNowPlayingActivity;

    //SharedPreferences.
    private static SharedPreferences mSharedPreferences;


    //Indicates if the library is currently being built.
    private boolean mIsBuildingLibrary = false;
    private boolean mIsScanFinished = false;

    //Google Play Music access object.
    private boolean mIsGMusicLoggedIn = false;


    //ImageLoader/ImageLoaderConfiguration objects for ListViews and GridViews.
    private ImageLoader mImageLoader;
    private ImageLoaderConfiguration mImageLoaderConfiguration;

    //Image display options.
    private DisplayImageOptions mDisplayImageOptions;

    //Cursor that stores the songs that are currently queued for download.
    private Cursor mPinnedSongsCursor;

    //Specifies whether the app is currently downloading pinned songs from the GMusic app.
    private boolean mIsFetchingPinnedSongs = false;

    public static final String uid4 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvFlvWGADp9cW2LPuOIjDPB";
    public static final String uid2 = "ormNR2mpS8HR8utvhNHKs2AJzV8GLPh35m3rE6GPND4GsOdrbySPETG4+0fvagBr5E";
    public static final String uid6 = "QgMR7z76DJlRqy+VyVzmx7cly2JiXo+ZnISYKKn71oP+Xw+dO/eRKFy3EFCO7khMxc";
    public static final String uid1 = "6QouPH11nHJPzXspzdkJbTcifIIGFtEkquXjA0y19Gouab7Gir8yLOA4V3m0URRivP";
    public static final String uid3 = "QeOx8JsY766F6FgU8uJABWRDZbqHEYRwT7iGmn7ukt7h5z+DOsYWSRmZxwJh3cpkGo";
    public static final String uid5 = "Vyqp4UZWnzGiiq/fWFKs5rrc+m3obsEpUxteGavKAhhXJZKgwAGFgkUQIDAQAB";

    //GAnalytics flag.
    private boolean mIsGAnalyticsEnabled = true;

    //Broadcast elements.
    private LocalBroadcastManager mLocalBroadcastManager;
    public static final String UPDATE_UI_BROADCAST = "com.jams.music.player.NEW_SONG_UPDATE_UI";

    //Update UI broadcast flags.
    public static final String SHOW_AUDIOBOOK_TOAST = "AudiobookToast";
    public static final String UPDATE_SEEKBAR_DURATION = "UpdateSeekbarDuration";
    public static final String UPDATE_PAGER_POSTIION = "UpdatePagerPosition";
    public static final String UPDATE_PLAYBACK_CONTROLS = "UpdatePlabackControls";
    public static final String SERVICE_STOPPING = "ServiceStopping";
    public static final String SHOW_STREAMING_BAR = "ShowStreamingBar";
    public static final String HIDE_STREAMING_BAR = "HideStreamingBar";
    public static final String UPDATE_BUFFERING_PROGRESS = "UpdateBufferingProgress";
    public static final String INIT_PAGER = "InitPager";
    public static final String NEW_QUEUE_ORDER = "NewQueueOrder";
    public static final String UPDATE_EQ_FRAGMENT = "UpdateEQFragment";

    //Contants for identifying each fragment/activity.
    public static final String FRAGMENT_ID = "FragmentId";
    public static final int ARTISTS_FRAGMENT = 0;
    public static final int ALBUM_ARTISTS_FRAGMENT = 1;
    public static final int ALBUMS_FRAGMENT = 2;
    public static final int SONGS_FRAGMENT = 3;
    public static final int PLAYLISTS_FRAGMENT = 4;
    public static final int GENRES_FRAGMENT = 5;
    public static final int FOLDERS_FRAGMENT = 6;
    public static final int ARTISTS_FLIPPED_FRAGMENT = 7;
    public static final int ARTISTS_FLIPPED_SONGS_FRAGMENT = 8;
    public static final int ALBUM_ARTISTS_FLIPPED_FRAGMENT = 9;
    public static final int ALBUM_ARTISTS_FLIPPED_SONGS_FRAGMENT = 10;
    public static final int ALBUMS_FLIPPED_FRAGMENT = 11;
    public static final int GENRES_FLIPPED_FRAGMENT = 12;
    public static final int GENRES_FLIPPED_SONGS_FRAGMENT = 13;

    //Constants for identifying playback routes.
    public static final int PLAY_ALL_SONGS = 0;
    public static final int PLAY_ALL_BY_ARTIST = 1;
    public static final int PLAY_ALL_BY_ALBUM_ARTIST = 2;
    public static final int PLAY_ALL_BY_ALBUM = 3;
    public static final int PLAY_ALL_IN_PLAYLIST = 4;
    public static final int PLAY_ALL_IN_GENRE = 5;
    public static final int PLAY_ALL_IN_FOLDER = 6;

    //Device orientation constants.
    public static final int ORIENTATION_PORTRAIT = 0;
    public static final int ORIENTATION_LANDSCAPE = 1;

    //Device screen size/orientation identifiers.
    public static final String REGULAR = "regular";
    public static final String SMALL_TABLET = "small_tablet";
    public static final String LARGE_TABLET = "large_tablet";
    public static final String XLARGE_TABLET = "xlarge_tablet";
    public static final int REGULAR_SCREEN_PORTRAIT = 0;
    public static final int REGULAR_SCREEN_LANDSCAPE = 1;
    public static final int SMALL_TABLET_PORTRAIT = 2;
    public static final int SMALL_TABLET_LANDSCAPE = 3;
    public static final int LARGE_TABLET_PORTRAIT = 4;
    public static final int LARGE_TABLET_LANDSCAPE = 5;
    public static final int XLARGE_TABLET_PORTRAIT = 6;
    public static final int XLARGE_TABLET_LANDSCAPE = 7;

    //Miscellaneous flags/identifiers.
    public static final String SONG_ID = "SongId";
    public static final String SONG_TITLE = "SongTitle";
    public static final String SONG_ALBUM = "SongAlbum";
    public static final String SONG_ARTIST = "SongArtist";
    public static final String ALBUM_ART = "AlbumArt";
    public static final String CURRENT_THEME = "CurrentTheme";
    public static final int DARK_THEME = 0;
    public static final int LIGHT_THEME = 1;

    //SharedPreferences keys.
    public static final String CROSSFADE_ENABLED = "CrossfadeEnabled";
    public static final String CROSSFADE_DURATION = "CrossfadeDuration";
    public static final String REPEAT_MODE = "RepeatMode";
    public static final String MUSIC_PLAYING = "MusicPlaying";
    public static final String SERVICE_RUNNING = "ServiceRunning";
    public static final String CURRENT_LIBRARY = "CurrentLibrary";
    public static final String CURRENT_LIBRARY_POSITION = "CurrentLibraryPosition";
    public static final String SHUFFLE_ON = "ShuffleOn";
    public static final String FIRST_RUN = "FirstRun";
    public static final String STARTUP_BROWSER = "StartupBrowser";
    public static final String SHOW_LOCKSCREEN_CONTROLS = "ShowLockscreenControls";
    public static final String ARTISTS_LAYOUT = "ArtistsLayout";
    public static final String ALBUM_ARTISTS_LAYOUT = "AlbumArtistsLayout";
    public static final String ALBUMS_LAYOUT = "AlbumsLayout";
    public static final String PLAYLISTS_LAYOUT = "PlaylistsLayout";
    public static final String GENRES_LAYOUT = "GenresLayout";
    public static final String FOLDERS_LAYOUT = "FoldersLayout";

    //Repeat mode constants.
    public static final int REPEAT_OFF = 0;
    public static final int REPEAT_PLAYLIST = 1;
    public static final int REPEAT_SONG = 2;
    public static final int A_B_REPEAT = 3;

    public static TestApplication from() {
        if (mApp != null) {
            return mApp;
        }
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;

        //Application context.
        mContext = getApplicationContext();

        //SharedPreferences.
        mSharedPreferences = this.getSharedPreferences("com.jams.music.player", Context.MODE_PRIVATE);


        //Playback kickstarter.
        mPlaybackKickstarter = new PlaybackKickstarter(this.getApplicationContext());


        //ImageLoader.
        mImageLoader = ImageLoader.getInstance();
        mImageLoaderConfiguration = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .memoryCache(new WeakMemoryCache())
                .memoryCacheSizePercentage(13)
                        //     .imageDownloader(new ByteArrayUniversalImageLoader(mContext))
                .build();
        mImageLoader.init(mImageLoaderConfiguration);

        //Init DisplayImageOptions.
        //  initDisplayImageOptions();

        //Log the user into Google Play Music only if the account is currently set up and active.
        if (mSharedPreferences.getBoolean("GOOGLE_PLAY_MUSIC_ENABLED", false) == true) {

            //Create a temp WebView to retrieve the user agent string.
            String userAgentString = "";
            if (mSharedPreferences.getBoolean("GOT_USER_AGENT", false) == false) {
                WebView webView = new WebView(getApplicationContext());
                webView.setVisibility(View.GONE);
                webView.loadUrl("http://www.google.com");
                userAgentString = webView.getSettings().getUserAgentString();
                mSharedPreferences.edit().putBoolean("GOT_USER_AGENT", true).commit();
                mSharedPreferences.edit().putString("USER_AGENT", userAgentString).commit();
                webView = null;
            }

            String accountName = mSharedPreferences.getString("GOOGLE_PLAY_MUSIC_ACCOUNT", "");


        }

    }

    /**
     * Initializes a DisplayImageOptions object. The drawable shown
     * while an image is loading is based on the current theme.
     */
    public void initDisplayImageOptions() {

        //Create a set of options to optimize the bitmap memory usage.
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inJustDecodeBounds = false;
        options.inPurgeable = true;

        int emptyColorPatch = 0;
        mDisplayImageOptions = null;
        mDisplayImageOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(emptyColorPatch)
                .showImageOnFail(emptyColorPatch)
                .showImageOnLoading(emptyColorPatch)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .decodingOptions(options)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .bitmapConfig(Bitmap.Config.ARGB_4444)
                .delayBeforeLoading(400)
                .displayer(new FadeInBitmapDisplayer(200))
                .build();

    }

    /**
     * Sends out a local broadcast that notifies all receivers to update
     * their respective UI elements.
     */
    public void broadcastUpdateUICommand(String[] updateFlags, String[] flagValues) {
        Intent intent = new Intent(UPDATE_UI_BROADCAST);
        for (int i = 0; i < updateFlags.length; i++) {
            intent.putExtra(updateFlags[i], flagValues[i]);
        }

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(mContext);
        mLocalBroadcastManager.sendBroadcast(intent);

    }


    /**
     * Returns the orientation of the device.
     */
    public int getOrientation() {
        if (getResources().getDisplayMetrics().widthPixels >
                getResources().getDisplayMetrics().heightPixels) {
            return ORIENTATION_LANDSCAPE;
        } else {
            return ORIENTATION_PORTRAIT;
        }

    }

    public SharedPreferences getSharedPreferences() {
        return mSharedPreferences;
    }


    public AudioPlaybackService getService() {
        return mService;
    }

    public NowPlayingActivity getNowPlayingActivity() {
        return mNowPlayingActivity;
    }


    public boolean isServiceRunning() {
        return mIsServiceRunning;
    }


    public boolean isCrossfadeEnabled() {
        return getSharedPreferences().getBoolean(CROSSFADE_ENABLED, false);
    }

    public int getCrossfadeDuration() {
        return getSharedPreferences().getInt(CROSSFADE_DURATION, 5);
    }


    public PlaybackKickstarter getPlaybackKickstarter() {
        return mPlaybackKickstarter;
    }


    public void setService(AudioPlaybackService service) {
        mService = service;
    }


    public void setIsServiceRunning(boolean running) {
        mIsServiceRunning = running;
    }


}
