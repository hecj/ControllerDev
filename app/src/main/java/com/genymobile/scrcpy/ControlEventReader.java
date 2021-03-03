package com.genymobile.scrcpy;


import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.os.Message;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import dev.controller.hecj.cn.controllerdev.util.ByteBuff;
import dev.controller.hecj.cn.controllerdev.util.DataProtocol;
import dev.controller.hecj.cn.controllerdev.util.LogUtil;

public class ControlEventReader {

    private static final int KEYCODE_PAYLOAD_LENGTH = 9;
    private static final int MOUSE_PAYLOAD_LENGTH = 13;
    private static final int TOUCH_PAYLOAD_LENGTH = 10;
    private static final int SCROLL_PAYLOAD_LENGTH = 16;
    private static final int COMMAND_PAYLOAD_LENGTH = 1;

    public static final int TEXT_MAX_LENGTH = 300;
    private static final int RAW_BUFFER_SIZE = 1024;

    private final byte[] rawBuffer = new byte[RAW_BUFFER_SIZE];
    private final ByteBuffer buffer = ByteBuffer.wrap(rawBuffer);
    private final byte[] textBuffer = new byte[TEXT_MAX_LENGTH];

    // 存请求头
    private final ByteBuffer headerBuffer = ByteBuffer.allocate(15);

    public ControlEventReader() {
        // invariant: the buffer is always in "get" mode
        buffer.limit(0);
    }

    public boolean isFull() {
        return buffer.remaining() == rawBuffer.length;
    }

    /*
    public void readFrom(InputStream input) throws IOException {
        if (isFull()) {
            throw new IllegalStateException("Buffer full, call next() to consume");
        }
        buffer.compact();
        int head = buffer.position();
        int r = input.read(rawBuffer, head, rawBuffer.length - head);
        if (r == -1) {
            throw new EOFException("Event controller socket closed");
        }
        buffer.position(head + r);
        buffer.flip();
    }
    */
    /*
    public void readFrom(InputStream input) throws Exception {
        if (isFull()) {
            throw new IllegalStateException("Buffer full, call next() to consume");
        }
        if(input.available() < 15){
            return;
        }
        PacketHeader packetHeader = SocketUtil.readPacketHeader(input,headerBuffer,15);
        headerBuffer.clear();
        if(packetHeader.getType() == ProtocolType.Input_Controll.type){
            // 输入控制协议
            buffer.compact();
            int head = buffer.position();
            int r = input.read(rawBuffer, head, packetHeader.getLength());
            if (r == -1) {
                throw new EOFException("Event controller socket closed");
            }
            buffer.position(head + r);
            buffer.flip();
        }
    }
    */
    /*
    private final ByteBuffer dataBuffer = ByteBuffer.allocate(1024);
    public void readFrom(InputStream input) throws Exception {
        if (isFull()) {
            throw new IllegalStateException("Buffer full, call next() to consume");
        }
        int num = input.available();
        // 每次读多少? 特别多就用缓存，现在处理很快，没必要做
        if(num < 15){
            return;
        }
        LogUtil.d("log_读取指令长度:"+num);
        dataBuffer.clear();
        input.read(dataBuffer.array(),0,num);
        headerBuffer.clear();
        headerBuffer.put(dataBuffer.array(),0,15);
        PacketHeader packetHeader = SocketUtil.readPacketHeader(headerBuffer);
        if((headerBuffer.get(0)&0xFF) == 0 && (headerBuffer.get(1)&0xFF)== 0 && (headerBuffer.get(2)&0xFF)== 9){
            // 输入控制协议
            buffer.compact();
            int head = buffer.position();
            System.arraycopy(dataBuffer.array(),15,rawBuffer,head,packetHeader.getLength());
            buffer.position(head + packetHeader.getLength());
            buffer.flip();
        } if((headerBuffer.get(0)&0xFF) == 0 && (headerBuffer.get(1)&0xFF)== 0 && (headerBuffer.get(2)&0xFF)== 8){
            // 设置bitRate
            dataBuffer.position(15);
            int bitRate = dataBuffer.getInt();
            LogUtil.d("usblog_读取指令更改bitRate:"+bitRate);
            Message msg = new Message();
            msg.what = 20001;
            msg.obj = bitRate;
//            serviceHandler.sendMessage(msg);
        }
    }
    */

