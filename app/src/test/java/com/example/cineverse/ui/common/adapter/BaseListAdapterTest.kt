package com.example.cineverse.ui.common.adapter

import androidx.recyclerview.widget.DiffUtil
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class BaseListAdapterTest {

    data class TestItem(val id: Int, val name: String)

    private class TestAdapter : BaseListAdapter<TestItem, androidx.viewbinding.ViewBinding>(TEST_DIFF) {
        override fun inflateBinding(
            inflater: android.view.LayoutInflater,
            parent: android.view.ViewGroup
        ): androidx.viewbinding.ViewBinding {
            throw NotImplementedError()
        }

        override fun bind(
            binding: androidx.viewbinding.ViewBinding,
            item: TestItem,
            position: Int
        ) {}

        fun getTestItem(position: Int) = getItem(position)
    }

    @Test
    fun adapter_initiallyEmpty() {
        val adapter = TestAdapter()

        assertThat(adapter.itemCount).isEqualTo(0)
    }

    @Test
    fun adapter_submitList_updatesItemCount() {
        val adapter = TestAdapter()
        val items = listOf(
            TestItem(1, "Item 1"),
            TestItem(2, "Item 2"),
            TestItem(3, "Item 3")
        )

        adapter.submitList(items)

        assertThat(adapter.itemCount).isEqualTo(3)
    }

    @Test
    fun adapter_submitList_emptyList() {
        val adapter = TestAdapter()
        val items = listOf(TestItem(1, "Item 1"))

        adapter.submitList(items)
        assertThat(adapter.itemCount).isEqualTo(1)

        adapter.submitList(emptyList())
        assertThat(adapter.itemCount).isEqualTo(0)
    }

    @Test
    fun adapter_getItem_returnsCorrectItem() {
        val adapter = TestAdapter()
        val items = listOf(
            TestItem(1, "Item 1"),
            TestItem(2, "Item 2")
        )
        adapter.submitList(items)

        val item = adapter.getTestItem(0)
        assertThat(item).isEqualTo(TestItem(1, "Item 1"))
    }

    companion object {
        private val TEST_DIFF = object : DiffUtil.ItemCallback<TestItem>() {
            override fun areItemsTheSame(oldItem: TestItem, newItem: TestItem) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: TestItem, newItem: TestItem) =
                oldItem == newItem
        }
    }
}
