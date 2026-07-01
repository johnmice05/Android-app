package com.example.androidapp.data.parser

import android.content.Context
import com.example.androidapp.data.models.*
import kotlinx.serialization.json.Json
import java.io.BufferedReader

/**
 * Parser for loading and processing Bible JSON files
 * Supports both English (flat array) and Urdu (nested) formats
 */
class BibleJsonParser(private val context: Context) {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    /**
     * Load English Bible from JSON file (flat array format)
     */
    fun loadEnglishBible(fileName: String): List<EnglishBibleVerse> {
        return try {
            val jsonString = loadJsonFromAssets(fileName)
            json.decodeFromString<List<EnglishBibleVerse>>(jsonString)
        } catch (e: Exception) {
            println("Error loading English Bible: ${e.message}")
            emptyList()
        }
    }

    /**
     * Load Urdu Bible from JSON file (nested structure)
     */
    fun loadUrduBible(fileName: String): List<UrduBibleBook> {
        return try {
            val jsonString = loadJsonFromAssets(fileName)
            // Handle both wrapped and unwrapped formats
            val bibleData = try {
                json.decodeFromString<List<UrduBibleBook>>(jsonString)
            } catch (e: Exception) {
                // Try wrapped format with "books" key
                json.decodeFromString<BibleWrapper>(jsonString).books
            }
            bibleData
        } catch (e: Exception) {
            println("Error loading Urdu Bible: ${e.message}")
            emptyList()
        }
    }

    /**
     * Load JSON file from assets folder
     */
    private fun loadJsonFromAssets(fileName: String): String {
        return context.assets.open(fileName).use { inputStream ->
            BufferedReader(inputStream.bufferedReader()).use { reader ->
                reader.readText()
            }
        }
    }

    /**
     * Convert flat English verses to structured books/chapters
     */
    fun englishVersesToStructure(verses: List<EnglishBibleVerse>): List<Book> {
        val booksMap = mutableMapOf<String, MutableList<EnglishBibleVerse>>()
        
        verses.forEach { verse ->
            booksMap.getOrPut(verse.book) { mutableListOf() }.add(verse)
        }

        return booksMap.entries.mapIndexed { index, (bookName, bookVerses) ->
            Book(
                id = index + 1,
                name = bookName,
                abbreviation = bookName.take(3),
                language = "English",
                testament = determineTestament(bookName)
            )
        }.sortedBy { it.id }
    }

    /**
     * Convert nested Urdu books to domain model
     */
    fun urduBooksToStructure(urduBooks: List<UrduBibleBook>): List<Book> {
        return urduBooks.mapIndexed { index, urduBook ->
            Book(
                id = index + 1,
                name = urduBook.name,
                abbreviation = urduBook.abbreviation,
                language = "Urdu",
                testament = determineTestament(urduBook.name)
            )
        }
    }

    /**
     * Extract chapters from English verses
     */
    fun englishVersesToChapters(verses: List<EnglishBibleVerse>): List<Chapter> {
        val books = englishVersesToStructure(verses)
        val bookMap = books.associateBy { it.name }

        val chapterSet = mutableSetOf<Pair<Int, Int>>() // bookId, chapter
        
        verses.forEach { verse ->
            val book = bookMap[verse.book]
            if (book != null) {
                chapterSet.add(book.id to verse.chapter)
            }
        }

        return chapterSet.map { (bookId, chapterNum) ->
            val versesInChapter = verses.count { it.chapter == chapterNum && bookMap[it.book]?.id == bookId }
            Chapter(
                bookId = bookId,
                chapterNumber = chapterNum,
                language = "English",
                verseCount = versesInChapter
            )
        }.sortedWith(compareBy({ it.bookId }, { it.chapterNumber }))
    }

    /**
     * Extract chapters from Urdu books
     */
    fun urduBooksToChapters(urduBooks: List<UrduBibleBook>): List<Chapter> {
        val chapters = mutableListOf<Chapter>()
        
        urduBooks.forEachIndexed { bookIndex, book ->
            book.chapters.forEach { chapter ->
                chapters.add(
                    Chapter(
                        bookId = bookIndex + 1,
                        chapterNumber = chapter.chapter,
                        language = "Urdu",
                        verseCount = chapter.verses.size
                    )
                )
            }
        }
        
        return chapters.sortedWith(compareBy({ it.bookId }, { it.chapterNumber }))
    }

    /**
     * Determine Old or New Testament based on book name
     */
    private fun determineTestament(bookName: String): String {
        val newTestamentBooks = setOf(
            "Matthew", "Mark", "Luke", "John",
            "Acts", "Romans", "1 Corinthians", "2 Corinthians",
            "Galatians", "Ephesians", "Philippians", "Colossians",
            "1 Thessalonians", "2 Thessalonians", "1 Timothy", "2 Timothy",
            "Titus", "Philemon", "Hebrews", "James", "1 Peter", "2 Peter",
            "1 John", "2 John", "3 John", "Jude", "Revelation",
            // Urdu equivalents
            "متی", "مرقس", "لوقا", "یوحنا",
            "اعمال", "رومیوں", "1 کرنتھیوں", "2 کرنتھیوں",
            "گلطیوں", "افسیوں", "فلپیوں", "کلسیوں"
        )
        
        return if (bookName in newTestamentBooks) "New" else "Old"
    }
}

@kotlinx.serialization.Serializable
data class BibleWrapper(
    val books: List<UrduBibleBook> = emptyList()
)
