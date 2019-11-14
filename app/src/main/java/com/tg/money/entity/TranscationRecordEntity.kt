package com.tg.money.entity

/**
 * @name lzic
 * @class name：com.tg.money.entity
 * @class describe
 * @anthor lzic QQ:510906433
 * @time 2019/11/7 17:49
 * @change
 * @chang time
 * @class describe
 */
data class TranscationRecordEntity(
        val code: Int,
        val content: Content,
        val contentEncrypt: String,
        val message: String
)

data class Content(
        val result: Result
)

data class Result(
        val `data`: List<Data>,
        val total: Int,
        val withdraw_money: String
)

data class Data(
        val account_amount: String,
        val amount: String,
        val arrival_account: String,
        val arrival_amount: String,
        val arrival_atid: String,
        val arrival_cano: String,
        val arrival_pano: String,
        val bank_code: String,
        val bank_name: String,
        val bank_num: String,
        val bank_user: String,
        val business_name: String,
        val business_uuid: String,
        val community_name: String,
        val community_uuid: String,
        val create_at: String,
        val finance_atid: String,
        val finance_cano: String,
        val finance_no: String,
        val finance_pano: String,
        val general_name: String,
        val general_uuid: String,
        val handle_type: String,
        val hynpay_payment_no: String,
        val hynpay_result: String,
        val hynpay_state: String,
        val id: String,
        val mobile: String,
        val orderno: String,
        val payment_no: String,
        val rate: String,
        val remark: String,
        val result: String,
        val service_amount: String,
        val service_fee: String,
        val signed_subject: String,
        val signed_time: String,
        val source_app_id: String,
        val source_name: String,
        val split_target: String,
        val split_type: String,
        val state: String,
        val taxes_amount: String,
        val taxes_rate: String,
        val taxes_split_id: String,
        val type: String,
        val update_at: String,
        val user_ID: String
)