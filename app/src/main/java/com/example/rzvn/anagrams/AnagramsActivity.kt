package com.example.rzvn.anagrams

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.Html
import android.text.Html.FROM_HTML_MODE_COMPACT
import android.text.InputType
import android.text.TextUtils
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import com.example.rzvn.anagrams.R.layout.content_anagrams

import kotlinx.android.synthetic.main.activity_anagrams.view.*
import kotlinx.android.synthetic.main.activity_anagrams.*
import kotlinx.android.synthetic.main.content_anagrams.view.*
//import kotlinx.android.synthetic.main.content_anagrams.*
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class AnagramsActivity : AppCompatActivity() {

    companion object {
        val START_MESSAGE = "Find as many words as possible that can be formed by adding one letter to <big>%s</big> (but that do not contain the substring %s)."
    }

    private var currentWord = ""
    private lateinit var dictionary: AnagramDictionary
    private lateinit var anagrams: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anagrams)
        setSupportActionBar(toolbar)

        val inputStream = assets.open("words.txt")
        try {
            dictionary = AnagramDictionary(InputStreamReader(inputStream))
        } catch(e: IOException) {
            val toast: Toast = Toast.makeText(this, "Could not load dictionary", Toast.LENGTH_LONG)
            toast.show()
        }

        editText.setRawInputType(InputType.TYPE_CLASS_TEXT)
        editText.setImeOptions(EditorInfo.IME_ACTION_GO)
        editText.setOnEditorActionListener { v, actionId, event ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_GO || (
                    actionId == EditorInfo.IME_NULL && event?.getAction() == KeyEvent.ACTION_DOWN
                    )) {
                processWord(editText)
                handled = true
            }
            handled
        }

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    private fun processWord(editText: EditText) {
        var word = editText.text.toString().trim().toLowerCase()
        if (word.length == 0) {
            return
        }
        var color = "#cc0029"
        if (dictionary.isGoodWord(word, currentWord) && anagrams.contains(word)) {
            anagrams = anagrams.filter { !word.equals(it) }
            color = "#00aa29"
        } else {
            word = "X ${word}"
        }
        resultView.append(Html.fromHtml(String.format("<font color=%s>%s</font><BR>", color, word), FROM_HTML_MODE_COMPACT))
        editText.text.clear()
        fab.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_anagrams, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun defaultAction(view: View): Boolean {
        if (currentWord.equals("")) {
            currentWord = dictionary.pickGoodStartingWord()
            anagrams = dictionary.getAnagramsWithOneMoreLetter(currentWord)
            gameStatusView.text = Html.fromHtml(String.format(START_MESSAGE, currentWord.toUpperCase(), currentWord), FROM_HTML_MODE_COMPACT)
            fab.setImageResource(android.R.drawable.ic_menu_help)
            fab.hide()
            resultView.text = ""
            editText.text.clear()
            editText.isEnabled = true
            editText.requestFocus()
            val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        } else {
            editText.setText(currentWord)
            editText.isEnabled = false
            fab.setImageResource(android.R.drawable.ic_media_play)
            currentWord = ""
            resultView.append(TextUtils.join("\n", anagrams))
            gameStatusView.append(" Hit 'Play' to start again")
        }
        return true
    }
}
