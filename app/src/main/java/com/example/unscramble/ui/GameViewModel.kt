/*

ViewModel for the Unscramble Game

Note
A ViewModel is scoped to the Activity that creates it.
A ViewModel survives a Configuration Change (Portrait->Landscape change).
All state should be kept in ViewModel.
State should be private, and changed only by code in the ViewModel class.
Access to state should dbe exposed through State / StateFlow properties that
are read-only.
 */

package com.example.unscramble.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.unscramble.data.MAX_NO_OF_WORDS
import com.example.unscramble.data.SCORE_INCREASE
import com.example.unscramble.data.allWords
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel containing the app data and methods to process the data
 */
class GameViewModel : ViewModel() {  // Create our own ViewModel by inheriting from ViewModel

    private val _uiState = MutableStateFlow( GameUiState() )
    // Construct a new GameUiState object (which will be constructed with default values)
    // This object holds all the various state variables in one object.
    // Wrap the object as a MutableStateFlow type:
    // - Mutable => can be updated
    // - MutableState => makes it an Observable value that Compose will observe and react to when updated
    // - Flow - is an object that can emit values, that can be captured (collected) by the observer.
    //
    // Game UI state:   _uiState
    // (the underscore indicates by convention that it is a member variable (an instance field)
    // used with ViewModel, make updates to _uiState

    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()
    // Wrap the _uiState as a Read-Only StateFlow
    // This is exposes as a public field, but its value can not be changed as it is read-only state flow.
    //
    // The UI depends on the uiState, but will not be able to modify it as StateFlow is read-only.
    // Changes are made to the underlying "_uiState"

    var userGuess by mutableStateOf("")  // MutableState makes this an observable (by Compose) value.
        private set                 // can only set() the userGuess within this class (as set() is made private)
                                    // get() remains public, so is accessible

    // Set of words used in the game
    private var usedWords: MutableSet<String> = mutableSetOf()

    private lateinit var currentWord: String

    init {
        resetGame()
    }

    /*
     * Re-initializes the game data to restart the game.
     */
    fun resetGame() {
        usedWords.clear()   // clear the Set of used words.

        _uiState.value = GameUiState(currentScrambledWord = pickRandomWordAndShuffle())
        // above we replace the existing game state by
        // constructing a new GameUiState object, setting the currentScrambledWord parameter,
        // but allowing all other parameters to take default values.
        // The value in _uiState is the underlying data value for uiState, which the
        // composables depends on. So, when this changes, any composable depending on
        // this are ultimately recomposed.
    }

    /*
     * Update the user's guess
     */
    fun updateUserGuess(guessedWord: String){
        userGuess = guessedWord
    }

    /*
     * Checks if the user's guess is correct.
     * Increases the score accordingly.
     */
    fun checkUserGuess() {
        if (userGuess.equals(currentWord, ignoreCase = true)) {
            // User's guess is correct, increase the score
            // and call updateGameState() to prepare the game for next round
            val updatedScore = _uiState.value.score.plus(SCORE_INCREASE)
            updateGameState(updatedScore)
        } else {
            // User's guess is wrong, change state of isGuessedWordWrong to true
            // - which will cause a recomposition
            // Lambda, takes current state and updates with new object
            _uiState.update { currentState ->
                currentState.copy(isGuessedWordWrong = true)
                // update the _uiState by copying the existing object,
                // changing the field, and replacing the original object
            }
        }
        // Reset user guess with blank
        updateUserGuess("")
    }

    /*
     * Skip to next word
     */
    fun skipWord() {
        updateGameState(_uiState.value.score)   // score remains same as it was
        // Reset user guess
        updateUserGuess("")
    }

    /*
     * Picks a new currentWord and currentScrambledWord and updates UiState according to
     * current game state.
     */
    private fun updateGameState(updatedScore: Int) {
        if (usedWords.size == MAX_NO_OF_WORDS){
            //Last round in the game,
            // // update the uiState, including update isGameOver to true, don't pick a new word
            _uiState.update { currentState ->
                currentState.copy(    // copy an object but alter some properties.
                    isGuessedWordWrong = false,
                    score = updatedScore,
                    isGameOver = true
                )
            }
        } else{
            // Normal round in the game.
            // update the _uiState value by taking a copy of the current state,
            // changing some of the fields/properties, and using it as new state
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = false, // the guess was correct
                    currentScrambledWord = pickRandomWordAndShuffle(),
                    currentWordCount = currentState.currentWordCount.inc(), // increment
                    score = updatedScore
                )
            }
        }
    }

    private fun shuffleCurrentWord(word: String): String {
        val tempWord = word.toCharArray()
        // Scramble the word
        tempWord.shuffle()
        while (String(tempWord) == word) {  // if the shuffle happened to give us the same word back,
            tempWord.shuffle()              // then shuffle again until until word is shuffled!
        }
        return String(tempWord)
    }

    private fun pickRandomWordAndShuffle(): String {
        // Continue picking up a new random word until you get one that hasn't been used before
        currentWord = allWords.random()
        return if (usedWords.contains(currentWord)) {
            pickRandomWordAndShuffle()              // DL recursive call !!
        } else {
            usedWords.add(currentWord)
            shuffleCurrentWord(currentWord)
        }
    }
}
