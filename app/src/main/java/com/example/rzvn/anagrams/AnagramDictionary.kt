package com.example.rzvn.anagrams

import java.io.BufferedReader
import java.io.Reader
import java.util.Random
import kotlin.coroutines.experimental.buildSequence

class AnagramDictionary(val reader: Reader) {

    companion object {
        val MIN_NUM_ANAGRAMS = 5
        val DEFAULT_WORD_LENGTH = 3
        val MAX_WORD_LENGTH = 7
    }

    private val random = Random()
    private val wordsList = mutableListOf<String>()
    private val wordsSet = HashSet<String>()
    private val lettersToWord = HashMap<String, ArrayList<String>>()

    init {
        val bufferedReader = BufferedReader(reader)
        bufferedReader.useLines { lines -> lines.forEach {
            val word = it.trim()
            val sortedWord = sortLetters(word)
            val anagramGroup = lettersToWord.getOrPut(sortedWord) { ArrayList<String>() }
            anagramGroup.add(word)
            wordsList.add(word)
        }}
    }


    fun isGoodWord(word: String, currentWord: String): Boolean {
        return wordsSet.contains(word) && (currentWord.length != word.length && !word.contains(Regex(currentWord)))
    }

    fun pickGoodStartingWord():String {
        val randomIdx = rand(0, wordsList.size)
        for (index in wrappingSequence(randomIdx, wordsList.size)) {
            val word = wordsList[index]
            val sortedWord = sortLetters(word)
            val anagramGroup = lettersToWord[sortedWord]
            if (anagramGroup.size >= MIN_NUM_ANAGRAMS) {
                return word
            }
        }
        return wordsList[randomIdx]
    }

    fun getAnagrams(word: String): List<String> {
        return wordsList.filter {
            val sorted = sortLetters(it)
            when {
                word.length != sorted.length -> false
                else -> word.equals(sorted)
            }
        }
    }

    fun getAnagramsWithOneMoreLetter(word: String): List<String> {
        val result = mutableListOf<String>()
        val anagrams = lettersToWord[word]!!
        for (letter in 'a'..'z') {
            if ("$word$letter" in anagrams) {
                result.add("$word$letter")
            }
        }
        return result
    }

    private fun sortLetters(word: String):String {
        val letters = listOf<String>(word)
        return letters.sorted().joinToString("")
    }

    private fun rand(from: Int, to: Int) : Int {
        return random.nextInt(to - from) + from
    }

    private fun wrappingSequence(startIndex: Int, size: Int) = buildSequence {
        for (index in startIndex..(size + (size - startIndex))) {
            yield(index % size)
        }
    }
}