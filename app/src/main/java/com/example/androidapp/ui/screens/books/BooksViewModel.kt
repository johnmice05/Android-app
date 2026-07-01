package com.example.androidapp.ui.screens.books

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidapp.data.models.Book
import com.example.androidapp.data.repository.BibleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BooksScreenState(
    val isLoading: Boolean = true,
    val englishBooks: List<Book> = emptyList(),
    val urduBooks: List<Book> = emptyList(),
    val selectedLanguage: String = "English",
    val error: String? = null
)

@HiltViewModel
class BooksViewModel @Inject constructor(
    private val bibleRepository: BibleRepository
) : ViewModel() {

    private val _state = MutableStateFlow(BooksScreenState())
    val state: StateFlow<BooksScreenState> = _state.asStateFlow()

    init {
        loadBooks()
    }

    private fun loadBooks() {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)
                
                // Initialize repository and load data
                bibleRepository.initialize()
                
                val englishBooks = bibleRepository.getBooksByLanguage("English")
                val urduBooks = bibleRepository.getBooksByLanguage("Urdu")
                
                _state.value = _state.value.copy(
                    isLoading = false,
                    englishBooks = englishBooks,
                    urduBooks = urduBooks,
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

    fun setSelectedLanguage(language: String) {
        _state.value = _state.value.copy(selectedLanguage = language)
    }

    fun getCurrentBooks(): List<Book> {
        return if (_state.value.selectedLanguage == "English") {
            _state.value.englishBooks
        } else {
            _state.value.urduBooks
        }
    }
}
