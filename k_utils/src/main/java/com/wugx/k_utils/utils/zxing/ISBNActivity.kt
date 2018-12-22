package com.wugx.k_utils.utils.zxing

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.wugx.k_utils.R
import kotlinx.android.synthetic.main.layout_scan_qode_input.*


class ISBNActivity : AppCompatActivity(), TextView.OnEditorActionListener {
    private var imm: InputMethodManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_scan_qode_input)
        initViews()
    }

    private fun initViews() {
        btnSearch.setOnClickListener {
            inputIsbnViewOut(true)
        }
        tvClose.setOnClickListener {
            onBackPressed()
        }
        etISBN.imeOptions = EditorInfo.IME_ACTION_SEARCH
        etISBN.setOnEditorActionListener(this)
        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    override fun onBackPressed() {
        val intent = Intent()
        setResult(Activity.RESULT_OK, intent)
        finish()
        overridePendingTransition(0, 0)
    }

    override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT
                || event != null && event.action == KeyEvent.ACTION_UP && event.keyCode == KeyEvent.KEYCODE_ENTER) {
            inputIsbnViewOut(true)
        }
        return true
    }

    private fun inputIsbnViewOut(isSearch: Boolean) {
        val isbn = etISBN!!.text.toString()
        if (isbn == "" && isSearch) {
            Toast.makeText(this, "请输入ISBN号", Toast.LENGTH_SHORT).show()
            return
        }
        val intent = Intent()
        intent.putExtra("isbn", isbn)
        setResult(Activity.RESULT_OK, intent)
        finish()
        if (null != imm) {
            imm!!.hideSoftInputFromWindow(etISBN!!.windowToken, 0)
        }
    }
}
