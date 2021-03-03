package dev.controller.hecj.cn.controllerdev.util;

import java.io.Serializable;

/**
 * by hecj
 * 包头(类型，版本，时间，包体长度)
 */
public class PacketHeader implements Serializable {

    private short type;
    private byte version;
    private long time;
    private int length;

    public short getType() {
        return type;
    }

    public void setType(short type) {
        this.type = type;
    }

    public byte getVersion() {
        return version;
    }

    public void setVersion(byte version) {
        this.version = version;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
