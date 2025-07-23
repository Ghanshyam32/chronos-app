// app/src/main/java/com/ghanshyam/chronosapp/reminder/ReminderViewModel.kt
package com.ghanshyam.chronosapp.reminder

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ghanshyam.chronosapp.data.Reminder
import com.ghanshyam.chronosapp.data.ReminderRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class UiEvent {
    /** Now carries the saved reminder */
    data class SaveSuccess(val reminder: Reminder) : UiEvent()
    data class SaveError(val message: String)    : UiEvent()
}

class ReminderViewModel(
    private val userId: String,
    private val repo: ReminderRepository = ReminderRepository()
) : ViewModel() {

    val reminders: StateFlow<List<Reminder>> = repo
        .getRemindersFlow(userId)
        .map { it.reversed() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow: SharedFlow<UiEvent> = _eventFlow

    fun addReminder(
        title: String,
        description: String,
        timestamp: Long,
        imageUri: Uri? = null
    ) {
        viewModelScope.launch {
            _isSaving.value = true
            try {
                // 1️⃣ possibly upload image
                val imageUrl = imageUri?.let { repo.uploadImage(userId, it) }
                // 2️⃣ build reminder & save
                val toSave = Reminder(
                    title       = title,
                    description = description,
                    timestamp   = timestamp,
                    imageUrl    = imageUrl
                )
                val saved = repo.addReminder(userId, toSave)
                // 3️⃣ emit event with the saved reminder
                _eventFlow.emit(UiEvent.SaveSuccess(saved))
            } catch (e: Exception) {
                _eventFlow.emit(UiEvent.SaveError(e.localizedMessage ?: "Unknown error"))
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun updateReminder(reminder: Reminder) = viewModelScope.launch {
        repo.updateReminder(userId, reminder)
    }

    fun deleteReminder(reminderId: String) = viewModelScope.launch {
        repo.deleteReminder(userId, reminderId)
    }
}
