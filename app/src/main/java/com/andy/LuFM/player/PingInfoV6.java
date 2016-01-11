package com.andy.LuFM.player;


import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class PingInfoV6 {
    public static final String BITRATE = "${BITRATE}";
    public static final String DEVICEID = "${DEVICEID}";
    public static final String RES_ID = "${res_id}";
    public static final String START = "${START}";
    public static final String END = "${END}";
    public static final String FILE_PATH = "${file_path}";
    public static final String PBITRATE = Pattern.quote(BITRATE);
    public static final String PDEVICEID = Pattern.quote(DEVICEID);
    public static final String PEND = Pattern.quote(END);
    public static final String PFILE_PATH = Pattern.quote(FILE_PATH);
    public static final String PRES_ID = Pattern.quote(RES_ID);
    public static final String PSTART = Pattern.quote(START);

    public String accessExp;
    public String backupIP;
    public int channelType;
    public String codename;
    public String domain;
    private String domainIP = null;
    private boolean hasPinged = false;
    public boolean isCDN;
    private List<String> lstBackupIP;
    private int mBackupIPIndex = -1;
    private double mReachTime = 0.0d;
    private double mResult = 0.0d;
    public double pcc = 0.0d;
    private int reachCnt = 0;
    public String replayExp;
    public String res;
    public String testpath;
    public int weight;

    public void update(PingInfoV6 right) {
        if (right != null) {
            this.domain = right.domain;
            this.testpath = right.testpath;
            this.res = right.res;
            this.codename = right.codename;
            this.weight = right.weight;
            this.channelType = right.channelType;
            this.isCDN = right.isCDN;
            this.pcc = right.pcc;
            this.lstBackupIP = right.lstBackupIP;
            this.backupIP = right.backupIP;
            this.accessExp = right.accessExp;
            this.replayExp = right.replayExp;
            this.domainIP = right.domainIP;
            this.hasPinged = right.hasPinged;
            this.mReachTime = right.mReachTime;
            this.reachCnt = right.reachCnt;
            this.mBackupIPIndex = right.mBackupIPIndex;
        }
    }

    public void setPinged(boolean flag) {
        this.hasPinged = flag;
    }

    public boolean hasPinged() {
        return this.hasPinged;
    }

    public String getReplayUrl(String resId, String deviceId, int bitrate, String start, String end) {
        if (this.channelType == 1) {
            return getAccessUrl(resId, deviceId, bitrate);
        }
        if (resId == null || resId.equalsIgnoreCase("")) {
            return null;
        }
        if (this.replayExp == null) {
            this.replayExp = this.accessExp;
        }
        if (start == null || end == null) {
            return null;
        }
        String url = this.replayExp;
        if (!url.contains(RES_ID)) {
            return null;
        }
        url = url.replaceAll(PRES_ID, resId);
        if (url.contains(START)) {
            url = url.replaceAll(PSTART, start);
        } else {
            url = new StringBuilder(String.valueOf(url)).append("&start=").append(start).toString();
        }
        if (url.contains(END)) {
            url = url.replaceAll(PEND, end);
        } else {
            url = new StringBuilder(String.valueOf(url)).append("&end=").append(end).toString();
        }
        if (url.contains(BITRATE)) {
            url = url.replaceAll(PBITRATE, String.valueOf(bitrate));
        }
        if (deviceId == null) {
            return url.replaceAll(PDEVICEID, "unknown");
        }
        if (url.contains(DEVICEID)) {
            return url.replaceAll(PDEVICEID, deviceId);
        }
        return url;
    }

    public String getAccessUrl(String resId, String deviceId, int bitrate) {
        if (this.accessExp == null || resId == null || resId.equalsIgnoreCase("")) {
            return null;
        }
        String url = this.accessExp;
        if (resId != null) {
            if (this.channelType == 0) {
                if (!url.contains(RES_ID)) {
                    return null;
                }
                url = url.replaceAll(PRES_ID, resId);
            } else if (!url.contains(FILE_PATH)) {
                return null;
            } else {
                url = url.replaceAll(PFILE_PATH, resId);
            }
        }
        if (url.contains(BITRATE)) {
            url = url.replaceAll(PBITRATE, String.valueOf(bitrate));
        }
        if (deviceId == null) {
            return url.replaceAll(PDEVICEID, "unknown");
        }
        if (url.contains(DEVICEID)) {
            return url.replaceAll(PDEVICEID, deviceId);
        }
        return url;
    }

    public String getPKURL(String domainIP) {
        if (domainIP == null) {
            return null;
        }
        return "http://" + domainIP + this.testpath;
    }

    public double getResult() {
        return this.mResult;
    }

    public void setResult(double res) {
        this.mResult = res;
    }

    public double getReachTime() {
        if (this.reachCnt == 0) {
            return 2.147483647E9d;
        }
        return this.mReachTime / ((double) this.reachCnt);
    }

    public void setReachTime(double time) {
        this.reachCnt++;
        this.mReachTime += time;
    }

    public String getDomain() {
        return this.domain;
    }

    private String getNextBackupIp() {
        if (this.backupIP == null) {
            return null;
        }
        if (this.lstBackupIP == null) {
            this.lstBackupIP = new ArrayList();
        }
        if (this.lstBackupIP.size() == 0) {
            String[] sources = this.backupIP.split(";");
            if (sources != null && sources.length != 0) {
                for (Object add : sources) {
                    this.lstBackupIP.add((String) add);
                }
            } else if (this.backupIP == null) {
                return null;
            } else {
                this.lstBackupIP.add(this.backupIP);
                return this.backupIP;
            }
        }
        if (this.lstBackupIP.size() <= 0) {
            return null;
        }
        this.mBackupIPIndex++;
        if (this.mBackupIPIndex < this.lstBackupIP.size()) {
            return (String) this.lstBackupIP.get(this.mBackupIPIndex);
        }
        this.mBackupIPIndex = 0;
        return (String) this.lstBackupIP.get(this.mBackupIPIndex);
    }

    public String getDomainIP() {
        if (this.isCDN) {
            return this.domain;
        }
        if (this.domainIP != null) {
            return this.domainIP;
        }
        if (this.domain != null) {
            try {
                this.domainIP = InetAddress.getByName(this.domain).getHostAddress();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (this.domainIP == null) {
            return getNextBackupIp();
        }
        return this.domainIP;
    }
}
