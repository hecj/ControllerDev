package dev.controller.hecj.cn.controllerdev.util;

import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * socket读取工具类
 * by hecj
 */
public class SocketUtil {
    /**
     * 读请求头
     */
    public static PacketHeader readPacketHeader(ByteBuffer headerBuffer) throws Exception {
        // 读请求头
        headerBuffer.position(0);
        PacketHeader packetHeader = new PacketHeader();
        packetHeader.setType(headerBuffer.getShort());
        packetHeader.setVersion(headerBuffer.get());
        packetHeader.setTime(headerBuffer.getLong());
        packetHeader.setLength(headerBuffer.getInt());
        return packetHeader;
    }

    /**
     * 读请求头
     */
    public static PacketHeader readPacketHeader(InputStream inputStream, ByteBuffer headerBuffer, int length) throws Exception {
        // 读请求头
        int len = 0;
        while(len < length){
            int temp = inputStream.read(headerBuffer.array(),len,length-len);
            if(temp > 0){
                len += temp;
            }
        }
        PacketHeader packetHeader = new PacketHeader();
        packetHeader.setType(headerBuffer.getShort());
        packetHeader.setVersion(headerBuffer.get());
        packetHeader.setTime(headerBuffer.getLong());
        packetHeader.setLength(headerBuffer.getInt());
        return packetHeader;
    }
    /**
     * 读视频帧
     */
    public static void read(InputStream inputStream, ByteBuffer frameBuffer, int length) throws Exception {
        int pos = 0;
        while(pos < length){
            int temp = inputStream.read(frameBuffer.array(),pos,length-pos);
            if(temp > 0){
                pos += temp;
            }
        }
    }

}
