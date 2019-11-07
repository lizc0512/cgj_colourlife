package com.tg.money.activity

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.tg.coloursteward.R
import com.tg.coloursteward.base.BaseActivity
import com.tg.coloursteward.baseModel.HttpResponse
import com.tg.coloursteward.util.GsonUtils
import com.tg.coloursteward.util.ToastUtil
import com.tg.money.adapter.InstantDistributionAdapter
import com.tg.money.entity.JsfpAccountEntity
import com.tg.money.model.MoneyModel
import kotlinx.android.synthetic.main.activity_instant_distribution.*
import kotlinx.android.synthetic.main.base_actionbar.*

/**
 * @name ${lizc}
 * @class name：com.tg.money.activity
 * @class describe
 * @anthor ${lizc} QQ:510906433
 * @time 2019/11/5 9:48
 * @change
 * @chang time
 * @class describe 即时分配页面
 */
class InstantDistributionActivity : BaseActivity(), View.OnClickListener, HttpResponse {

    var mList: MutableList<JsfpAccountEntity.ContentBean.DetailBean> = mutableListOf()
    var adapter: InstantDistributionAdapter? = null
    var moneyModel = MoneyModel()
    override fun OnHttpResponse(what: Int, result: String) {
        when (what) {
            0 -> {
                setAdapter(result)
            }
        }
    }

    private fun setAdapter(result: String) {
        var jsfpAccountEntity: JsfpAccountEntity = GsonUtils.ktGsonToBean(result, JsfpAccountEntity::class.java)
        mList = (jsfpAccountEntity.content!!.detail!!)
        if (null == adapter) {
            adapter = InstantDistributionAdapter(this, R.layout.item_instant_distribution, mList)
            rv_instant.adapter = adapter
        } else {
            adapter?.notifyDataSetChanged()
        }
        adapter?.onItemChildClickListener = BaseQuickAdapter.OnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.tv_instant_left -> {
                    if (mList.get(position).open_withdrawals.equals("1")) {
                        ToastUtil.showShortToast(this, "left")
                    }
                }
                R.id.tv_instant_right -> {
                    if (mList.get(position).open_cashing.equals("1")) {
                        ToastUtil.showShortToast(this, "right")
                    }
                }

            }
        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_base_back -> finish()
            R.id.tv_base_confirm -> startActivity(Intent(this, TransactionRecordsActivity::class.java))
        }
    }

    override fun getHeadTitle(): String? {
        return null
    }

    override fun getContentView(): View? {
        return null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instant_distribution)
        moneyModel = MoneyModel(this)
        initView()
        initData()
    }

    private fun initData() {
        moneyModel.getjsfpAccount(0, "", this)
    }

    private fun initView() {
        tv_base_title.setText("即时分配")
        tv_base_confirm.setText("交易记录")
        tv_base_confirm.setTextColor(ContextCompat.getColor(this, R.color.color_1ca1f4))
        iv_base_back.setOnClickListener(this)
        tv_base_confirm.setOnClickListener(this)
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_instant.layoutManager = linearLayoutManager
    }
}