    // 存请求头
//    private static final ByteBuffer headerBuffer = ByteBuffer.allocate(15);
//    private volatile ByteBuff dataBuffer = new ByteBuff(1024);
//    private static final int HEAD_LENGTH = 15;

    /*
    private void printBuffer(){
        LogUtil.d("dataBuffer ReadPostion:"+dataBuffer.getReadPostion()+", WritePostion:"
                +dataBuffer.getWritePostion()+", Limit:"+dataBuffer.getLimit()+", Avaiable:"
        +dataBuffer.getAvaiable());
    }

    public void readFrom(InputStream input) throws Exception {
        if (isFull()) {
            throw new IllegalStateException("Buffer full, call next() to consume");
        }
        printBuffer();
        if (dataBuffer.getWritePostion() - dataBuffer.getReadPostion() < HEAD_LENGTH) {
            dataBuffer.read(input);
            printBuffer();
        }
        for (int i = dataBuffer.getReadPostion(); i < dataBuffer.getWritePostion() - 2; i++) {
            printBuffer();
            if ((dataBuffer.get(i) == DataProtocol.Controller.type[0]) && (dataBuffer.get(i + 1) == DataProtocol.Controller.type[1])
                    && (dataBuffer.get(i + 2) == DataProtocol.Controller.type[2])) {
                System.arraycopy(dataBuffer.array(), i, headerBuffer.array(), 0, HEAD_LENGTH);
                headerBuffer.position(11);
                int length = headerBuffer.getInt();
                LogUtil.d("dataBuffer length:"+length);
                if (dataBuffer.getLimit() < length + HEAD_LENGTH) {
                    dataBuffer.read(input);
                    return;
                }

                printBuffer();
                // 输入控制协议 -------begin-----
                buffer.compact();
                int head = buffer.position();
                System.arraycopy(dataBuffer.array(),i+15,rawBuffer,head,length);
                buffer.position(head + length);
                buffer.flip();
                // 输入控制协议 -------end-----

                // 下一个位置
                dataBuffer.nextBuff(i, HEAD_LENGTH+length);

//                if (dataBuffer.getAvaiable() < 256 || dataBuffer.getReadPostion() >512) {
//                    // 复位条件
//                    dataBuffer.copyToHead();
//                }

                // 复位条件
                dataBuffer.copyToHead();
                LogUtil.d("指令复位ReadPostion:"+dataBuffer.getReadPostion());
                break;
            } else {
                dataBuffer.setReadPostion(i + 1);
                dataBuffer.setLimit(dataBuffer.getLimit() - 1);
                printBuffer();
            }
        }
    }
    */

    private volatile ByteBuff mDataBuffer = new ByteBuff(1024);
    // 存请求头
    private static final int HEAD_LENGTH = 15;
    private final ByteBuffer mHeaderBuffer = ByteBuffer.allocate(HEAD_LENGTH);
    // 协议数据包长度异常超限(length<0||length>1M)
    private static final int ERROR_PACKET_LEGNTH = 1*1024*1024;

