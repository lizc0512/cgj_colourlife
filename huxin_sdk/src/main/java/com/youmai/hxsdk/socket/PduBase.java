package com.youmai.hxsdk.socket;

public class PduBase {
    /****************************************************
     * basic unit of data type length
     */
    public static final int pdu_basic_length = 2;
    public static final int pdu_header_length = 89;


    /****************************************************
     * index 0. pos:[0-4)
     * the begining flag of a pdu.
     */
    public static final int startflag = 0x66aa;

    /****************************************************
     * index 1. pos:[4-40)
     */
    public byte[] user_id = new byte[36];

    /****************************************************
     * index 2. pos:[40-76)
     */
    public byte[] app_id = new byte[36];

    /****************************************************
     * index 3. pos:[76-80)
     */
    public int service_id;

    /****************************************************
     * index 4. pos:[80-84)
     */
    public int commandid;

    /****************************************************
     * index 5. pos:[84-88)
     */
    public int seq_id;

    /*****************************************************
     * index 6. pos:[88,89)
     */
    public byte version;

    /****************************************************
     * index 7. pos:[89-93)
     */
    public int length;

    /***************************************************
     * index 8. pos:[93-infinity)
     */
    public byte[] body;
}


