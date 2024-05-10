package com.filloax.fxlib

import com.filloax.fxlib.nbt.getOrPut
import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentUtils
import net.minecraft.network.chat.Style
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.network.Filterable
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.ItemLore
import net.minecraft.world.item.component.WritableBookContent
import net.minecraft.world.item.component.WrittenBookContent

fun itemFromId(id: String): Item {
    return itemFromId(ResourceLocation(id))
}

fun itemFromId(id: ResourceLocation): Item {
    return BuiltInRegistries.ITEM.get(id)
}


/**
 * Get lore lines as mutable list string, whose changes are reflected on the item tags
 */
fun ItemStack.loreLines(): MutableList<Component> {
    return LoreListProxy(this)
}

private class LoreListProxy(val itemStack: ItemStack): AbstractMutableList<Component>() {
    private fun lore() = itemStack[DataComponents.LORE] ?: run {
        val lore = ItemLore.EMPTY
        itemStack[DataComponents.LORE] = lore
        lore
    }

    private fun <T> updateLore(updateLinesFunction: (MutableList<Component>) -> T): T {
        val list = lore().styledLines.toMutableList()
        val out = updateLinesFunction(list)
        val newLore = ItemLore(list)
        itemStack[DataComponents.LORE] = newLore
        return out
    }

    override val size: Int = lore().styledLines.size

    override fun get(index: Int): Component = lore().styledLines[index]
    override fun removeAt(index: Int): Component = updateLore { it.removeAt(index) }
    override fun set(index: Int, element: Component) = updateLore { it.set(index, element) }
    override fun add(index: Int, element: Component) = updateLore { it.add(index, element) }
}

fun createWrittenBook(title: Component, author: Component, pages: List<Component>): ItemStack {
    val book = ItemStack(Items.WRITTEN_BOOK)
    book.setBookTags(title, author, pages)
    return book
}

fun ItemStack.setBookTags(title: String?, author: String?, pages: List<Component>) {
    if (`is`(Items.WRITABLE_BOOK)) {
        this[DataComponents.WRITABLE_BOOK_CONTENT] = WritableBookContent(pages.map { Filterable.passThrough(it.string) })
    } else if (`is`(Items.WRITTEN_BOOK)) {
        this[DataComponents.WRITTEN_BOOK_CONTENT] = WrittenBookContent(
            Filterable.passThrough(title ?: Items.WRITTEN_BOOK.descriptionId),
            author ?: "???",
            0,
            pages.map { Filterable.passThrough(it) },
            true
        )
    } else {
        throw IllegalStateException("Cannot set pages of non-book item stack $this")
    }
}

fun ItemStack.setBookTags(pages: List<Component>) {
    setBookTags(null, null, pages)
}

fun ItemStack.setBookTags(title: Component, author: Component, pages: List<Component>) {
    setBookTags( title.string, author.string, pages)
}

fun ItemStack.getBookText(): List<Component> {
    return if (`is`(Items.WRITABLE_BOOK)) {
        this.getOrDefault(DataComponents.WRITABLE_BOOK_CONTENT, WritableBookContent.EMPTY).pages.map{Component.literal(it.raw)}
    } else if (`is`(Items.WRITTEN_BOOK)) {
        this.getOrDefault(DataComponents.WRITTEN_BOOK_CONTENT, WrittenBookContent.EMPTY).pages.map{it.raw}
    } else {
        throw IllegalStateException("Cannot get pages of non-book item stack $this")
    }
}

fun ItemStack.getBookTitle() =
    if (`is`(Items.WRITTEN_BOOK)) {
        this.getOrDefault(DataComponents.WRITTEN_BOOK_CONTENT, WrittenBookContent.EMPTY).title.raw
    } else {
        throw IllegalStateException("Cannot get title of non-written book item stack $this")
    }

fun ItemStack.getBookAuthor() =
    if (`is`(Items.WRITTEN_BOOK)) {
        this.getOrDefault(DataComponents.WRITTEN_BOOK_CONTENT, WrittenBookContent.EMPTY).author
    } else {
        throw IllegalStateException("Cannot get author of non-written book item stack $this")
    }