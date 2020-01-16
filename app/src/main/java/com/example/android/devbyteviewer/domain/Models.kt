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

package com.example.android.devbyteviewer.domain

import com.example.android.devbyteviewer.util.smartTruncate

/**
 * Доменные объекты - это простые классы данных Kotlin, которые представляют вещи в нашем приложении. Эти
 * объекты, которые должны отображаться на экране или манипулировать приложением.
 *
 * @see database для объектов, которые отображаются в базе данных
 * @see network для объектов, которые анализируют или готовят сетевые вызовы
 */

/**
 * Видео представляет собой девбайт, который можно воспроизвести.
 */
data class DevByteVideo(val title: String,
                        val description: String,
                        val url: String,
                        val updated: String,
                        val thumbnail: String) {

    /**
     * Краткое описание используется для отображения усеченных описаний в пользовательском интерфейсе.
     */
    val shortDescription: String
        get() = description.smartTruncate(200)
}