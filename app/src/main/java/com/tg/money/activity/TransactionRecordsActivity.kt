package com.tg.money.activity

import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import android.widget.PopupWindow
import com.chad.library.adapter.base.BaseQuickAdapter
import com.tg.coloursteward.R
import com.tg.coloursteward.base.BaseActivity
import com.tg.coloursteward.baseModel.HttpResponse
import com.tg.coloursteward.net.HttpTools
import com.tg.coloursteward.util.GsonUtils
import com.tg.coloursteward.util.StringUtils
import com.tg.money.adapter.TranscationRecordAdapter
import com.tg.money.callback.SelectTypeCallBack
import com.tg.money.entity.Data
import com.tg.money.entity.SelectTypeEntity
import com.tg.money.entity.TranscationRecordEntity
import com.tg.money.model.MoneyModel
import com.tg.money.view.SelectTypePopView
import kotlinx.android.synthetic.main.activity_transaction_records.*
import kotlinx.android.synthetic.main.base_actionbar.*
import java.util.*

/**
 * @name ${lizc}
 * @class name：com.tg.money.activity
 * @class describe
 * @anthor ${lizc} QQ:510906433
 * @time 2019/11/7 9:05
 * @change
 * @chang time
 * @class describe 即时分配交易记录页面
 */
class TransactionRecordsActivity : BaseActivity(), View.OnClickListener, HttpResponse {
    var dataList: MutableList<Data> = mutableListOf()
    var listGridView: MutableList<SelectTypeEntity.ContentBean.ResultBean> = mutableListOf()
    var adapter: TranscationRecordAdapter? = null
    var selectTypePopView: SelectTypePopView? = null
    var moneyModel = MoneyModel()
    var mPage = 1
    var numTotal: Int = 0
    var general: String = ""
    override fun OnHttpResponse(what: Int, result: String) {
        when (what) {
            0 -> {
                initAdapter(result)
            }
            1 -> {
                val content = HttpTools.getContentString(result)
                if (StringUtils.isNotEmpty(content)) {
                    var selectTypeEntity: SelectTypeEntity = GsonUtils.ktGsonToBean(result, SelectTypeEntity::class.java)
                    selectTypeEntity.content?.result?.let {
                        listGridView = (it)
                    }
                }
            }
        }
    }

    private fun initAdapter(result: String) {
        var content = HttpTools.getContentString(result)
        if (mPage == 1) {
            dataList.clear()
        }
        if (!TextUtils.isEmpty(content)) {
            var dataTest: TranscationRecordEntity = GsonUtils.ktGsonToBean(result, TranscationRecordEntity::class.java)
            dataList.addAll(dataTest.content.result.data)
            numTotal = dataTest.content.result.total
        }
        if (null == adapter) {
            adapter = TranscationRecordAdapter(this, R.layout.item_transaction_record, dataList)
            rv_transcation_record.adapter = adapter
        } else {
            adapter?.setNewData(dataList)
        }
        adapter?.setOnLoadMoreListener(BaseQuickAdapter.RequestLoadMoreListener() {
            Handler().postDelayed({
                if (dataList?.size!! >= numTotal) {
                    adapter?.loadMoreEnd()
                } else {
                    mPage++
                    initData(mPage, false, general)
                    adapter?.loadMoreComplete()
                }
            }, 1000)

        }, rv_transcation_record)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_base_back -> finish()
            R.id.tv_base_confirm -> {
                selectTypePopView = SelectTypePopView(this@TransactionRecordsActivity, listGridView as ArrayList<SelectTypeEntity.ContentBean.ResultBean>?,
                        SelectTypeCallBack { view, position ->
                            run {
                                for (i in listGridView) {
                                    i.isCheck = 0
                                }
                                listGridView.get(position).isCheck = 1
                                mPage = 1
                                general = listGridView.get(position).general_uuid
                                initData(mPage, true, general)
                            }
                        })
                selectTypePopView?.setOnDismissListener(poponDismissListener())
                selectTypePopView?.showPopupWindow(tv_base_confirm)
            }
        }
    }

    override fun getContentView(): View? {
        return null
    }

    override fun getHeadTitle(): String? {
        return null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_records)
        moneyModel = MoneyModel(this)
        initView()
        initData(mPage, true, general)
        initType()
    }

    private fun initType() {
        moneyModel.getjsfpType(1, "", this)
    }

    private fun initData(page: Int, loading: Boolean, general: String) {
        moneyModel.getjsfpRecord(0, general, page, "20", loading, this)
    }

    private fun initView() {
        tv_base_title.setText("交易记录")
        tv_base_confirm.setText("筛选")
        tv_base_confirm.textSize = 14f
        iv_base_back.setOnClickListener(this)
        tv_base_confirm.setOnClickListener(this)
        var linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_transcation_record.layoutManager = linearLayoutManager
    }

    /**
     * 添加新笔记时弹出的popWin关闭的事件，主要是为了将背景透明度改回来
     *
     * @author cg
     */
    internal inner class poponDismissListener : PopupWindow.OnDismissListener {

        override fun onDismiss() {
            val lp = this@TransactionRecordsActivity.getWindow().getAttributes()
            lp.alpha = 1.0f
            this@TransactionRecordsActivity.getWindow().setAttributes(lp)
        }
    }
}
