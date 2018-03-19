package com.youmai.hxsdk.entity;


/**
 * 作者：create by YW
 * 日期：2016.07.25 15:37
 * 描述：
 */
public class CallInfo {

    private static boolean isCountFloatView = true;

    /**
     * 电话被叫状态,被叫在接通状态下为CALL_STATE_OFFHOOK
     */
    public enum CALL_STATE_MT {
        CALL_STATE_IDLE, CALL_STATE_RINGING, CALL_STATE_OFFHOOK
    }


    /**
     * 电话主叫状态,主叫在响铃状态下为CALL_STATE_OFFHOOK
     */
    public enum CALL_STATE_M0 {
        CALL_STATE_IDLE, CALL_STATE_OUTGOING, CALL_STATE_OFFHOOK
    }


    private static CALL_STATE_MT mCallMTState = CALL_STATE_MT.CALL_STATE_IDLE;

    private static CALL_STATE_M0 mCallMOState = CALL_STATE_M0.CALL_STATE_IDLE;


    public static void setCallMTState(CALL_STATE_MT state) {
        mCallMTState = state;
    }

    public static CALL_STATE_MT getCallMTState() {
        return mCallMTState;
    }

    public static void setCallMOState(CALL_STATE_M0 state) {
        mCallMOState = state;
    }


    public static boolean IsCalling() {
        boolean res = false;
        if (mCallMTState.ordinal() > CALL_STATE_MT.CALL_STATE_IDLE.ordinal()) {
            res = true;
        }
        if (mCallMOState.ordinal() > CALL_STATE_M0.CALL_STATE_IDLE.ordinal()) {
            res = true;
        }
        return res;
    }


    /**
     * 是否在被叫响铃状态
     *
     * @return
     */
    public static boolean IsMTCalling() {
        boolean res = false;
        if (mCallMTState.ordinal() == CALL_STATE_MT.CALL_STATE_RINGING.ordinal()) {
            return true;
        }
        return res;
    }

    /**
     * 是否在被叫接通状态
     *
     * @return
     */
    public static boolean IsMTCalled() {
        boolean res = false;
        if (mCallMTState.ordinal() == CALL_STATE_MT.CALL_STATE_OFFHOOK.ordinal()) {
            return true;
        }
        return res;
    }


    /**
     * 是否在主叫通话状态
     *
     * @return
     */
    public static boolean IsMOCalling() {
        boolean res = false;
        if (mCallMOState.ordinal() > CALL_STATE_M0.CALL_STATE_IDLE.ordinal()) {
            res = true;
        }
        return res;
    }


    public static boolean isCountFloatView() {
        return isCountFloatView;
    }

    public static void setIsCountFloatView(boolean isCountFloatView) {
        CallInfo.isCountFloatView = isCountFloatView;
    }
}
