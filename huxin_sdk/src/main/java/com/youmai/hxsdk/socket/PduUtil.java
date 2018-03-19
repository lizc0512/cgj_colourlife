package com.youmai.hxsdk.socket;

import android.util.Log;

import com.youmai.hxsdk.utils.LogFile;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;


public abstract class PduUtil {

    private static final String TAG = "TcpClient";

    public abstract void OnRec(PduBase pduBase);

    public abstract void OnCallback(PduBase pduBase);


    public int ParsePdu(ByteBuffer buffer) {
        if (buffer.limit() > PduBase.pdu_basic_length) {
            int begin = buffer.getInt(PduBaseEnum.startflag.ordinal() * PduBase.pdu_basic_length);
            Log.v(TAG, "begin is " + begin);
            if (begin != PduBase.startflag) {
                Log.e(TAG, "header error...");
                LogFile.inStance().toFile("header error...");
                buffer.clear();
                return -1;
            }
        } else {    //did not contain a start flag yet.continue read.
            Log.v(TAG, "did not has full start flag");
            buffer.position(buffer.limit());
            buffer.limit(buffer.capacity());
            return 0;
        }

        if (buffer.limit() >= (PduBaseEnum.length.ordinal() + 1) * PduBase.pdu_basic_length) {
            //has full header
            int bodyLength = buffer
                    .getInt(PduBaseEnum.length.ordinal() * PduBase.pdu_basic_length);

            int totalLength = bodyLength
                    + (PduBaseEnum.length.ordinal() + 1) * PduBase.pdu_basic_length;
            if (totalLength <= buffer.limit()) {
                //has a full pack.
                byte[] packByte = new byte[totalLength];
                buffer.get(packByte);
                PduBase pduBase = BuildPdu(packByte);
                OnRec(pduBase);
                buffer.compact();
                //read to read.
                buffer.flip();
                return totalLength;

            } else {
                buffer.position(buffer.limit());
                buffer.limit(buffer.capacity());
                return 0;
            }

        } else {
            Log.v(TAG, " not a full header");
            buffer.position(buffer.limit());
            buffer.limit(buffer.capacity());
            return -2;
        }
    }

    private PduBase BuildPdu(byte[] bytes) {
        PduBase units = new PduBase();
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        buffer.put(bytes);
        buffer.flip();

        buffer.getInt();  //units.startflag
        units.terminal_token = buffer.getInt();
        units.commandid = buffer.getInt();
        units.seq_id = buffer.getInt();

        units.data_type = buffer.get();
        units.pdu_version = buffer.get();
        units.extension_reserved[0] = buffer.get();
        units.extension_reserved[1] = buffer.get();

        int length = buffer.getInt();
        units.length = length;
        units.body = new byte[length];
        buffer.get(units.body);
        return units;


        /*units.terminal_token = buffer.getInt(PduBaseEnum.terminal_token.ordinal() * PduBase.pdu_basic_length);
        units.commandid = buffer.getInt(PduBaseEnum.commandid.ordinal() * PduBase.pdu_basic_length);
        units.seq_id = buffer.getInt(PduBaseEnum.seq_id.ordinal() * PduBase.pdu_basic_length);

        units.data_type = buffer.get(PduBaseEnum.place_holder.ordinal() * PduBase.pdu_basic_length);
        units.pdu_version = buffer.get(PduBaseEnum.place_holder.ordinal() * PduBase.pdu_basic_length + 1);
        units.extension_reserved[0] = buffer.get(PduBaseEnum.place_holder.ordinal() * PduBase.pdu_basic_length + 2);
        units.extension_reserved[1] = buffer.get(PduBaseEnum.place_holder.ordinal() * PduBase.pdu_basic_length + 3);

        int length = buffer.getInt(PduBaseEnum.length.ordinal() * PduBase.pdu_basic_length);
        units.length = length;
        units.body = new byte[length];
        buffer.position(PduBaseEnum.body.ordinal() * PduBase.pdu_basic_length);
        buffer.get(units.body, 0, length);

        return units;*/

    }

    public ByteBuffer serializePdu(PduBase pduBase) {
        int length = PduBase.pdu_header_length + pduBase.length;
        ByteBuffer byteBuffer = ByteBuffer.allocate(length);
        byteBuffer.clear();

        byteBuffer.putInt(PduBase.startflag);
        byteBuffer.putInt(pduBase.terminal_token);
        byteBuffer.putInt(pduBase.commandid);
        byteBuffer.putInt(pduBase.seq_id);

        byteBuffer.put(pduBase.data_type);
        byteBuffer.put(pduBase.pdu_version);
        byteBuffer.put(pduBase.extension_reserved[0]);
        byteBuffer.put(pduBase.extension_reserved[1]);

        byteBuffer.putInt(pduBase.length);

        byteBuffer.put(pduBase.body);

        return byteBuffer;

        /*ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        try {
            dos.writeInt(PduBase.startflag);
            dos.writeInt(pduBase.terminal_token);
            dos.writeInt(pduBase.commandid);
            dos.writeInt(pduBase.seq_id);
            dos.writeByte(pduBase.data_type);
            dos.writeByte(pduBase.pdu_version);

            dos.writeByte(pduBase.extension_reserved[0]);

            dos.writeByte(pduBase.extension_reserved[1]);

            dos.writeInt(pduBase.length);

            dos.write(pduBase.body);
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }*/
    }


}
