package com.example.androidapp.ui.screens.verses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidapp.data.models.Verse
import com.example.androidapp.data.repository.BibleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VersesScreenState(
    val isLoading: Boolean = true,
    val bookName: String = "",
    val chapterNumber: Int = 0,
    val bookLanguage: String = "English",
    val verses: List<Verse> = emptyList(),
    val currentVerseIndex: Int = 0,
    val error: String? = null
)

@HiltViewModel
class VersesViewModel @Inject constructor(
    private val bibleRepository: BibleRepository
) : ViewModel() {

    private val _state = MutableStateFlow(VersesScreenState())
    val state: StateFlow<VersesScreenState> = _state.asStateFlow()

    fun loadVerses(bookName: String, chapter: Int, language: String) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(
                    isLoading = true,
                    bookName = bookName,
                    chapterNumber = chapter,
                    bookLanguage = language
                )
                
                val verses = bibleRepository.getVersesByChapterName(bookName, chapter, language)
                
                _state.value = _state.value.copy(
                    isLoading = false,
                    verses = verses,
                    error = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    fun nextVerse() {
        if (_state.value.currentVerseIndex < _state.value.verses.size - 1) {
            _state.value = _state.value.copy(
                currentVerseIndex = _state.value.currentVerseIndex + 1
            )
        }
    }

    fun previousVerse() {
        if (_state.value.currentVerseIndex > 0) {
            _state.value = _state.value.copy(
                currentVerseIndex = _state.value.currentVerseIndex - 1
            )
        }
    }

    fun goToVerse(index: Int) {
        if (index >= 0 && index < _state.value.verses.size) {
            _state.value = _state.value.copy(currentVerseIndex = index)
        }
    }
}
