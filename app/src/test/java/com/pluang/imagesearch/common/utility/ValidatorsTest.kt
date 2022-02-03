package com.pluang.imagesearch.common.utility


import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ValidatorsTest{

    @Test
    fun whenInputIsValid(){
        val input = "test"
        val result = Validators.validateSearchQuery(input)
        assertThat(result).isEqualTo(true)
    }

    @Test
    fun whenInputIsNull(){
        val input :String?=null
        val resultNull = Validators.validateSearchQuery(input)
        assertThat(resultNull).isEqualTo(false)
    }

    @Test
    fun whenInputIsEmpty(){
        val input =""
        val resultNull = Validators.validateSearchQuery(input)
        assertThat(resultNull).isEqualTo(false)
    }

    @Test
    fun whenInputIsBlank(){
        val input = "  "
        val resultNull = Validators.validateSearchQuery(input)
        assertThat(resultNull).isEqualTo(false)
    }

}