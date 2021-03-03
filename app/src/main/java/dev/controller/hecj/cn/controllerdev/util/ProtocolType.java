package dev.controller.hecj.cn.controllerdev.util;

public enum ProtocolType {

    VideoFrame((short) 1000),Audio((short) 1001),Input_Controll((short) 1002),DeviceMessage((short) 1011),SynTime((short) 1010);

    public short type;
    ProtocolType(short type){
        this.type = type;
    }

}
