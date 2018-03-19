package com.youmai.hxsdk.socket;

public class PduBase {
    /****************************************************
     * basic unit of data type length
     */
    public static final int pdu_basic_length = 4;
    public static final int pdu_header_length = 24;
    /****************************************************
     * index 0. pos:[0-4)
     * the begining flag of a pdu.
     */
    public static final int startflag = 123456789;

    /****************************************************
     * index 1. pos:[4-8)
     */
    public int terminal_token /*user_id*/;
    /****************************************************
     * index 2. pos:[8-12)
     */
    public int commandid;
    /****************************************************
     * index 3. pos:[12-16)
     */
    public int seq_id;
    /***********
     * [16,17)
     */
    public byte data_type;

    /*********************************************
     * index 5, [17,18)
     * pdu verions define.
     */
    public byte pdu_version;


    /********************************************
     * Reserved for extension.
     * index 6, [18,20)
     */
    public byte[] extension_reserved = new byte[2];
    /****************************************************
     * index 4. pos:[20-24)
     */
    public int length;

    /***************************************************
     * index 5. pos:[24-infinity)
     */
    public byte[] body;
}


