package com.example.the_hat.backendClasses

import com.example.the_hat.backendClasses.dicts.Word

class Player(val name: String) {
    val explained: MutableList<Word> = ArrayList()
    val guessed: MutableList<Word> = ArrayList()

    fun explain(word: Word) {
        explained.add(word)
    }

    fun eraseExplain() {
        explained.removeAt(explained.lastIndex)
    }

    fun guess(word: Word) {
        guessed.add(word)
    }

    fun eraseGuess() {
        guessed.removeAt(guessed.lastIndex)
    }

    override fun toString(): String {
        return "Player{" +
                "name='" + name + '\'' +
                ", explained=" + explained +
                ", guessed=" + guessed +
                '}'
    }
}
