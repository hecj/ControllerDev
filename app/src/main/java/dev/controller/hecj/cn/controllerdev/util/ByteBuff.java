package dev.controller.hecj.cn.controllerdev.util;


import java.io.InputStream;

public class ByteBuff {

    private byte[] buffer = null;

    private int limit = 0;
    // 读位置
    private int readPostion = 0;
    // 写位置
    private int writePostion = 0;

    private int capacity = 0;

    public ByteBuff(int capacity){
        buffer = new byte[capacity];
        this.capacity = capacity;
    }

    public byte[] array(){
        return this.buffer;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getReadPostion() {
        return readPostion;
    }

    public void setReadPostion(int readPostion) {
        this.readPostion = readPostion;
    }

    public int getWritePostion() {
        return writePostion;
    }

    public void setWritePostion(int writePostion) {
        this.writePostion = writePostion;
    }

    public int getCapacity(){
        return capacity;
    }

    public int getAvaiable(){
        return this.capacity - this.writePostion ;
    }

    public byte get(int postion){
        return this.buffer[postion];
    }

    public void read(InputStream inputStream) throws Exception {
        LogUtil.d("......正在读取数据......");
        int length = inputStream.read(this.array(), this.getWritePostion(), this.getAvaiable());
        LogUtil.d("读位置:"+this.getReadPostion()+",写位置:"+this.getWritePostion()+",Limit长度:"+this.getLimit()+",计划读取:"+this.getAvaiable()+",实际读取长度:"+length);
        if (length == -1) {
            LogUtil.d("......没有读取到数据,休眠10毫秒后继续读取......");
            Thread.sleep(10l);
            return;
        }
        this.setWritePostion(this.getWritePostion() + length);
        this.setLimit(this.getLimit() + length);
    }
    /**
     * 复制到首部
     */
    public void copyToHead(){
        System.arraycopy(this.array(), this.getReadPostion(), this.array(), 0, this.getWritePostion() - this.getReadPostion());
        this.setWritePostion(this.getWritePostion() - this.getReadPostion());
        this.setReadPostion(0);
    }

    public void nextBuff(int currentReadPostion, int readLength){
        this.setReadPostion(currentReadPostion + readLength);
        this.setLimit(this.getLimit() - readLength);
    }

    public void clear(){
        this.setLimit(0);
        this.setReadPostion(0);
        this.setWritePostion(0);
    }

    public void log(){
        LogUtil.d("DataBuffer数据信息,读位置:"+this.getReadPostion()+",写位置:"+this.getWritePostion()+",Limit长度:"+this.getLimit()+",空闲长度:"+this.getAvaiable());
    }

}