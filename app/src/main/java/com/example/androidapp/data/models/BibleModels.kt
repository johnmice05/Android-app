package com.example.androidapp.data.models

import kotlinx.serialization.Serializable

// ===== English Bible Models (Flat array structure) =====
@Serializable
data class EnglishBibleVerse(
    val book: String,
    val chapter: Int,
    val verse: Int,
    val text: String
)

// ===== Urdu Bible Models (Nested structure) =====
@Serializable
data class UrduBibleBook(
    val name: String,
    val abbreviation: String = "",
    val chapters: List<UrduChapter> = emptyList()
)

@Serializable
data class UrduChapter(
    val chapter: Int,
    val verses: List<UrduVerse> = emptyList()
)

@Serializable
data class UrduVerse(
    val verse: Int,
    val text: String
)

// ===== Unified Domain Models =====
data class Book(
    val id: Int,
    val name: String,
    val abbreviation: String,
    val language: String, // "English" or "Urdu"
    val testament: String = "" // "Old" or "New"
)

data class Chapter(
    val bookId: Int,
    val chapterNumber: Int,
    val language: String,
    val verseCount: Int = 0
)

data class Verse(
    val bookId: Int,
    val chapterNumber: Int,
    val verseNumber: Int,
    val text: String,
    val language: String
)

data class BibleReference(
    val book: String,
    val chapter: Int,
    val verse: Int,
    val text: String,
    val language: String
)
