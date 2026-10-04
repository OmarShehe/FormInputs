package com.omarshehe.forminputkotlin.adapter

import androidx.test.platform.app.InstrumentationRegistry
import com.omarshehe.forminputkotlin.interfaces.ItemSelectedListener
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class AutoCompleteAdapterTest {
    private fun filterCount(items: List<String>, query: String): Int {
        val inst = InstrumentationRegistry.getInstrumentation()
        val latch = CountDownLatch(1)
        var count = -1
        inst.runOnMainSync {
            val adapter = AutoCompleteAdapter(inst.targetContext, android.R.id.text1, items, object : ItemSelectedListener { override fun onItemSelected(item: String) {} })
            adapter.filter.filter(query) { count = it; latch.countDown() }
        }
        latch.await(5, TimeUnit.SECONDS)
        return count
    }

    @Test fun singleItemList() = assertEquals(1, filterCount(listOf("Dodoma"), "do"))
    @Test fun multiItemList() = assertEquals(2, filterCount(listOf("Dodoma", "Dar", "Mbeya"), "d"))
}