    public void readFrom(InputStream inputStream, OutputStream outputStream) throws Exception {
        if (isFull()) {
            LogUtil.e("Cosmop协议异常,控制协议缓存,position:"+buffer.position()+",limit:"+buffer.limit()+",remaining:"+buffer.remaining());
            throw new IllegalStateException("Buffer full, call next() to consume");
        }

        if (mDataBuffer.getWritePostion() - mDataBuffer.getReadPostion() < HEAD_LENGTH) {
            LogUtil.d(".......继续读取数据.......");
            mDataBuffer.read(inputStream);
        }
        DataProtocol dataProtocol = nextPacket(mDataBuffer);
        if (dataProtocol == null) {
            return;
        }

        // header
        System.arraycopy(mDataBuffer.array(), mDataBuffer.getReadPostion(), mHeaderBuffer.array(), 0, HEAD_LENGTH);
        mHeaderBuffer.position(3);
        long clientTime = mHeaderBuffer.getLong();
        mHeaderBuffer.position(11);
        int length = mHeaderBuffer.getInt();

        // 容错 length < 0 || length > 1M(1*1024*1024)
        if(length < 0 || length > ERROR_PACKET_LEGNTH){
            LogUtil.e("Cosmop异常,协议数据包长度异常超限,max_length:"+ERROR_PACKET_LEGNTH+",length:"+length);
            // 异常时,容错移动1个位置
            mDataBuffer.nextBuff(mDataBuffer.getReadPostion(),1);
            return;
        }

        LogUtil.d("协议类型:"+dataProtocol.name()+",数据长度:"+length+",Limit长度:"+mDataBuffer.getLimit());
        if (mDataBuffer.getLimit() < length + HEAD_LENGTH) {
            LogUtil.d("数据长度不够,继续读取数据,Limit长度:"+mDataBuffer.getLimit()+".......");
            mDataBuffer.read(inputStream);
            return;
        }

        int startPos = mDataBuffer.getReadPostion() + HEAD_LENGTH;

        // 业务处理
        handleBusiness(dataProtocol, mDataBuffer, startPos, length, clientTime, outputStream);

        // 下一个位置
        mDataBuffer.nextBuff(mDataBuffer.getReadPostion(), HEAD_LENGTH + length);

        // 复位条件
        mDataBuffer.copyToHead();
        LogUtil.d("指令复位,读位置:"+ mDataBuffer.getReadPostion()+",Limit长度:"+mDataBuffer.getLimit());
    }

    private void handleBusiness(DataProtocol dataProtocol,ByteBuff dataBuffer,int startPos, int length, long clientTime, OutputStream outputStream){
        if (dataProtocol == DataProtocol.Controller) {
            LogUtil.d("获取一个控制协议,长度:"+length+",Limit长度:"+dataBuffer.getLimit());
            // 输入控制协议 -------begin-----
            buffer.compact();
            int head = buffer.position();
            LogUtil.d("控制协议缓存,position:"+head+",limit:"+buffer.limit()+",remaining:"+buffer.remaining());
            System.arraycopy(dataBuffer.array(),startPos,rawBuffer,head,length);
            buffer.position(head + length);
            buffer.flip();
            // 输入控制协议 -------end-----
        } else if(dataProtocol == DataProtocol.SynTimeTs){
            // 服务端 - 客户端时间
            long distanceTime = System.currentTimeMillis() - clientTime;
            syncTimets(outputStream, distanceTime);
        }
    }

