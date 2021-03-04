package dev.controller.hecj.cn.controllerdev;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.genymobile.scrcpy.ControlEvent;
import com.genymobile.scrcpy.Device;
import com.genymobile.scrcpy.EventController;
import com.genymobile.scrcpy.Ln;
import com.genymobile.scrcpy.Options;
import com.genymobile.scrcpy.Position;

import java.nio.ByteBuffer;

import dev.controller.hecj.cn.controllerdev.util.DataProtocol;

public class MainActivity extends Activity {

    EventController mEventController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void onClick(View view){

        if(view.getId() == R.id.button) {


            //Toast.makeText(this, "模拟点击", Toast.LENGTH_SHORT).show();

            new Thread() {
                @Override
                public void run() {
                    Device mDevice = getDevice(720, 2000000, 6);

                    mEventController = new EventController(mDevice);

                    controlerBuffer.clear();


                    /*
                    // 10 按下
                    controlerBuffer.put((byte)0);
                    controlerBuffer.put((byte) KeyEvent.ACTION_DOWN);
                    controlerBuffer.putInt(KeyEvent.KEYCODE_HOME);
                    controlerBuffer.putInt(0);
                    handlerControler(0,10);

                    controlerBuffer.clear();
                    // 10 抬起
                    controlerBuffer.put((byte)0);
                    controlerBuffer.put((byte) KeyEvent.ACTION_UP);
                    controlerBuffer.putInt(KeyEvent.KEYCODE_HOME);
                    controlerBuffer.putInt(0);

                    handlerControler(0,10);
                    */


                    // 类型 1 byte
                    controlerBuffer.clear();
                    controlerBuffer.put((byte) 5);
                    int id = 0;
                    controlerBuffer.put((byte) id);
                    controlerBuffer.put((byte) 0);
                    int x = 414;
                    int y = 600;
                    controlerBuffer.put((byte) (x >> 8));
                    controlerBuffer.put((byte) x);
                    controlerBuffer.put((byte) (y >> 8));
                    controlerBuffer.put((byte) y);
                    int displayW = 1345;
                    int displayH = 2392;
                    controlerBuffer.put((byte) (displayW >> 8));
                    controlerBuffer.put((byte) displayW);
                    controlerBuffer.put((byte) (displayH >> 8));
                    controlerBuffer.put((byte) displayH);
                    handlerControler(0, 11);

                    // 类型 1 byte
                    controlerBuffer.clear();
                    controlerBuffer.put((byte) 5);
                    controlerBuffer.put((byte) id);
                    controlerBuffer.put((byte) 2);
                    controlerBuffer.put((byte) (x >> 8));
                    controlerBuffer.put((byte) x);
                    controlerBuffer.put((byte) (y >> 8));
                    controlerBuffer.put((byte) y);
                    controlerBuffer.put((byte) (displayW >> 8));
                    controlerBuffer.put((byte) displayW);
                    controlerBuffer.put((byte) (displayH >> 8));
                    controlerBuffer.put((byte) displayH);
                    handlerControler(0, 11);

                    // 类型 1 byte
                    controlerBuffer.clear();
                    controlerBuffer.put((byte) 5);
                    controlerBuffer.put((byte) id);
                    controlerBuffer.put((byte) 1);
                    controlerBuffer.put((byte) (x >> 8));
                    controlerBuffer.put((byte) x);
                    controlerBuffer.put((byte) (y >> 8));
                    controlerBuffer.put((byte) y);
                    controlerBuffer.put((byte) (displayW >> 8));
                    controlerBuffer.put((byte) displayW);
                    controlerBuffer.put((byte) (displayH >> 8));
                    controlerBuffer.put((byte) displayH);
                    handlerControler(0, 11);


                }
            }.start();
        } else if(view.getId() == R.id.full){
            Toast.makeText(this, "点击了屏幕", Toast.LENGTH_LONG).show();
        }


    }

    private Device getDevice(int size, int bitRate, int frameRate){
        Options options = new Options();
        options.setBitRate(bitRate);
        options.setMaxSize(size);
        options.setFrameRate(frameRate);
        return new Device(options);
    }

    /**
     * 控制事件处理
     * @param dataBuffer
     * @param startPos
     * @param length
     */
    static ByteBuffer controlerBuffer = ByteBuffer.allocate(1024);
    private void handlerControler(int startPos, int length){
        controlerBuffer.position(0);
        int type = controlerBuffer.get();
        ControlEvent controlEvent = null;
        switch (type) {
            case ControlEvent.TYPE_KEYCODE:
                controlEvent = parseKeycodeControlEvent();
                break;
            case ControlEvent.TYPE_TEXT:
//                controlEvent = parseTextControlEvent();
                break;
            case ControlEvent.TYPE_MOUSE:
//                controlEvent = parseMouseControlEvent();
                break;
            case ControlEvent.TYPE_TOUCH:
                controlEvent = parseMouseTouchEvent();
                break;
            case ControlEvent.TYPE_SCROLL:
//                controlEvent = parseScrollControlEvent();
                break;
            case ControlEvent.TYPE_COMMAND:
//                controlEvent = parseCommandControlEvent();
                break;
            default:
                // todo 报错
                Ln.w("Unknown event type: " + type);
                controlEvent = null;
                break;
        }

        if (controlEvent == null) {
            // failure, reset savedPosition
            controlerBuffer.position(0);
        }
        mEventController.handleEvent(controlEvent);
    }

    private ControlEvent parseKeycodeControlEvent(){
        int action = toUnsigned(controlerBuffer.get());
        int keycode = controlerBuffer.getInt();
        int metaState = controlerBuffer.getInt();
        return ControlEvent.createKeycodeControlEvent(action, keycode, metaState);
    }

    private ControlEvent parseMouseTouchEvent(){
        int id = toUnsigned(controlerBuffer.get());
        int action = toUnsigned(controlerBuffer.get());
        Position position = readPosition(controlerBuffer);
        return ControlEvent.createMotionTouchEvent(id, action, position);
    }

    private static Position readPosition(ByteBuffer buffer) {
        int x = toUnsigned(buffer.getShort());
        int y = toUnsigned(buffer.getShort());
        int screenWidth = toUnsigned(buffer.getShort());
        int screenHeight = toUnsigned(buffer.getShort());
        return new Position(x, y, screenWidth, screenHeight);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    private static int toUnsigned(byte value) {
        return value & 0xff;
    }
    @SuppressWarnings("checkstyle:MagicNumber")
    private static int toUnsigned(short value) {
        return value & 0xffff;
    }

}
