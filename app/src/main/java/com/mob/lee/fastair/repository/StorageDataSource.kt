package com.mob.lee.fastair.repository

import com.mob.lee.fastair.model.DataWrap
import java.io.File
import java.lang.Exception

class StorageDataSource :DataSource{
    suspend fun delete(files:List<File>):DataWrap<Boolean>{
        return try {
            val result = files.map { it.delete() }.all { it }
            if(result){
                DataWrap.success(true)
            }else{
                DataWrap.error()
            }
        }catch (e:Exception){
            DataWrap.error(msg = e.message)
        }
    }
}