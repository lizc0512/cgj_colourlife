package com.tg.money.entity

/**
 * @name lizc
 * @class name：com.tg.money.entity
 * @class describe
 * @anthor lizc QQ:510906433
 * @time 2019/11/6 11:50
 * @change
 * @chang time
 * @class describe
 */
class JsfpAccountEntity {

    /**
     * code : 0
     * message : success
     * content : {"total_balance":"29.32","detail":[{"general_id":25,"general_uuid":"ICEXSFXT-F3B5-4F86-ABDC-F124E505F047","business_uuid":"7fe7aa29-3355-42ca-936a-8c5da16b5c97","pano":"10464d48c2a8d7f4425c9cc2102b1826","atid":46,"split_type":2,"split_target":"lizhicheng01","finance_cano":"1046e793cfd542154c06ada847d8360a","finance_cno":"lizhicheng01","split_target_uuid":"f541d3ab-add2-4b2f-a83c-7541adabc8e6","uuid_finance_cano":"1046ad76faeaff9243c89d77059cb54d","uuid_finance_cno":"6341f78ae6244fc7860a7367970925ee","split_money":"29.09","withdraw_money":"2.02","general_name":"新收费系统总店（OA）","open_withdrawals":"1","open_cashing":"1"},{"general_id":45,"general_uuid":"ICEEQJ00-811D-4C6B-9EDE-8F378D624EEE","business_uuid":"ed70c299-3fdf-48bc-ae2f-4669a2c7de13","pano":"67789ad2f69f42b283c53e02a0ae00b0","atid":13,"split_type":2,"split_target":"lizhicheng01","finance_cano":"101309657428b9034e108463a67229ee","finance_cno":"lizhicheng01","split_target_uuid":"f541d3ab-add2-4b2f-a83c-7541adabc8e6","uuid_finance_cano":"101353ebf1de60e44d5c8290af654a52","uuid_finance_cno":"c8b19eae1507454c845127d9e38c97ee","split_money":"0.23","withdraw_money":"0.05","general_name":"E清洁总店（OA）","open_withdrawals":"1","open_cashing":"1"}]}
     * contentEncrypt :
     */

    var code: Int = 0
    var message: String? = null
    var content: ContentBean? = null
    var contentEncrypt: String? = null

    class ContentBean {
        /**
         * total_balance : 29.32
         * detail : [{"general_id":25,"general_uuid":"ICEXSFXT-F3B5-4F86-ABDC-F124E505F047","business_uuid":"7fe7aa29-3355-42ca-936a-8c5da16b5c97","pano":"10464d48c2a8d7f4425c9cc2102b1826","atid":46,"split_type":2,"split_target":"lizhicheng01","finance_cano":"1046e793cfd542154c06ada847d8360a","finance_cno":"lizhicheng01","split_target_uuid":"f541d3ab-add2-4b2f-a83c-7541adabc8e6","uuid_finance_cano":"1046ad76faeaff9243c89d77059cb54d","uuid_finance_cno":"6341f78ae6244fc7860a7367970925ee","split_money":"29.09","withdraw_money":"2.02","general_name":"新收费系统总店（OA）","open_withdrawals":"1","open_cashing":"1"},{"general_id":45,"general_uuid":"ICEEQJ00-811D-4C6B-9EDE-8F378D624EEE","business_uuid":"ed70c299-3fdf-48bc-ae2f-4669a2c7de13","pano":"67789ad2f69f42b283c53e02a0ae00b0","atid":13,"split_type":2,"split_target":"lizhicheng01","finance_cano":"101309657428b9034e108463a67229ee","finance_cno":"lizhicheng01","split_target_uuid":"f541d3ab-add2-4b2f-a83c-7541adabc8e6","uuid_finance_cano":"101353ebf1de60e44d5c8290af654a52","uuid_finance_cno":"c8b19eae1507454c845127d9e38c97ee","split_money":"0.23","withdraw_money":"0.05","general_name":"E清洁总店（OA）","open_withdrawals":"1","open_cashing":"1"}]
         */

        var total_balance: String? = null
        var detail: MutableList<DetailBean>? = null

        inner class DetailBean {
            /**
             * general_id : 25
             * general_uuid : ICEXSFXT-F3B5-4F86-ABDC-F124E505F047
             * business_uuid : 7fe7aa29-3355-42ca-936a-8c5da16b5c97
             * pano : 10464d48c2a8d7f4425c9cc2102b1826
             * atid : 46
             * split_type : 2
             * split_target : lizhicheng01
             * finance_cano : 1046e793cfd542154c06ada847d8360a
             * finance_cno : lizhicheng01
             * split_target_uuid : f541d3ab-add2-4b2f-a83c-7541adabc8e6
             * uuid_finance_cano : 1046ad76faeaff9243c89d77059cb54d
             * uuid_finance_cno : 6341f78ae6244fc7860a7367970925ee
             * split_money : 29.09
             * withdraw_money : 2.02
             * general_name : 新收费系统总店（OA）
             * open_withdrawals : 1
             * open_cashing : 1
             */

            var general_id: String? = null
            var general_uuid: String? = null
            var business_uuid: String? = null
            var pano: String? = null
            var atid: String? = null
            var split_type: String? = null
            var split_target: String? = null
            var finance_cano: String? = null
            var finance_cno: String? = null
            var split_target_uuid: String? = null
            var uuid_finance_cano: String? = null
            var uuid_finance_cno: String? = null
            var split_money: String? = null
            var withdraw_money: String? = null
            var general_name: String? = null
            var open_withdrawals: String? = null
            var open_cashing: String? = null
        }
    }
}
