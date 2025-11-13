package com.calc.app.viewmodel

import androidx.lifecycle.ViewModel
import com.calc.app.data.CalculationHistory
import com.calc.app.data.CalculationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HistoryViewModel : ViewModel() {
    
    private val _historyList = MutableStateFlow<List<CalculationHistory>>(emptyList())
    val historyList = _historyList.asStateFlow()
    
    private val _filteredType = MutableStateFlow<CalculationType?>(null)
    val filteredType = _filteredType.asStateFlow()
    
    val filteredHistory = MutableStateFlow<List<CalculationHistory>>(emptyList())
    
    init {
        updateFilteredHistory()
    }
    
    fun addHistory(history: CalculationHistory) {
        _historyList.update { currentList ->
            listOf(history) + currentList
        }
        updateFilteredHistory()
    }
    
    fun clearHistory() {
        _historyList.value = emptyList()
        updateFilteredHistory()
    }
    
    fun deleteHistoryItem(id: String) {
        _historyList.update { currentList ->
            currentList.filter { it.id != id }
        }
        updateFilteredHistory()
    }
    
    fun setFilter(type: CalculationType?) {
        _filteredType.value = type
        updateFilteredHistory()
    }
    
    private fun updateFilteredHistory() {
        filteredHistory.value = if (_filteredType.value == null) {
            _historyList.value
        } else {
            _historyList.value.filter { it.type == _filteredType.value }
        }
    }
    
    companion object {
        private var instance: HistoryViewModel? = null
        
        fun getInstance(): HistoryViewModel {
            if (instance == null) {
                instance = HistoryViewModel()
            }
            return instance!!
        }
    }
}

