package com.pluang.imagesearch.common.utility

object Validators {
    fun validateSearchQuery(query:String?):Boolean{
        return !query.isNullOrEmpty() && !query.isNullOrBlank()
    }
}