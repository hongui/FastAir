package com.mob.lee.fastair.fragment

import com.mob.lee.fastair.R
import com.mob.lee.fastair.base.AppFragment
import android.text.Spanned
import android.graphics.Typeface
import android.text.style.StyleSpan
import android.text.style.ForegroundColorSpan
import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.fragment_text.*


/**
 * Created by Andy on 2018/1/20.
 */
class TextFragment : AppFragment() {

    override fun layout() = R.layout.fragment_text


    override fun setting() {
        val type = arguments?.getInt("type") ?: 0

        val content = if (0 == type) {
            toolbar("使用帮助")
            help()
        } else {
            toolbar("关于")
            about()
        }
        textContent.text = content
    }

    private fun help(): CharSequence {
        var start = 0
        val span = SpannableStringBuilder("快传使用帮助")
        setTitle(span, start)
        start = span.length
        span.append("发送文件")
        setTitle(span, start)
        span.append("\t1. 点击主页中想要发送的文件分类项，进入详情页；\n"
                + "\t2. 选择想要发送的文件，点击右下方的“咻”按钮，进入设备查找页；\n"
                + "\t3. 选择发送方设备，进行连接；\n"
                + "\t4. 列表项的右边显示为绿色的完成后，表示文件发送成功。\n")
        start = span.length
        span.append("接收文件")
        setTitle(span, start)
        span.append(
                "\t1. 点击主页右下方的下载图标，进入设备查找页；\n" + "\t2. 参照“发送文件”的第3,4步。\n"
        )
        start = span.length
        span.append("修改文件保存路径")
        setTitle(span, start)
        span.append(
                "\t1. 点击主页左上方的菜单项，打开左侧抽屉菜单；\n"
                        + "\t2. 点击保存路径菜单，进入文件夹选择页；\n"
                        + "\t3. 点击想要保存的路径，进入子目录，也可点击上方的导航条回退；\n"
                        + "\t4. 点击右上方的选择，下一次的文件传输文件将保存在选定的文件夹中。\n"
        )
        start = span.length
        span.append("其他说明")
        setTitle(span, start)
        span.append(
                ("\t1. 传输文件前需打开WI-FI开关，但可不连接热点；\n"
                        + "\t2. 文件默认保存在SD卡中的Download目录下；\n"
                        + "\t3. 文件详情页可长按呼出菜单，对选定文件进行查看内容，详情，删除操作；\n"
                        + "\t4. 聊天页长按聊天项可复制文字内容，可在其他应用中粘贴；\n"
                        + "\t5. 使用前离电磁干扰越远，设备查找速度越快，文件传输速度越快;\n")
        )
        start = span.length
        span.append("\t6. 注意，v1.0版本与v1.1版本之间的聊天功能无效，请升级至v1.1;\n")
        span.append("\t7. 传输结束后，出现点击无效的情况，请退出程序即可恢复，无需杀死程序。\n")
        span.setSpan(ForegroundColorSpan(ContextCompat.getColor(context!!, R.color.color_red)), start, span.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return span
    }

    private fun about(): CharSequence {
        var start = 0
        val span = SpannableStringBuilder("关于快传")
        setTitle(span, start)
        span.append("\t快传是无需借助任何现有网络就可进行文件传输及聊天的应用，传输距离根据硬件差异大约小于等于23米左右，传输速度均值为6.8MB/s。\n")
        start = span.length
        span.append("关于作者")
        setTitle(span, start)
        span.append("毕业于云南大学-电子信息工程，目前是一名Android工程师，欢迎骚扰！\n")
        span.append("\tQ  Q：")
        start = span.length
        span.append("632518410\n")
        setContact(span, start)
        span.append("\t邮箱：")
        start = span.length
        span.append("andytab@163.com\n")
        setContact(span, start)
        span.append("\t博客：")
        start = span.length
        span.append("www.andytab.xyz\n")
        setContact(span, start)
        return span
    }

    private fun setTitle(span: SpannableStringBuilder, start: Int) {
        span.setSpan(ForegroundColorSpan(ContextCompat.getColor(context!!, R.color.colorPrimaryText)), start, span.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        span.setSpan(StyleSpan(Typeface.BOLD), start, span.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        span.setSpan(RelativeSizeSpan(1.2f), start, span.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        span.append("\n")
    }

    private fun setContact(span: SpannableStringBuilder, start: Int) {
        span.setSpan(ForegroundColorSpan(ContextCompat.getColor(context!!, R.color.colorPrimaryText)), start, span.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        span.setSpan(StyleSpan(Typeface.BOLD), start, span.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
}