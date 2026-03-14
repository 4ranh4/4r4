package com.app4r4.ui.onboarding

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    companion object {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val USER_TRANSPORT = stringPreferencesKey("user_transport")
        val USER_DIET = stringPreferencesKey("user_diet")
    }

    val onboardingCompleted = dataStore.data
        .map { it[ONBOARDING_COMPLETED] ?: false }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun completeOnboarding(transport: String, diet: String) {
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[ONBOARDING_COMPLETED] = true
                prefs[USER_TRANSPORT] = transport
                prefs[USER_DIET] = diet
            }
        }
    }
}
