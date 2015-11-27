package com.andy.LuFM.test;

import android.util.Log;

import com.andy.LuFM.data.InfoManager;
import com.andy.LuFM.helper.ChannelHelper;
import com.andy.LuFM.model.ChannelNode;
import com.andy.LuFM.model.Node;
import com.andy.LuFM.model.ProgramNode;

import java.util.List;

/**
 * Created by Andy.Wang on 2015/11/27.
 */
public class PlayerAgent {
    static PlayerAgent instance;


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

    public void play(Node node) {
        if (node != null) {
            // requestAudioFocus();
            // stopFM();
            // setLoadedAndPlayId(0, 0);
            String url;
           /* if (node.nodeName.equalsIgnoreCase(BaseConstants.X_COMMAND_CHANNEL)) {
                ChannelNode temp = (ChannelNode) node;
                if (temp.channelType == 1) {
                    List<ProgramNode> lstNodes = temp.getAllLstProgramNode();
                    if (lstNodes != null && lstNodes.size() > 0) {
                        play((Node) lstNodes.get(0));
                        return;
                    }
                    return;
                }
                InfoManager.getInstance().root().setPlayingChannelNode(node);
                url = temp.getSourceUrl();
                ProgramNode program;
                if (url == null || url.equalsIgnoreCase(bi.b)) {
                    if (this.hasRecoveredFromCrash) {
                        this.hasRecoveredFromCrash = false;
                    }
                    program = temp.getProgramNodeByTime(System.currentTimeMillis());
                    if (program != null) {
                        if (program.getCurrPlayStatus() == 3) {
                            url = program.getSourceUrl();
                        } else {
                            url = program.getSourceUrl();
                        }
                        if (!(url == null || url.equalsIgnoreCase(bi.b))) {
                            if (!playFrontAudioAdv(program)) {
                                InfoManager.getInstance().root().setPlayMode();
                                if (_play(url) && program.getCurrPlayStatus() == 3) {
                                    autoSeek(program.id);
                                }
                            }
                            if (program.getCurrPlayStatus() == 3) {
                                this.liveStream = false;
                            } else {
                                this.liveStream = true;
                            }
                            InfoManager.getInstance().root().setPlayingNode(program);
                            RemoteControl.getInstance().updateProgramInfo(this._context, temp, program);
                        }
                    }
                } else {
                    program = temp.getProgramNodeByTime(System.currentTimeMillis());
                    if (program != null && program.getCurrPlayStatus() == 1) {
                        url = program.getSourceUrl();
                    }
                    if (!playFrontAudioAdv(program)) {
                        InfoManager.getInstance().root().setPlayMode();
                        _play(url);
                    }
                    this.liveStream = true;
                    if (program != null) {
                        InfoManager.getInstance().root().setPlayingNode(program);
                        RemoteControl.getInstance().updateProgramInfo(this._context, temp, program);
                    } else {
                        InfoManager.getInstance().root().setPlayingNode(temp);
                    }
                }
            } else*/
            if (node.nodeName.equalsIgnoreCase("program")) {
                ProgramNode temp2 = (ProgramNode) node;
                ChannelNode cn = ChannelHelper.getInstance().getChannel(temp2);
                if (cn != null) {
                    InfoManager.getInstance().root().setPlayingChannelNode(cn);
                }
                url = "";
            /*    if (temp2.getCurrPlayStatus() == 3) {
                    *//*if (!temp2.isDownloadProgram()) {
                        url = InfoManager.getInstance().root().getLocalProgramSource(temp2);
                    }*//*
                    if (url == null || url.equalsIgnoreCase("")) {
                        String cache = PlayCacheAgent.getInstance().getCache(temp2);
                        if (cache != null) {
                            url = cache;
                        } else {
                            PlayCacheAgent.getInstance().cacheNode(temp2);
                            url = temp2.getSourceUrl();
                        }
                    }
                } else {
                    url = temp2.getSourceUrl();
                }*/
                url = temp2.getLowBitrateSource();
                /*if (!(url == null || url.equalsIgnoreCase(""))) {
                    if (this.currPlayState == 1 && (this.source == null || this.source.equalsIgnoreCase(url))) {
                        _resume();
                    } else if (!playFrontAudioAdv(temp2)) {
                        InfoManager.getInstance().root().setPlayMode();
                        if (_play(url) && temp2.getCurrPlayStatus() == 3) {
                           // autoSeek(temp2.id);
                        }
                    }
                    if (temp2.getCurrPlayStatus() == 3) {
                        this.liveStream = false;
                    } else {
                        this.liveStream = true;
                    }
                    InfoManager.getInstance().root().setPlayingNode(temp2);
                    RemoteControl.getInstance().updateProgramInfo(this._context, cn, temp2);
                }*/
                Log.i("Sync", "aurl:" + url);
                _play(url);
            } else if (node.nodeName.equalsIgnoreCase("ringtone")) {
                // playRingTone(node);
                // InfoManager.getInstance().root().setPlayMode(PlayMode.ALARM_PLAY_ONLINE);
            }
            // this.currPlayState = EducationType.TOP;
            //   InfoManager.getInstance().runSellApps();
            // DoubleClick.getInstance().visitButton("\u64ad\u653e\u524d");
        }
    }

    private boolean _play(String source) {
      /*  if (this.iService == null) {
            return false;
        }
        if (source == null && this.source == null) {
            return false;
        }
        if (this.source != null && this.source.equalsIgnoreCase(source) && this.currPlayState == EducationType.TOP) {
            return false;
        }
        long position = (long) queryPosition();
        long duration = (long) queryDuration();
        if (source != null) {
            this.source = source;
        }
        if (!(this.source == null || this.source.equalsIgnoreCase(this.preloaded_source))) {
            setSource(this.source);
        }
        dispatchPlayState(PlayStatus.BUFFER_CUSTOM);
        autoReserve();
        this.beginPlay = System.currentTimeMillis() / 1000;
        this.beginFromPlay = System.currentTimeMillis();
        this.recvDoPlay = false;
        this.mConnected = false;
        try {
            if ((InfoManager.getInstance().root().currentPlayMode() == PlayMode.PLAY_FRONT_ADVERTISEMENT || InfoManager.getInstance().root().currentPlayMode() == PlayMode.PLAY_END_ADVERTISEMENT) && InfoManager.getInstance().getTaobaoAudioAdv()) {
                TaobaoAgent.getInstance().stopAD();
            }
            this.hasPlayed = true;
            this.iService.play();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        PlayedMetaInfo.getInstance().addPlayedMeta(InfoManager.getInstance().root().getCurrentPlayingNode(), (int) position, (int) duration);
        if (this.liveStream) {
            this.playDuration = 0;
        } else {
            this.playDuration = queryDuration();
        }*/
        return true;
    }

}
