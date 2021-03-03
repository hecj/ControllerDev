package dev.controller.hecj.cn.controllerdev.util;

public enum DataProtocol {

    // 老协议
    VideoFrame(new byte[]{3,(byte)232,1}),
//    Audio(new byte[]{3,(byte)233,1}),
    DeviceMessage(new byte[]{3,(byte)243,1}),
    SynTime(new byte[]{3,(byte)242,1}),

    // 新协议
    Video(new byte[]{0,0,2}),
    Audio(new byte[]{0,0,3}),
    Controller(new byte[]{0,0,9}),

    // 分辨率
    Resoletion(new byte[]{0,1,0}),
    // 比特率
    BitRate(new byte[]{0,1,1}),

    // 心跳检测
    HeatBeat(new byte[]{9,9,9}),
    // 同步时间
    SynTimeTs(new byte[]{0,1,7}),
    // 返回服务端和客户端间隔时间
    SynTimeTsBack(new byte[]{0,1,8});

    public byte[] type;
    DataProtocol(byte[] bytes){
        this.type = bytes;
    }

}
