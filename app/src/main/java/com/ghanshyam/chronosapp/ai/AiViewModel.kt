package com.ghanshyam.chronosapp.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AiViewModel(
  private val repo: GreetingRepository = GreetingRepository()
) : ViewModel() {

  private val _greeting = MutableStateFlow<String?>(null)
  val greeting: StateFlow<String?> = _greeting

  private val _loading = MutableStateFlow(false)
  val loading: StateFlow<Boolean> = _loading

  fun fetchGreeting(prompt: String) {
    viewModelScope.launch {
      _loading.value = true
      try {
        _greeting.value = repo.getGreeting(prompt)
      } catch (e: Exception) {
        _greeting.value = "Error: ${e.message}"
      } finally {
        _loading.value = false
      }
    }
  }

  fun clearGreeting() {
    _greeting.value = null
  }
}
