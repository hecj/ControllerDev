package com.genymobile.scrcpy;

import android.graphics.Rect;

public class Options {
    private int maxSize;
    private int bitRate;
    private boolean tunnelForward = true;
    private Rect crop;
    private boolean sendFrameMeta = false;

    private int frameRate;

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public int getBitRate() {
        return bitRate;
    }

    public void setBitRate(int bitRate) {
        this.bitRate = bitRate;
    }

    public boolean isTunnelForward() {
        return tunnelForward;
    }

    public void setTunnelForward(boolean tunnelForward) {
        this.tunnelForward = tunnelForward;
    }

    public Rect getCrop() {
        return crop;
    }

    public void setCrop(Rect crop) {
        this.crop = crop;
    }

    public boolean getSendFrameMeta() {
        return sendFrameMeta;
    }

    public void setSendFrameMeta(boolean sendFrameMeta) {
        this.sendFrameMeta = sendFrameMeta;
    }

    /*
    @SuppressWarnings("checkstyle:MagicNumber")
    public static Options createOptions(String... args) {
        if (args.length != 5)
            throw new IllegalArgumentException("Expecting 5 parameters");

        Options options = new Options();

        int maxSize = Integer.parseInt(args[0]) & ~7; // multiple of 8
        options.setMaxSize(maxSize);

        int bitRate = Integer.parseInt(args[1]);
        options.setBitRate(bitRate);

        // use "adb forward" instead of "adb tunnel"? (so the server must listen)
        boolean tunnelForward = Boolean.parseBoolean(args[2]);
        options.setTunnelForward(tunnelForward);

        Rect crop = parseCrop(args[3]);
        options.setCrop(crop);

        boolean sendFrameMeta = Boolean.parseBoolean(args[4]);
        options.setSendFrameMeta(sendFrameMeta);

        return options;
    }
    */

    private static Rect parseCrop(String crop) {
        if ("-".equals(crop)) {
            return null;
        }
        // input format: "width:height:x:y"
        String[] tokens = crop.split(":");
        if (tokens.length != 4) {
            throw new IllegalArgumentException("Crop must contains 4 values separated by colons: \"" + crop + "\"");
        }
        int width = Integer.parseInt(tokens[0]);
        int height = Integer.parseInt(tokens[1]);
        int x = Integer.parseInt(tokens[2]);
        int y = Integer.parseInt(tokens[3]);
        return new Rect(x, y, x + width, y + height);
    }

    public int getFrameRate() {
        return frameRate;
    }

    public void setFrameRate(int frameRate) {
        this.frameRate = frameRate;
    }
}