    /**
     * 同步时间
     * @return
     */
    public void syncTimets(OutputStream outputStream ,long distanceTime) {
        ByteBuffer headerBuffer = ByteBuffer.allocate(15+8);
        // 15
        headerBuffer.put(DataProtocol.SynTimeTsBack.type);
        headerBuffer.putLong(System.currentTimeMillis());
        headerBuffer.putInt(8);
        headerBuffer.putLong(distanceTime);
        headerBuffer.flip();
        try {
            outputStream.write(headerBuffer.array(),0,23);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DataProtocol nextPacket(ByteBuff dataBuffer){
        for (int i = dataBuffer.getReadPostion(); i < dataBuffer.getWritePostion() - 2; i++) {
            DataProtocol[] dataProtocolList = DataProtocol.values();
            DataProtocol resultProtocol = null;
            for(DataProtocol protocol : dataProtocolList){
                if((dataBuffer.get(i) == protocol.type[0]) && (dataBuffer.get(i + 1) == protocol.type[1]) && (dataBuffer.get(i + 2) == protocol.type[2])){
                    resultProtocol = protocol;
                }
            }
            if(resultProtocol == null){
                // 移位(#触发数据包异常,正常情况下不会执行这一步)
                LogUtil.d("Cosmop异常,nextPacketDataBuffer数据信息,读位置:"+dataBuffer.getReadPostion()+",写位置:"+dataBuffer.getWritePostion()+",Limit长度:"+dataBuffer.getLimit()+",空闲长度:"+dataBuffer.getAvaiable());
                dataBuffer.nextBuff(i,1);
                continue;
            }
            return resultProtocol;
        }
        return null;
    }

    public ControlEvent next() {
        LogUtil.d("ControlEvent.next buffer.hasRemaining():"+buffer.hasRemaining());
        if (!buffer.hasRemaining()) {
            return null;
        }
        int savedPosition = buffer.position();
        int type = buffer.get();
        LogUtil.d("ControlEvent.next type:"+type+",savedPosition:"+savedPosition);
        ControlEvent controlEvent;
        switch (type) {
            case ControlEvent.TYPE_KEYCODE:
                controlEvent = parseKeycodeControlEvent();
                break;
            case ControlEvent.TYPE_TEXT:
                controlEvent = parseTextControlEvent();
                break;
            case ControlEvent.TYPE_MOUSE:
                controlEvent = parseMouseControlEvent();
                break;
            case ControlEvent.TYPE_TOUCH:
                controlEvent = parseMouseTouchEvent();
                break;
            case ControlEvent.TYPE_SCROLL:
                controlEvent = parseScrollControlEvent();
                break;
            case ControlEvent.TYPE_COMMAND:
                controlEvent = parseCommandControlEvent();
                break;
            default:
                // todo 报错
                Ln.w("Unknown event type: " + type+" savedPosition:"+savedPosition+" limit:"+buffer.limit());
                controlEvent = null;
                break;
        }

        if (controlEvent == null) {
            // failure, reset savedPosition
            buffer.position(savedPosition);
        }
        return controlEvent;
    }

    private ControlEvent parseKeycodeControlEvent() {
        if (buffer.remaining() < KEYCODE_PAYLOAD_LENGTH) {
            return null;
        }
        int action = toUnsigned(buffer.get());
        int keycode = buffer.getInt();
        int metaState = buffer.getInt();
        return ControlEvent.createKeycodeControlEvent(action, keycode, metaState);
    }

    private ControlEvent parseTextControlEvent() {
        if (buffer.remaining() < 1) {
            return null;
        }
        int len = toUnsigned(buffer.getShort());
        if (buffer.remaining() < len) {
            return null;
        }
        buffer.get(textBuffer, 0, len);
        String text = new String(textBuffer, 0, len, StandardCharsets.UTF_8);
        return ControlEvent.createTextControlEvent(text);
    }

    private ControlEvent parseMouseControlEvent() {
        if (buffer.remaining() < MOUSE_PAYLOAD_LENGTH) {
            return null;
        }
        int action = toUnsigned(buffer.get());
        int buttons = buffer.getInt();
        Position position = readPosition(buffer);
        return ControlEvent.createMotionControlEvent(action, buttons, position);
    }

    private ControlEvent parseMouseTouchEvent() {
        if (buffer.remaining() < TOUCH_PAYLOAD_LENGTH) {
            return null;
        }
        int id = toUnsigned(buffer.get());
        int action = toUnsigned(buffer.get());
        Position position = readPosition(buffer);
        return ControlEvent.createMotionTouchEvent(id, action, position);
    }

    private ControlEvent parseScrollControlEvent() {
        if (buffer.remaining() < SCROLL_PAYLOAD_LENGTH) {
            return null;
        }
        Position position = readPosition(buffer);
        int hScroll = buffer.getInt();
        int vScroll = buffer.getInt();
        return ControlEvent.createScrollControlEvent(position, hScroll, vScroll);
    }

    private ControlEvent parseCommandControlEvent() {
        if (buffer.remaining() < COMMAND_PAYLOAD_LENGTH) {
            return null;
        }
        int action = toUnsigned(buffer.get());
        return ControlEvent.createCommandControlEvent(action);
    }

    private static Position readPosition(ByteBuffer buffer) {
        int x = toUnsigned(buffer.getShort());
        int y = toUnsigned(buffer.getShort());
        int screenWidth = toUnsigned(buffer.getShort());
        int screenHeight = toUnsigned(buffer.getShort());
        return new Position(x, y, screenWidth, screenHeight);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    private static int toUnsigned(short value) {
        return value & 0xffff;
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    private static int toUnsigned(byte value) {
        return value & 0xff;
    }

    /**
     * 重置buffer
     */
    public void resetReader(){
        buffer.clear();
        buffer.limit(0);
        mDataBuffer.clear();
    }
}
