package com.tg.money.activity

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.PopupWindow
import com.chad.library.adapter.base.BaseQuickAdapter
import com.tg.coloursteward.R
import com.tg.coloursteward.base.BaseActivity
import com.tg.coloursteward.baseModel.HttpResponse
import com.tg.coloursteward.net.HttpTools
import com.tg.coloursteward.util.GsonUtils
import com.tg.coloursteward.util.StringUtils
import com.tg.coloursteward.view.AppDetailPopWindowView
import com.tg.money.adapter.InstantDistributionAdapter
import com.tg.money.callback.SelectTypeCallBack
import com.tg.money.entity.JsfpAccountEntity
import com.tg.money.entity.SelectTypeEntity
import com.tg.money.model.MoneyModel
import com.tg.money.view.SelectTypePopView
import kotlinx.android.synthetic.main.activity_instant_distribution.*
import kotlinx.android.synthetic.main.base_actionbar.*
import java.util.*

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
    var listGridView: MutableList<SelectTypeEntity.ContentBean.ResultBean> = mutableListOf()
    var adapter: InstantDistributionAdapter? = null
    var moneyModel = MoneyModel()
    var popWindowView: AppDetailPopWindowView? = null
    var selectTypePopView: SelectTypePopView? = null
    override fun OnHttpResponse(what: Int, result: String) {
        when (what) {
            0 -> {
                setAdapter(result)
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

    private fun setAdapter(result: String) {
        var jsfpAccountEntity: JsfpAccountEntity = GsonUtils.ktGsonToBean(result, JsfpAccountEntity::class.java)
        jsfpAccountEntity.content?.detail?.let {
            mList = (it)
        }
        if (null == adapter) {
            adapter = InstantDistributionAdapter(this, R.layout.item_instant_distribution, mList)
            rv_instant.adapter = adapter
        } else {
            adapter?.notifyDataSetChanged()
        }
        adapter?.onItemChildClickListener = BaseQuickAdapter.OnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.rl_instant_next -> {
                    val intent: Intent = Intent(this, DistributionrecordsActivity::class.java)
                    intent.putExtra("general_uuid", mList.get(position).general_uuid)
                    intent.putExtra("split_type", mList.get(position).split_type)
                    intent.putExtra("split_target", mList.get(position).split_target)
                    startActivity(intent)
                }
                R.id.tv_instant_left -> {
                    if (mList.get(position).open_withdrawals.equals("1")) {
                        val intent = Intent(this, ExchangeMoneyActivity::class.java)
                        intent.putExtra("money", mList.get(position).split_money);
                        intent.putExtra("general_uuid", mList.get(position).general_uuid)
                        intent.putExtra("split_type", mList.get(position).split_type)
                        intent.putExtra("split_target", mList.get(position).split_target)
                        startActivity(intent)
                    }
                }
                R.id.tv_instant_right -> {
                    if (mList.get(position).open_cashing.equals("1")) {
                        val intent = Intent(this, WithDrawalActivity::class.java)
                        intent.putExtra("general_uuid", mList.get(position).general_uuid)
                        intent.putExtra("split_type", mList.get(position).split_type)
                        intent.putExtra("split_target", mList.get(position).split_target)
                        startActivity(intent)
                    }
                }

            }
        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_base_back -> finish()
            R.id.tv_base_confirm -> startActivity(Intent(this, TransactionRecordsActivity::class.java))
            R.id.tv_instant_select -> {
                if (selectTypePopView == null) {
                    selectTypePopView = SelectTypePopView(this@InstantDistributionActivity, listGridView as ArrayList<SelectTypeEntity.ContentBean.ResultBean>?,
                            SelectTypeCallBack { view, position ->
                                run {
                                    listGridView.get(position).isCheck = 1
                                }
                            })
                    selectTypePopView?.setOnDismissListener(poponDismissListener())
                }
                selectTypePopView?.showPopupWindow(tv_instant_select)
            }
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
        moneyModel.getjsfpType(1, "", this)
    }

    private fun initView() {
        tv_base_title.setText("即时分配")
        tv_base_confirm.setText("交易记录")
        tv_base_confirm.setTextColor(ContextCompat.getColor(this, R.color.color_1ca1f4))
        iv_base_back.setOnClickListener(this)
        tv_base_confirm.setOnClickListener(this)
        tv_instant_select.setOnClickListener(this)
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_instant.layoutManager = linearLayoutManager
    }

    /**
     * 添加新笔记时弹出的popWin关闭的事件，主要是为了将背景透明度改回来
     *
     * @author cg
     */
    internal inner class poponDismissListener : PopupWindow.OnDismissListener {

        override fun onDismiss() {
            val lp = this@InstantDistributionActivity.getWindow().getAttributes()
            lp.alpha = 1.0f
            this@InstantDistributionActivity.getWindow().setAttributes(lp)
        }
    }
}


