package com.mob.lee.fastair.viewmodel

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mob.lee.fastair.base.AndroidScope
import com.mob.lee.fastair.model.APPLICATION
import com.mob.lee.fastair.model.Category
import com.mob.lee.fastair.model.EXCEL
import com.mob.lee.fastair.model.MUSIC
import com.mob.lee.fastair.model.OtherCategory
import com.mob.lee.fastair.model.PDF
import com.mob.lee.fastair.model.PICTURE
import com.mob.lee.fastair.model.POWERPOINT
import com.mob.lee.fastair.model.Record
import com.mob.lee.fastair.model.STATE_CHECK
import com.mob.lee.fastair.model.TXT
import com.mob.lee.fastair.model.VIDEO
import com.mob.lee.fastair.model.WORD
import com.mob.lee.fastair.model.ZIP
import com.mob.lee.fastair.repository.RecordRep
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.consumeEachIndexed
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

/**
 * Created by Andy on 2017/11/13.
 */
class FileViewModel : ViewModel() {
    val record = MutableLiveData<Record>()
    val needUpdate=MutableLiveData<Boolean>()
    var cetegory:Category=OtherCategory()
    var mCheckAll = false
    var mIsDes = true

    fun load(scope : AndroidScope, context : Context, position:Int) {
        needUpdate.value=true
        scope.launch(Dispatchers.Main) {
            val channel = RecordRep.load(scope,context, position)
            channel.consumeEachIndexed {
                if(0==it.index){
                    needUpdate.value=false
                }
                record.value = it.value
            }
            channel.invokeOnClose {
                needUpdate.value=false
            }
        }

    }

    fun checkedrecords() : List<Record> {
        return emptyList()
    }
}