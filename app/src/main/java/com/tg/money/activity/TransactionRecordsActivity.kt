package com.tg.money.activity

import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.tg.coloursteward.R
import com.tg.coloursteward.base.BaseActivity
import com.tg.coloursteward.baseModel.HttpResponse
import com.tg.coloursteward.util.GsonUtils
import com.tg.money.adapter.TranscationRecordAdapter
import com.tg.money.entity.Data
import com.tg.money.entity.DemoTestEntity
import com.tg.money.entity.TranscationRecordEntity
import com.tg.money.model.MoneyModel
import kotlinx.android.synthetic.main.activity_transaction_records.*
import kotlinx.android.synthetic.main.base_actionbar.*

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
    var mList: MutableList<TranscationRecordEntity.ContentBean.ResultBean.DataBean>? = mutableListOf()
    var adapter: TranscationRecordAdapter? = null
    var moneyModel = MoneyModel()
    var mPage = 1
    var numTotal: Int = 0
    override fun OnHttpResponse(what: Int, result: String) {
        when (what) {
            0 -> {
                initAdapter(result)
            }
        }
    }

    private fun initAdapter(result: String) {
//        var transcationRecordEntity: TranscationRecordEntity = GsonUtils.ktGsonToBean(result, TranscationRecordEntity::class.java)
//        mList?.addAll(transcationRecordEntity.content!!.result!!.data!!)
//        val testList = ArrayList<TranscationRecordEntity.ContentBean.ResultBean.DataBean>()
//        var aaaa = GsonUtils.ktGsonToBean(result, TranscationRecordEntity::class.java)
//        testList.addAll(aaaa.content!!.result!!.data!!)



        var dataTest: DemoTestEntity = GsonUtils.ktGsonToBean(result, DemoTestEntity::class.java)
        dataList.addAll(dataTest.content.result.data)
        numTotal = dataTest.content.result.total


        if (null == adapter) {
            adapter = TranscationRecordAdapter(this, R.layout.item_transaction_record, dataList)
            rv_transcation_record.adapter = adapter
        } else {
            adapter?.notifyDataSetChanged()
        }
        adapter?.setOnLoadMoreListener(BaseQuickAdapter.RequestLoadMoreListener() {
            Handler().postDelayed({
                if (dataList?.size!! >= numTotal) {
                    adapter?.loadMoreEnd()
                } else {
                    mPage++
                    initData(mPage, false)
                    adapter?.loadMoreComplete()
                }
            }, 1000)

        }, rv_transcation_record)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_base_back -> finish()
            R.id.tv_base_confirm -> {
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
        initData(mPage, true)
    }

    private fun initData(page: Int, loading: Boolean) {
        moneyModel.getjsfpRecord(0, "", page, "20", loading, this)
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
}
