package com.horizonloop.app

import android.content.Context
import androidx.lifecycle.ViewModel
import com.horizonloop.app.data.*
import com.horizonloop.app.ui.viewmodel.AppViewModel

/**
 * MainViewModel.kt
 * ViewModel for MainActivity - wraps AppViewModel
 */

class MainViewModel : ViewModel() {
    private val vm = AppViewModel()

    // Mutable properties - expose vm state via getter/setter
    var searchQuery: String
        get() = vm.searchQuery
        set(value) { vm.searchQuery = value }
    
    var currentFilter: FilterType
        get() = vm.currentFilter
        set(value) { vm.currentFilter = value }
    
    var showHomeView: Boolean
        get() = vm.showHomeView
        set(value) { vm.showHomeView = value }
    
    var showCapsuleMenu: Boolean
        get() = vm.showCapsuleMenu
        set(value) { vm.showCapsuleMenu = value }
    
    var showTranslationDebug: Boolean
        get() = vm.showTranslationDebug
        set(value) { vm.showTranslationDebug = value }
    
    var activeTab: ActiveTab
        get() = vm.activeTab
        set(value) { vm.activeTab = value }
    
    var isPlaying: Boolean
        get() = vm.isPlaying
        set(value) { vm.isPlaying = value }
    
    var audioMode: Boolean
        get() = vm.audioMode
        set(value) { vm.audioMode = value }

    // Read-only properties
    val audioFiles get() = vm.audioFiles
    val filteredAudioFiles get() = vm.filteredAudioFiles
    val currentAudioTitle get() = vm.currentAudioTitle
    val currentAudioFilePath get() = vm.currentAudioFilePath
    val showSettingsDialog get() = vm.showSettingsDialog
    val isTranslating get() = vm.isTranslating
    val isPlaybackEnded get() = vm.isPlaybackEnded
    val currentPlaybackTime get() = vm.currentPlaybackTime
    val totalDuration get() = vm.totalDuration
    val currentSpeedIndex get() = vm.currentSpeedIndex
    val activeLoopId get() = vm.activeLoopId
    val notes get() = vm.notes
    val loops get() = vm.loops
    val translatedDialogues get() = vm.translatedDialogues
    val translationProgress get() = vm.translationProgress
    val translationLog get() = vm.translationLog
    val translationSteps get() = vm.translationSteps
    val selectedDialogueIds get() = vm.selectedDialogueIds
    val currentDialogue get() = vm.currentDialogue
    val speeds get() = vm.speeds
    val currentSpeed get() = vm.currentSpeed

    fun loadAudioFiles(context: Context) = vm.loadAudioFiles(context)
    fun selectAudio(audio: Audio) = vm.selectAudio(audio)
    fun filterAudioFiles() = vm.filterAudioFiles()
    fun togglePlayPause() = vm.togglePlayPause()
    fun rewind() = vm.rewind()
    fun forward() = vm.forward()
    fun seekTo(position: Double) = vm.seekTo(position)
    fun setSpeed(index: Int) = vm.setSpeed(index)
    fun updatePlaybackState() = vm.updatePlaybackState()
    fun formatTime(seconds: Double) = vm.formatTime(seconds)
    fun getProgressPercent() = vm.getProgressPercent()
    fun addNote(text: String) = vm.addNote(text)
    fun deleteNote(id: Int) = vm.deleteNote(id)
    fun addLoop(title: String, startTime: String, endTime: String, color: Int) = vm.addLoop(title, startTime, endTime, color)
    fun deleteLoop(id: Int) = vm.deleteLoop(id)
    fun playLoop(loop: Loop) = vm.playLoop(loop)
    fun translateAudio(context: Context) = vm.translateAudio(context)
    fun toggleAudioMode() = vm.toggleAudioMode()
    fun selectDialogue(dialogue: Dialogue) = vm.selectDialogue(dialogue)
    fun clearDialogueSelection() = vm.clearDialogueSelection()
    fun goHome() = vm.goHome()
    fun toggleShowCapsuleMenu() = vm.toggleShowCapsuleMenu()
    fun hideCapsuleMenu() = vm.hideCapsuleMenu()
}
