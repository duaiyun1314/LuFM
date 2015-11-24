package com.andy.LuFM.helper;

import com.andy.LuFM.model.Node;
import com.andy.LuFM.model.ProgramNode;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wanglu on 15/11/24.
 */
public class ProgramHelper extends Node {
    static final String TAG = "ProgramHelper";
    private static ProgramHelper _instance = null;
    private Map<Integer, ProgramScheduleList[]> mapProgramNodes;
    public Map<Integer, Boolean> mapUpdatePrograms;
    public transient ProgramNode programNodeTemp;

    private ProgramHelper() {
        this.mapProgramNodes = new HashMap();
        this.mapUpdatePrograms = new HashMap();
        this.nodeName = "programhelper";
    }

    public static ProgramHelper getInstance() {
        if (_instance == null) {
            _instance = new ProgramHelper();
        }
        return _instance;
    }
}
