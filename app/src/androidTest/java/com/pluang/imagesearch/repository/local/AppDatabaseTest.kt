package com.pluang.imagesearch.repository.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.pluang.imagesearch.models.Result
import com.pluang.imagesearch.models.Urls
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppDatabaseTest : TestCase() {

    private lateinit var appDatabase: AppDatabase
    private lateinit var resultDao: ResultDao


    @Before
    public override fun setUp() {
        super.setUp()
        val context = ApplicationProvider.getApplicationContext<Context>()
        appDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        resultDao = appDatabase.resultDao()
    }

    @After
    fun closeDB() {
        appDatabase.close()
    }


    @Test
    fun writeAndReadTest()= runBlocking {
        val query = "test"
        val result = Result(
            created_at = "10/10/2010",
            height = 300,
            width = 300,
            query = query,
            id = "testID",
            urls = Urls(
                small = "small",
                regular = "regular",
                full = "full",
                raw = "raw",
                thumb = "thumb"
            )
        )
        resultDao.insertResult(result)
        val results = resultDao.getResultsByQuery(query)
        assertThat(results.contains(result)).isTrue()
    }

}