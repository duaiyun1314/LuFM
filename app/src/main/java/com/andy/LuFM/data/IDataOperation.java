package com.andy.LuFM.data;

/**
 * Created by Andy.Wang on 2015/11/11.
 */
public interface IDataOperation {
    String dataRequestName();

    Result doCommand(DataCommand dataCommand, IResultRecvHandler iDataRecvHandler);

}
