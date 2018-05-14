package com.tg.coloursteward.info;

import java.io.Serializable;

/**
 * 银行卡信息
 */
public class BankCardInfo implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 3718882142039896365L;
    public int bankCardId;

    public String cardNo;

    public String bankName;

    public String userName;

    public int bankCode;
}
