package com.example.androidapp.ui.screens.chapters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidapp.data.models.Chapter
import com.example.androidapp.data.repository.BibleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChaptersScreenState(
    val isLoading: Boolean = true,
    val bookName: String = "",
    val bookLanguage: String = "English",
    val chapters: List<Chapter> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class ChaptersViewModel @Inject constructor(
    private val bibleRepository: BibleRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ChaptersScreenState())
    val state: StateFlow<ChaptersScreenState> = _state.asStateFlow()

    fun loadChapters(bookName: String, language: String) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(
                    isLoading = true,
                    bookName = bookName,
                    bookLanguage = language
                )
                
                val chapters = bibleRepository.getChaptersByBookName(bookName, language)
                
                _state.value = _state.value.copy(
                    isLoading = false,
                    chapters = chapters,
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
}
