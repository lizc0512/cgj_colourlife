package com.youmai.hxsdk.socket;

public enum PduBaseEnum {
    /***
     * [0,4)
     * 开始标记
     */
    startflag,

    /***
     * [4,8)
     * 发起者的userid，notify类型而言，是接收者的userid。
     * 任何接收自路由服务的数据包均需要解此字段。其他情况均无需特别处理。
     */
    terminal_token,
    /***
     * [8,12)
     * 操作码
     */
    commandid,

    /**
     * [12,16)
     * 包头的序列号id是为了让app匹配服务端发的数据和app自己发起的请求的结果。
     * 例如，app发起查询某群组信息的请求，后端收到请求，向数据库查询，这时候其他用户B给他发了一个IM消息，
     * 后台转发IM消息给app，app需要明白这个数据报不是他主动请求的回复。
     */
    seq_id,

    /**
     * 仅仅是4字节的字节内容的占位符。
     */
    place_holder,

    /**
     * index 5, [20,24)
     * data type define for pdu carried
     * pb长度
     */
    length,

    /**
     * 包体数据
     */
    body,
}
