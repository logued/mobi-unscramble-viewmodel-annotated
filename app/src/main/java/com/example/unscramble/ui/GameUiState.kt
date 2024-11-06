/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.unscramble.ui

/**
 * Data class that represents the game UI state.
 * (A data class is intended to simply store data in an object)
 * Both the fields and a Constructor will be generated for this data class,
 * based on the parameters supplied. Note the default values for the fields.
 * Fields are public by default.
 * Note that 'val' type is used, so:
 * - no reassignment to these fields is allowed once they have been initialized
 * - we can get the value stored in a field  e.g.print( gameUiState.score )
 *
 */
data class GameUiState(
    val currentScrambledWord: String = "",
    val currentWordCount: Int = 1,
    val score: Int = 0,
    val isGuessedWordWrong: Boolean = false,
    val isGameOver: Boolean = false
)
