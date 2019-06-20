/*
* @date 创建时间 2017/7/3
* @author  yuansk
* @Description  数据请求之后的回调
* @version  1.1
*/
package com.tg.coloursteward.baseModel;

import com.yanzhenjie.nohttp.rest.Response;

/**
 * <p>接受回调结果.</p>
 * Created in Nov 4, 2015 12:54:50 PM.
 *
 * @author Yan Zhenjie.
 */
public interface HttpListener<T> {

    void onSucceed(int what, Response<T> response);

    void onFailed(int what, Response<T> response);


}
