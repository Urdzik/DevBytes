/*
 * Copyright (C) 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.devbyteviewer.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.example.android.devbyteviewer.database.getDatabase
import com.example.android.devbyteviewer.domain.DevByteVideo
import com.example.android.devbyteviewer.repository.VideosRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * DevByteViewModel предназначен для хранения и управления данными, относящимися к пользовательскому интерфейсу, с учетом жизненного цикла. Эта
 * позволяет данным пережить изменения конфигурации, такие как поворот экрана. Кроме того, фон
 * работа, такая как выборка сетевых результатов, может продолжаться через изменения конфигурации и доставлять
 * результаты после появления нового фрагмента или действия.
 *
 * @param application Приложение, к которому прикреплена эта модель, безопасно хранить
 * ссылка на приложения через ротацию, так как приложение никогда не воссоздается во время действия
 * или фрагменты событий жизненного цикла.
 */
class DevByteViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * Это работа для всех сопрограмм, запущенных этой ViewModel.
     *
     * Отмена этого задания отменит все сопрограммы, запущенные этой ViewModel.
     */
    private val viewModelJob = SupervisorJob()

    /**
     * Это основная область для всех сопрограмм, запускаемых MainViewModel.
     *
     * Поскольку мы передаем viewModelJob, вы можете отменить все сопрограммы, запущенные uiScope, вызвав
     * viewModelJob.cancel ()
     */
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    /**
     * Список воспроизведения видео, которые могут быть показаны на экране. Это личное, чтобы избежать разоблачения
     * способ установить это значение для наблюдателей.
     */


    /**
     * Event triggered for network error. This is private to avoid exposing a
     * way to set this value to observers.
     */
    private var _eventNetworkError = MutableLiveData<Boolean>(false)

    /**
     * Event triggered for network error. Views should use this to get access
     * to the data.
     */
    val eventNetworkError: LiveData<Boolean>
        get() = _eventNetworkError

    /**
     * Flag to display the error message. This is private to avoid exposing a
     * way to set this value to observers.
     */
    private var _isNetworkErrorShown = MutableLiveData<Boolean>(false)

    /**
     * Flag to display the error message. Views should use this to get access
     * to the data.
     */
    val isNetworkErrorShown: LiveData<Boolean>
        get() = _isNetworkErrorShown


    private val videosRepository = VideosRepository(getDatabase(application))
    val playlist = videosRepository.videos

    /**
     * init{} is called immediately when this ViewModel is created.
     */
    init {
        refreshDataFromRepository()
    }

    /**
     * Refresh data from network and pass it via LiveData. Use a coroutine launch to get to
     * background thread.
     */
    private fun refreshDataFromRepository() = viewModelScope.launch {

        try {
            videosRepository.refreshVideos()
            _eventNetworkError.value = false
            _isNetworkErrorShown.value = false

        } catch (networkError: IOException) {
            // Show a Toast error message and hide the progress bar.
            if (playlist.value!!.isEmpty())
                _eventNetworkError.value = true
        }
    }

    /**
     * Resets the network error flag.
     */
    fun onNetworkErrorShown() {
        _isNetworkErrorShown.value = true
    }


    /**
     * Cancel all coroutines when the ViewModel is cleared
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    /**
     * Factory for constructing DevByteViewModel with parameter
     */
    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DevByteViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DevByteViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}
