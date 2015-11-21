package com.andy.LuFM.controller;

import com.andy.LuFM.Utils.PlayStatus;

/**
 * Created by wanglu on 15/11/20.
 */
public class PlayerAgent {
    private static final String Tag = "PlayerAgent";
    private static PlayerAgent instance;

    private int mPlaySource = 0;
    private int currPlayState = PlayStatus.INIT;

    private PlayerAgent() {
        //   FMcontrol.getInstance().addListener(this);
        //  NetWorkManage.getInstance().addListener(this);
    }

    public static synchronized PlayerAgent getInstance() {
        PlayerAgent playerAgent;
        synchronized (PlayerAgent.class) {
            if (instance == null) {
                instance = new PlayerAgent();
            }
            playerAgent = instance;
        }
        return playerAgent;
    }

    public void addPlaySource(int source) {
        this.mPlaySource = source;
    }
    public boolean isPlaying() {
        return this.currPlayState == PlayStatus.PLAY;
    }

}
