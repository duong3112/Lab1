package com.example.duong.lab1;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import java.io.IOException;
import java.util.List;

import android.util.Log;
import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManager;
import com.google.android.things.pio.Pwm;

/**
 * Skeleton of an Android Things activity.
 * <p>
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 * <p>
 * <pre>{@code
 * PeripheralManagerService service = new PeripheralManagerService();
 * mLedGpio = service.openGpio("BCM6");
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * mLedGpio.setValue(true);
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 */
public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int INTERVAL_BETWEEN_BLINKS_MS=1000;
    private Handler mHandler = new Handler();

    private Gpio mLedGpioBlue;
    private boolean mLedStateBlue = false;

    private Gpio mLedGpioGreen;
    private boolean mLedStateGreen = true;

    private Gpio mLedGpioRed;
    private boolean mLedStateRed = true;

    private int Switch_led = 0;

    private static final String PWM_NAME = "PWM1";
    private static final int PWM_DUTYCYCLE_TIMER = 30;
    private int PwmDutyCycle_val = 0;
    private Pwm mPwm;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"Starting BlinkActivity");

        try{
            String pinName = BoardDefaults.getGPIOForLED();
            // PWM
            PeripheralManager manager;
            manager = PeripheralManager.getInstance();
            List<String> portList = manager.getPwmList();
            if(portList.isEmpty()){
                Log.i(TAG,"No PWM port available on this device. ");
            }else{
                Log.i(TAG, "List of available ports: "+ portList);
            }
            mPwm = manager.openPwm(PWM_NAME);
            mPwm.setPwmFrequencyHz(120);
            mPwm.setPwmDutyCycle(PwmDutyCycle_val);

            //Emable the PWM signal
            mPwm.setEnabled(true);

            mLedGpioBlue = PeripheralManager.getInstance().openGpio("BCM19");
            mLedGpioBlue.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            mLedGpioBlue.setValue(mLedStateBlue);
            //Log.d(TAG, "Start blinking LED Blue GPIO 6");

            mLedGpioGreen = PeripheralManager.getInstance().openGpio("BCM26");
            mLedGpioGreen.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            mLedGpioGreen.setValue(mLedStateGreen);
            //Log.d(TAG,"Start linking LED GREEN GPIO 13");

            mLedGpioRed = PeripheralManager.getInstance().openGpio("BCM6");
            mLedGpioRed.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            mLedGpioRed.setValue(mLedStateRed);

            //mHandler.post(mBlinkRunnable);
            mHandler.post(pwnRunnable);
        }catch (IOException e) {
            Log.e(TAG, "Error on PeripheralIO API", e);
            Log.w(TAG,"Unabble to access PWM",e);
        }
    }
    /* public  void initializePwm(Pwm pwm)throws IOException{
        pwm.setPwmFrequencyHz(120);
        pwm.setPwmDutyCycle(75);

        //Emable the PWM signal
        pwm.setEnabled(true);
    }*/
    private Runnable pwnRunnable = new Runnable() {
        @Override
        public void run() {
            try {

                if(PwmDutyCycle_val == 99){
                    Switch_led = (Switch_led + 1)%4;
                }

                switch (Switch_led){
                    case 0:
                        mLedStateBlue = true;
                        mLedStateGreen = false;
                        mLedStateRed = true;

                        mLedGpioBlue.setValue(mLedStateBlue);
                        mLedGpioGreen.setValue(mLedStateGreen);
                        mLedGpioRed.setValue(mLedStateRed);
                        break;
                    case 1:
                        mLedStateBlue = true;
                        mLedStateGreen = true;
                        mLedStateRed = false;

                        mLedGpioBlue.setValue(mLedStateBlue);
                        mLedGpioGreen.setValue(mLedStateGreen);
                        mLedGpioRed.setValue(mLedStateRed);
                        break;
                    case 2:
                        mLedStateBlue = false;
                        mLedStateGreen = true;
                        mLedStateRed = true;

                        mLedGpioBlue.setValue(mLedStateBlue);
                        mLedGpioGreen.setValue(mLedStateGreen);
                        mLedGpioRed.setValue(mLedStateRed);
                        break;
                    case 3:
                        mLedStateBlue = false;
                        mLedStateGreen = false;
                        mLedStateRed = true;

                        mLedGpioBlue.setValue(mLedStateBlue);
                        mLedGpioGreen.setValue(mLedStateGreen);
                        mLedGpioRed.setValue(mLedStateRed);
                        break;
                }

                PwmDutyCycle_val = (PwmDutyCycle_val + 1 )%100;
                mPwm.setPwmDutyCycle(PwmDutyCycle_val);
                mHandler.postDelayed(pwnRunnable, PWM_DUTYCYCLE_TIMER);
                Log.d(TAG,"PwmDutyCycle_val: "+ PwmDutyCycle_val);
            }catch (IOException e){
                Log.e(TAG, "Error on pwnRunnable", e);
            }
        }
    };
    private Runnable mBlinkRunnable = new Runnable() {
        @Override
        public void run() {
            if (mLedGpioBlue == null && mLedGpioGreen == null) {// Exit Runnable if the GPIO is already closed
                return;
            }
            try {
                // Toggle the GPIO state
                mLedStateBlue = !mLedStateBlue;
                mLedStateGreen = !mLedStateGreen;
                mLedGpioBlue.setValue(mLedStateBlue);
                mLedGpioGreen.setValue(mLedStateGreen);
                Log.d(TAG, "State set to " + mLedStateBlue);
                // Reschedule the same runnable in {#INTERVAL_BETWEEN_BLINKS_MS} milliseconds
                mHandler.postDelayed(mBlinkRunnable, INTERVAL_BETWEEN_BLINKS_MS);
            } catch (IOException e) {
                Log.e(TAG, "Error on PeripheralIO API", e);
            }
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove pending blink Runnable from the handler.9
        mHandler.removeCallbacks(mBlinkRunnable);
        // Close the Gpio pin.
        Log.i(TAG, "Closing LED GPIO pin");
        try {
            mLedGpioBlue.close();
            mLedGpioGreen.close();
        } catch (IOException e) {
            Log.e(TAG, "Error on PeripheralIO API", e);
        } finally {
            mLedGpioBlue = null;
            mLedGpioGreen =null;
        }

        if(mPwm != null){
            try {
                mPwm.close();
                mPwm = null;
            }catch (IOException e){
                Log.w(TAG,"Unable to close PWM",e);
            }
        }
    }

}
