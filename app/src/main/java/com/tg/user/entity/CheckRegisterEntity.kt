package com.tg.user.entity

/**
 * @name lizc
 * @class name：com.tg.user.entity
 * @class describe
 * @anthor lizc QQ:510906433
 * @time 2019/10/29 10:09
 * @change
 * @chang time
 * @class describe
 */
data class CheckRegisterEntity(
        val code: Int,
        val content: Content,
        val contentEncrypt: String,
        val message: String
)

data class Content(
        val is_register: Int
)