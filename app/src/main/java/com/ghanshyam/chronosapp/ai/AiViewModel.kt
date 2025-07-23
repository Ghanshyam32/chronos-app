// app/src/main/java/com/ghanshyam/chronosapp/ai/AiViewModel.kt
package com.ghanshyam.chronosapp.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AiViewModel(
  private val repo: AIGreetingRepository = AIGreetingRepository()
) : ViewModel() {

  private val _greeting = MutableStateFlow<String?>(null)
  val greeting: StateFlow<String?> = _greeting.asStateFlow()

  private val _loading = MutableStateFlow(false)
  val loading: StateFlow<Boolean> = _loading.asStateFlow()

  fun fetchGreeting(prompt: String) {
    viewModelScope.launch {
      _loading.value = true
      try {
        _greeting.value = repo.getGreeting(prompt)
      } catch (e: Exception) {
        _greeting.value = "Error: ${e.localizedMessage}"
      } finally {
        _loading.value = false
      }
    }
  }

  fun clearGreeting() {
    _greeting.value = null
  }
}
