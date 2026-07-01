package com.example.androidapp.data.repository

import android.content.Context
import com.example.androidapp.data.models.*
import com.example.androidapp.data.parser.BibleJsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for accessing Bible data (English & Urdu) offline
 * Provides unified interface for querying books, chapters, and verses
 */
class BibleRepository(private val context: Context) {

    private val parser = BibleJsonParser(context)
    
    // Cache loaded data
    private var englishVerses: List<EnglishBibleVerse>? = null
    private var urduBooks: List<UrduBibleBook>? = null
    private var englishBooks: List<Book>? = null
    private var urduBooksStructured: List<Book>? = null
    private var allBooks: List<Book>? = null
    private var allChapters: List<Chapter>? = null
    private var allVerses: List<Verse>? = null

    /**
     * Initialize and load both Bible files
     */
    suspend fun initialize() = withContext(Dispatchers.IO) {
        try {
            // Load English Bible
            englishVerses = parser.loadEnglishBible("English_bible.json")
            englishBooks = parser.englishVersesToStructure(englishVerses ?: emptyList())
            
            // Load Urdu Bible
            urduBooks = parser.loadUrduBible("urdu_bible_clean.json")
            urduBooksStructured = parser.urduBooksToStructure(urduBooks ?: emptyList())
            
            // Combine all books
            allBooks = ((englishBooks ?: emptyList()) + (urduBooksStructured ?: emptyList()))
                .distinctBy { it.id }
                .sortedBy { it.id }
        } catch (e: Exception) {
            println("Error initializing Bible Repository: ${e.message}")
        }
    }

    /**
     * Get all books
     */
    fun getBooks(): List<Book> {
        return allBooks ?: emptyList()
    }

    /**
     * Get all books in a specific language
     */
    fun getBooksByLanguage(language: String): List<Book> {
        return getBooks().filter { it.language == language }
    }

    /**
     * Get a specific book by name and language
     */
    fun getBook(name: String, language: String): Book? {
        return getBooks().firstOrNull { it.name == name && it.language == language }
    }

    /**
     * Get all chapters
     */
    fun getChapters(): List<Chapter> {
        if (allChapters == null) {
            val englishChapters = parser.englishVersesToChapters(englishVerses ?: emptyList())
            val urduChapters = parser.urduBooksToChapters(urduBooks ?: emptyList())
            allChapters = (englishChapters + urduChapters)
                .distinctBy { it.bookId to it.chapterNumber }
                .sortedWith(compareBy({ it.bookId }, { it.chapterNumber }))
        }
        return allChapters ?: emptyList()
    }

    /**
     * Get chapters for a specific book
     */
    fun getChaptersByBook(bookId: Int): List<Chapter> {
        return getChapters().filter { it.bookId == bookId }
    }

    /**
     * Get chapters for a book by name and language
     */
    fun getChaptersByBookName(bookName: String, language: String): List<Chapter> {
        val book = getBook(bookName, language) ?: return emptyList()
        return getChaptersByBook(book.id)
    }

    /**
     * Get all verses
     */
    fun getVerses(): List<Verse> {
        if (allVerses == null) {
            val englishVers = mutableListOf<Verse>()
            val urduVers = mutableListOf<Verse>()
            
            // Convert English verses
            englishVerses?.forEach { verse ->
                val book = englishBooks?.firstOrNull { it.name == verse.book }
                if (book != null) {
                    englishVers.add(
                        Verse(
                            bookId = book.id,
                            chapterNumber = verse.chapter,
                            verseNumber = verse.verse,
                            text = verse.text,
                            language = "English"
                        )
                    )
                }
            }
            
            // Convert Urdu verses
            urduBooks?.forEachIndexed { bookIndex, book ->
                book.chapters.forEach { chapter ->
                    chapter.verses.forEach { verse ->
                        urduVers.add(
                            Verse(
                                bookId = bookIndex + 1,
                                chapterNumber = chapter.chapter,
                                verseNumber = verse.verse,
                                text = verse.text,
                                language = "Urdu"
                            )
                        )
                    }
                }
            }
            
            allVerses = (englishVers + urduVers).sortedWith(
                compareBy({ it.bookId }, { it.chapterNumber }, { it.verseNumber })
            )
        }
        return allVerses ?: emptyList()
    }

    /**
     * Get verses for a specific chapter
     */
    fun getVersesByChapter(bookId: Int, chapterNumber: Int): List<Verse> {
        return getVerses().filter { it.bookId == bookId && it.chapterNumber == chapterNumber }
    }

    /**
     * Get verses for a chapter by name and language
     */
    fun getVersesByChapterName(bookName: String, chapterNumber: Int, language: String): List<Verse> {
        val book = getBook(bookName, language) ?: return emptyList()
        return getVersesByChapter(book.id, chapterNumber)
    }

    /**
     * Get a specific verse
     */
    fun getVerse(bookName: String, chapter: Int, verse: Int, language: String): Verse? {
        return getVersesByChapterName(bookName, chapter, language)
            .firstOrNull { it.verseNumber == verse }
    }

    /**
     * Search verses by text (case-insensitive)
     */
    fun searchVerses(query: String, language: String? = null): List<Verse> {
        val lowerQuery = query.lowercase()
        return getVerses()
            .filter { verse ->
                (language == null || verse.language == language) &&
                verse.text.lowercase().contains(lowerQuery)
            }
            .take(100) // Limit results for performance
    }

    /**
     * Get total book count
     */
    fun getBookCount(): Int = getBooks().size

    /**
     * Get total chapter count
     */
    fun getChapterCount(): Int = getChapters().size

    /**
     * Get total verse count
     */
    fun getVerseCount(): Int = getVerses().size

    /**
     * Get statistics
     */
    fun getStatistics(): BibleStatistics {
        return BibleStatistics(
            totalBooks = getBookCount(),
            totalChapters = getChapterCount(),
            totalVerses = getVerseCount(),
            englishBooks = getBooksByLanguage("English").size,
            urduBooks = getBooksByLanguage("Urdu").size
        )
    }
}

data class BibleStatistics(
    val totalBooks: Int = 0,
    val totalChapters: Int = 0,
    val totalVerses: Int = 0,
    val englishBooks: Int = 0,
    val urduBooks: Int = 0
)
