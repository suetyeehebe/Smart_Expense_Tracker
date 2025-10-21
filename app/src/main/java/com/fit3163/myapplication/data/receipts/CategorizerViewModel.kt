package com.fit3163.myapplication.data.receipts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fit3163.myapplication.data.receipts.APIService
import com.fit3163.myapplication.data.receipts.PredictRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class SuggestionState(
    val label: String? = null,
    val confidence: Double? = null,
    val top3: List<Pair<String, Int>> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null
)

class CategorizerViewModel(
    private val api: APIService
) : ViewModel() {

    private val _ui = MutableStateFlow(SuggestionState())
    val ui: StateFlow<SuggestionState> = _ui

    fun clear() { _ui.value = SuggestionState() }

    fun suggest(text: String) {
        if (text.isBlank()) return
        _ui.value = _ui.value.copy(loading = true, error = null)
        viewModelScope.launch {
            try {
                val resp = api.predict(PredictRequest(text = text))
                val top3 = resp.top3.map { it.label to (it.confidence * 100).toInt() }
                _ui.value = SuggestionState(
                    label = resp.label,
                    confidence = resp.confidence,
                    top3 = top3,
                    loading = false
                )
            } catch (e: Exception) {
                _ui.value = _ui.value.copy(loading = false, error = e.message ?: "Network error")
            }
        }
    }

    class Factory(private val api: APIService) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CategorizerViewModel(api) as T
        }
    }
}