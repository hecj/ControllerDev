package com.genymobile.scrcpy;
//
//import android.graphics.Rect;
//import android.media.MediaCodec;
//import android.media.MediaCodecInfo;
//import android.media.MediaFormat;
//import android.os.IBinder;
//import android.util.Log;
//import android.view.Surface;
//
//import com.genymobile.scrcpy.wrappers.SurfaceControl;
//
//import java.io.IOException;
//import java.nio.ByteBuffer;
//import java.util.concurrent.atomic.AtomicBoolean;
//
//public class ScreenEncoder implements Device.RotationListener {
//
//    static String TAG = "ScreenEncoder";
//
////    private static final int DEFAULT_FRAME_RATE = 60; // fps
////    private static final int DEFAULT_I_FRAME_INTERVAL = 10; // seconds
//
//    private static final int DEFAULT_FRAME_RATE = 2; // fps
//    private static final int DEFAULT_I_FRAME_INTERVAL = 5; // seconds
//
//    private static final int REPEAT_FRAME_DELAY = 6; // repeat after 6 frames
//
//    private static final int MICROSECONDS_IN_ONE_SECOND = 1_000_000;
//    private static final int NO_PTS = -1;
//
//    private final AtomicBoolean rotationChanged = new AtomicBoolean();
//    private final ByteBuffer headerBuffer = ByteBuffer.allocate(12);
//
//    private int bitRate;
//    private int frameRate;
//    private int iFrameInterval;
//    private boolean sendFrameMeta;
//    private long ptsOrigin;
//
//    private TcpConnection mTcpConnection;
//
//    public ScreenEncoder(TcpConnection tcpConnection, boolean sendFrameMeta, int bitRate, int frameRate, int iFrameInterval) {
//        this.sendFrameMeta = sendFrameMeta;
//        this.bitRate = bitRate;
//        this.frameRate = frameRate;
//        this.iFrameInterval = iFrameInterval;
//        this.mTcpConnection = tcpConnection;
//    }
//
//    public ScreenEncoder(TcpConnection tcpConnection, boolean sendFrameMeta, int bitRate,int frameRate) {
////        this(serverConnection, sendFrameMeta, bitRate, DEFAULT_FRAME_RATE, DEFAULT_I_FRAME_INTERVAL);
//        this(tcpConnection, sendFrameMeta, bitRate, frameRate, DEFAULT_I_FRAME_INTERVAL);
//    }
//
//    @Override
//    public void onRotationChanged(int rotation) {
//        rotationChanged.set(true);
//    }
//
//    public boolean consumeRotationChange() {
//        return rotationChanged.getAndSet(false);
//    }
//
//    public void streamScreen(Device device) throws IOException {
//        MediaFormat format = createFormat(bitRate, frameRate, iFrameInterval);
//        Log.d(TAG,"录屏准备启动,bitRate:"+bitRate+",frameRate:"+frameRate+",iFrameInterval:"+iFrameInterval);
//        device.setRotationListener(this);
//        boolean alive;
//        try {
//            do {
//                MediaCodec codec = createCodec();
//                IBinder display = createDisplay();
//                Rect contentRect = device.getScreenInfo().getContentRect();
//                Log.d("cosmop","设备视频宽高,width:"+contentRect.width()+",height:"+contentRect.height());
//                Rect videoRect = device.getScreenInfo().getVideoSize().toRect();
//                setSize(format, videoRect.width(), videoRect.height());
//                configure(codec, format);
//                Surface surface = codec.createInputSurface();
//                setDisplaySurface(display, surface, contentRect, videoRect);
//                Log.d(TAG,"录屏编码已启动...");
//                codec.start();
//                try {
//                    alive = encode(codec);
//                } finally {
//                    codec.stop();
//                    destroyDisplay(display);
//                    codec.release();
//                    surface.release();
//                    Log.d(TAG,"录屏编码__已退出!");
//                }
//            } while (alive);
//        } finally {
//            device.setRotationListener(null);
//        }
//    }
//    public byte[] configbyte;
//    // 退出录屏标记
//    volatile boolean eof = false;
//    private boolean encode(MediaCodec codec) throws IOException {
//        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
//
//        while (!consumeRotationChange() && !eof) {
//            int outputBufferId = codec.dequeueOutputBuffer(bufferInfo, -1);
////            eof = (bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0;
//            boolean bfFlag = (bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0;
//            if(bfFlag){
//               return false;
//            }
//            try {
//                if (consumeRotationChange()) {
//                    // must restart encoding with new size
//                    break;
//                }
//                if (outputBufferId >= 0) {
//                    ByteBuffer codecBuffer = codec.getOutputBuffer(outputBufferId);
//
//                    byte[] outData = new byte[bufferInfo.size];
//                    codecBuffer.get(outData);
//
//                    if (bufferInfo.flags == 2) {
//                        // BUFFER_FLAG_CODEC_CONFIG
//                        configbyte = new byte[bufferInfo.size];
//                        configbyte = outData;
//                    } else if (bufferInfo.flags == 1) {
//                        // key frame 关键帧
//                        byte[] keyframe = new byte[bufferInfo.size + configbyte.length];
//                        System.arraycopy(configbyte, 0, keyframe, 0, configbyte.length);
//                        System.arraycopy(outData, 0, keyframe, configbyte.length, outData.length);
//                        //connection.send(keyframe);
////                        LogUtil.d("获取一个关键帧keyframe:"+keyframe.length);
//                        //send(keyframe);
//                        send(keyframe,"key");
//                    } else {
//                        //connection.send(outData);
////                        LogUtil.d("获取一个差值帧:"+outData.length);
//                        //send(outData);
//                        send(outData,"diff");
//                    }
//                }
//            }catch(Exception ex){
//                ex.printStackTrace();
//            } finally {
//                if (outputBufferId >= 0) {
//                    codec.releaseOutputBuffer(outputBufferId, false);
//                }
//            }
//        }
//        return !eof;
//    }
//
//    /**
//     * by hecj
//     * 消息头(固定15个字节:类型+版本+时间+长度)+消息体(长度浮动)
//     * 类型 2byte  short
//     * 版本 1byte  byte
//     * 时间 8byte  long
//     * 长度 4byte  int
//     * =======消息头 15个byte
//     */
//    int keyFrameNum = 0;
//    int diffFrameNum = 0;
//    private void send(byte[] bytes, String frameType){
//        long currentTime = System.currentTimeMillis();
//        ByteBuffer buffer = ByteBuffer.allocate(bytes.length+15+5+4);
//        // 类型
////        buffer.putShort(ProtocolType.VideoFrame.type);
//        // 版本
////        buffer.put((byte)1);
//        // 视频帧
//        buffer.put(DataProtocol.Video.type);
//        // 时间
//        buffer.putLong(currentTime);
//        // 长度
//        buffer.putInt(bytes.length+5+4);
//
//        // 中间插入5个字节的帧序号
//        if("key".equals(frameType)){
//            keyFrameNum++;
//            diffFrameNum = 0;
//        } else{
//            diffFrameNum++;
//        }
//        buffer.putInt(keyFrameNum);
//        buffer.put((byte)diffFrameNum);
//
//        // data
//        buffer.put(bytes);
//
//        // 尾部4个字节验证 1 2 3 4
//        buffer.put(new byte[]{1,2,3,4});
//
//        LogUtil.d("队列信息,发送一个关键帧:"+keyFrameNum+",差值帧:"+diffFrameNum+",长度:"+bytes.length);
//
//        mTcpConnection.sendQueue(buffer.array());
//    }
//
//    /**
//     * by hecj
//     * 消息头(固定15个字节:类型+版本+时间+长度)+消息体(长度浮动)
//     * 类型 2byte  short
//     * 版本 1byte  byte
//     * 时间 8byte  long
//     * 长度 4byte  int
//     * =======消息头 15个byte
//     */
//    private void send(byte[] bytes){
//        ByteBuffer buffer = ByteBuffer.allocate(bytes.length+15);
//        // 类型
//        buffer.putShort(ProtocolType.VideoFrame.type);
//        // 版本
//        buffer.put((byte)1);
//        // 时间
//        buffer.putLong(System.currentTimeMillis());
//        // 长度
//        buffer.putInt(bytes.length);
//        // data
//        buffer.put(bytes);
//        mTcpConnection.send(buffer.array());
//    }
//
//    private void writeFrameMeta(MediaCodec.BufferInfo bufferInfo, int packetSize) throws IOException {
//        headerBuffer.clear();
//
//        long pts;
//        if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
//            pts = NO_PTS; // non-media data packet
//        } else {
//            if (ptsOrigin == 0) {
//                ptsOrigin = bufferInfo.presentationTimeUs;
//            }
//            pts = bufferInfo.presentationTimeUs - ptsOrigin;
//        }
//
//        headerBuffer.putLong(pts);
//        headerBuffer.putInt(packetSize);
//        headerBuffer.flip();
////        IO.writeFully(fd, headerBuffer);
//
//        mTcpConnection.send(headerBuffer.array());
//
//    }
//
//    private static MediaCodec createCodec() throws IOException {
//        return MediaCodec.createEncoderByType("video/avc");
//    }
//
//    private static MediaFormat createFormat(int bitRate, int frameRate, int iFrameInterval) throws IOException {
//        MediaFormat format = new MediaFormat();
//        format.setString(MediaFormat.KEY_MIME, "video/avc");
//        format.setInteger(MediaFormat.KEY_BIT_RATE, bitRate);
//        format.setInteger(MediaFormat.KEY_FRAME_RATE, frameRate);
//        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
//        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, iFrameInterval);
//        // display the very first frame, and recover from bad quality when no new frames
//        format.setLong(MediaFormat.KEY_REPEAT_PREVIOUS_FRAME_AFTER, MICROSECONDS_IN_ONE_SECOND * REPEAT_FRAME_DELAY / frameRate); // µs
//        return format;
//    }
//
//    private static IBinder createDisplay() {
//        return SurfaceControl.createDisplay("scrcpy", true);
//    }
//
//    private static void configure(MediaCodec codec, MediaFormat format) {
//        codec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
//    }
//
//    private static void setSize(MediaFormat format, int width, int height) {
//        Log.d("cosmop","设置视频宽高,width:"+width+",height:"+height);
//        format.setInteger(MediaFormat.KEY_WIDTH, width);
//        format.setInteger(MediaFormat.KEY_HEIGHT, height);
//    }
//
//    private static void setDisplaySurface(IBinder display, Surface surface, Rect deviceRect, Rect displayRect) {
//        SurfaceControl.openTransaction();
//        try {
//            SurfaceControl.setDisplaySurface(display, surface);
//            SurfaceControl.setDisplayProjection(display, 0, deviceRect, displayRect);
//            SurfaceControl.setDisplayLayerStack(display, 0);
//        } finally {
//            SurfaceControl.closeTransaction();
//        }
//    }
//
//    private static void destroyDisplay(IBinder display) {
//        SurfaceControl.destroyDisplay(display);
//    }
//
//    /**
//     * 退出录屏
//     */
//    public void stopScreenEncoder(){
//        Log.d(TAG,"停止录屏编码");
//        this.eof = true;
//    }
//}
