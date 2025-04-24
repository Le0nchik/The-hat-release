package com.example.the_hat

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Switch
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import com.example.the_hat.backendClasses.Player
import com.example.the_hat.backendClasses.dicts.Dict
import com.example.the_hat.backendClasses.dicts.TypeWord.DONE
import com.example.the_hat.backendClasses.dicts.TypeWord.SKIP
import com.example.the_hat.backendClasses.dicts.Word
import com.example.the_hat.backendClasses.games.Game
import com.example.the_hat.backendClasses.games.GameByPair
import com.example.the_hat.backendClasses.games.GameEveryoneWithEveryone

class MainActivity : ComponentActivity() {
    var players: MutableList<Player> = ArrayList()
    var configs: MutableMap<String, Int> = mutableMapOf(
        "teamMode" to 0,
        "swapPlayers" to -1,
        "time" to 30,
        "dictEasy" to 0,
        "dictMedium" to 0,
        "dictHard" to 0
    )
    lateinit var dict: Dict<Word>
    lateinit var game: Game


    fun openDict(file: String): Dict<Word> {
        return Dict(
            assets.open(file).bufferedReader().readLines().map { Word(it) }
                .toMutableList())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.main)
        Log.i("SwitchScreen", "To main")
    }


    fun goToMain(view: View) {
        setContentView(R.layout.main)
    }

    fun goToAproov(view: View) {
        setContentView(R.layout.aproov)
        Log.i("SwitchScreen", "To Aproov")
    }

    fun goToCreatePlayers(view: View) {
        setContentView(R.layout.create_players)
        var containerNames = findViewById<LinearLayout>(R.id.create_players_containerTexts)
        for (i in 0 until players.size) {
            addPlayer(view)
            var text: EditText = containerNames.getChildAt(i) as EditText
            var name: Editable = Editable.Factory.getInstance().newEditable(players[i].name)
            text.text = name
        }
        Log.i("SwitchScreen", "To CreatePlayers")
    }

    @SuppressLint("MissingInflatedId")
    fun goToSetting(view: View) {
        var containerNames = findViewById<LinearLayout>(R.id.create_players_containerTexts)
        players = ArrayList()
        for (i in 0 until containerNames.childCount) {
            var text: EditText = containerNames.getChildAt(i) as EditText
            players.add(Player(text.text.toString()))
        }
        setContentView(R.layout.game_settings)
        var teamButton = findViewById<CheckBox>(R.id.game_settings_teamMode)
        teamButton.isChecked = configs["teamMode"] == 1
        containerNames.removeAllViews()

        var time = findViewById<TextView>(R.id.game_settings_time)
        time.text = configs["time"].toString()

        Log.i("SwitchScreen", "To Setting")
    }

    fun goToResults(view: View) {
        setContentView(R.layout.results)
        Log.i("SwitchScreen", "To Results")
        updateResults(view)
    }

    fun goToGame(view: View) {
        setContentView(R.layout.game)
        updateGame(view)
    }

    fun goToHistory(view: View) {
        setContentView(R.layout.history)
        updateHistory(view)
    }

    private fun updateHistory(view: View) {
        var texts = findViewById<LinearLayout>(R.id.history_texts)
        var buttons = findViewById<LinearLayout>(R.id.history_buttons)
        var boxes = findViewById<LinearLayout>(R.id.history_checkBoxes)
    }

    fun updateResults(view: View) {
        Log.d("pipiska", view.toString())
        val table: TableLayout = findViewById(R.id.results_all)

        var row1: TableRow = TableRow(this)

        var text: TextView = TextView(this)
        text.text = ""
        row1.addView(text)

        for (i in 0 until players.size) {
            var text1: TextView = TextView(this)
            text1.text = players[i].name
            row1.addView(text1)
        }
        table.addView(row1)

        for (i in 0 until players.size) {
            var row2: TableRow = TableRow(this)

            var text1: TextView = TextView(this)
            text1.text = players[i].name
            row2.addView(text1)
            for (j in 0 until players.size) {
                var text2: TextView = TextView(this)
                text2.text = game.countOfWords(i, j)
                row2.addView(text2)
            }
            table.addView(row2)
        }
    }


    private fun updateGame(view: View) {
        var text: TextView = findViewById<TextView>(R.id.game_whoByWho)
        text.text = game.playerNames
    }

    private fun updateRound(view: View) {

    }


    @SuppressLint("WrongViewCast")
    fun aprooving(view: View) {
        var ll = findViewById<ScrollView>(R.id.aproov_ll).getChildAt(0) as LinearLayout

        ll.removeAllViews()

        var arrayWords = game.data.last().words.words
        var arrayWordsType = game.data.last().words.types

        for (i in 0 until arrayWords.size - 1) {
            var linearLayout = LinearLayout(this).apply {

            }

            linearLayout.addView(CheckBox(this).apply { isChecked = arrayWordsType[i] == DONE })
            linearLayout.addView(TextView(this).apply { text = arrayWords[i].value })
            ll.addView(linearLayout)
        }
    }


    fun aproov(view: View) {
        var ll = findViewById<ScrollView>(R.id.aproov_ll).getChildAt(0) as LinearLayout

        var words = game.data.last().words

        for (i in 0 until words.words.size - 1) {
            var linearLayout: LinearLayout = ll.getChildAt(i) as LinearLayout
            var v: CheckBox = linearLayout.getChildAt(0) as CheckBox
            if (v.isChecked) {
                words.types[i] = DONE
            } else {
                words.types[i] = SKIP
            }
        }
        game.replayed(game.data.size - 1, words)
        goToGame(view)
    }

    @SuppressLint("ResourceAsColor")
    fun addPlayer(view: View) {
        var containerNames = findViewById<LinearLayout>(R.id.create_players_containerTexts)
        var containerButtons1 = findViewById<LinearLayout>(R.id.create_players_containerButtons1)
        var containerButtons2 = findViewById<LinearLayout>(R.id.create_players_containerButtons2)
        if (containerNames.childCount > 12) {
            Toast.makeText(this, "Максимум 13 игроков", Toast.LENGTH_SHORT).show()
            return
        }
        val textView = EditText(this).apply {
            id = 900000 + containerNames.childCount
            setPadding(30, 10, 10, 10)
            height = 125
            gravity = Gravity.CENTER_HORIZONTAL
        }

        val button1 = Button(this).apply {
            id = 900000 + containerButtons1.childCount
            setPadding(0, 10, 10, 10)
            height = 125
        }

        val button2 = Button(this).apply {
            id = 900000 + containerButtons2.childCount
            text = "⇵"
            setPadding(10, 10, 10, 0)
            height = 125
        }


        button1.setBackgroundResource(android.R.drawable.ic_delete)
        button1.setOnClickListener {
            containerNames.removeViewAt(button1.id - 900000)
            containerButtons1.removeViewAt(button1.id - 900000)
            containerButtons2.removeViewAt(button1.id - 900000)
            for (i in 0 until containerNames.childCount) {
                containerNames.getChildAt(i).id = 900000 + i
                containerButtons1.getChildAt(i).id = 900000 + i
                containerButtons2.getChildAt(i).id = 900000 + i
            }
        }

        button2.setBackgroundColor(Color.parseColor("#D5D6D6"))
        button2.setOnClickListener {
            if (configs["swapPlayers"] != -1) {
                for (i in 0 until containerButtons2.childCount) {
                    containerButtons2.getChildAt(i).setBackgroundColor(Color.parseColor("#D5D6D6"))
                }
                if (configs["swapPlayers"] != button2.id) {
                    val number: Int = configs["swapPlayers"]!!.toInt()
                    val text1: EditText = containerNames.getChildAt(button2.id - 900000) as EditText
                    val text2: EditText = containerNames.getChildAt(number) as EditText
                    val text: Editable = text2.text
                    text2.text = text1.text
                    text1.text = text
                }
                configs["swapPlayers"] = -1
            } else {
                configs["swapPlayers"] = button2.id - 900000
                button2.setBackgroundColor(ContextCompat.getColor(this, R.color.yellow))
            }
        }
        containerNames.addView(textView)
        containerButtons1.addView(button1)
        containerButtons2.addView(button2)
    }


    fun timeMinus(view: View) {
        if (configs["time"] != 5) {
            configs["time"] = (configs["time"])!!.minus(5)
            var text = findViewById<TextView>(R.id.game_settings_time)
            text.text = configs["time"].toString()
        }
    }

    fun timePlus(view: View) {
        configs["time"] = (configs["time"])!!.plus(5)
        var text = findViewById<TextView>(R.id.game_settings_time)
        text.text = configs["time"].toString()
    }

    fun switchTeamMode(view: View) {
        if (configs["teamMode"] == 1) {
            configs["teamMode"] = 0
        } else {
            configs["teamMode"] = 1
        }
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    fun dictEasyAdd(view: View) {
        var switchEasy = findViewById<Switch>(R.id.game_settings_easyDict)
        var switchMedium = findViewById<Switch>(R.id.game_settings_mediumDict)
        var switchHard = findViewById<Switch>(R.id.game_settings_hardDict)

        if (switchEasy.isChecked) {
            switchMedium.isChecked = false
            switchHard.isChecked = false

            configs["dictEasy"] = 1
            configs["dictMedium"] = 0
            configs["dictHard"] = 0
        }
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    fun dictMediumAdd(view: View) {
        var switchEasy = findViewById<Switch>(R.id.game_settings_easyDict)
        var switchMedium = findViewById<Switch>(R.id.game_settings_mediumDict)
        var switchHard = findViewById<Switch>(R.id.game_settings_hardDict)

        if (switchMedium.isChecked) {
            switchEasy.isChecked = false
            switchHard.isChecked = false

            configs["dictEasy"] = 0
            configs["dictMedium"] = 1
            configs["dictHard"] = 0
        }


    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    fun dictHardAdd(view: View) {
        var switchEasy = findViewById<Switch>(R.id.game_settings_easyDict)
        var switchMedium = findViewById<Switch>(R.id.game_settings_mediumDict)
        var switchHard = findViewById<Switch>(R.id.game_settings_hardDict)

        if (switchHard.isChecked) {
            switchEasy.isChecked = false
            switchMedium.isChecked = false

            configs["dictEasy"] = 0
            configs["dictMedium"] = 0
            configs["dictHard"] = 1
        }
    }

    @SuppressLint("MissingInflatedId")
    fun finishCreating(view: View) {
        val namesLayout: LinearLayout = findViewById(R.id.create_players_containerTexts)
        if (namesLayout.childCount < 2) {
            Toast.makeText(this, "Минимум игроков 2", Toast.LENGTH_SHORT).show()
            return
        }
        if (configs["teamMode"] == 1 && namesLayout.childCount % 2 == 1) {
            Toast.makeText(
                this, "В командном режиме нужно чётное число игроков", Toast.LENGTH_SHORT
            ).show()
            return
        }
        for (i in 0 until namesLayout.childCount) {
            val nameText: EditText = namesLayout.getChildAt(i) as EditText
            var name: String = nameText.text.toString()

            if (name.isEmpty()) {
                Toast.makeText(this, "У одного из игроков пустое имя", Toast.LENGTH_SHORT).show()
                return
            }
        }

        if (configs["dictEasy"] == 1) {
            dict = openDict("dict_easy_sorted.txt")
        } else if (configs["dictMedium"] == 1) {
            dict = openDict("dict_mid_sorted.txt")
        } else if (configs["dictHard"] == 1) {
            dict = openDict("dict_hard_sorted.txt")
        } else {
            Toast.makeText(this, "Выберите словарь", Toast.LENGTH_SHORT).show()
            return
        }
        players = ArrayList()
        for (i in 0 until namesLayout.childCount) {
            val nameText: EditText = namesLayout.getChildAt(i) as EditText
            var name: String = nameText.text.toString()
            players.add(Player(name))
        }



        if (configs["teamMode"] == 1) {
            game = GameByPair(players, dict)
        } else {
            game = GameEveryoneWithEveryone(players, dict)
        }

        game.nextRound()
        game.nextWord()

        goToGame(view)
    }

    fun startRound(view: View) {
        setContentView(R.layout.round)
        val textView = findViewById<TextView>(R.id.round_timerTextView)
        var text2: TextView = findViewById<TextView>(R.id.round_word)
        text2.text = game.currentWord.value
        object : CountDownTimer((1000 * (configs["time"]!!.toInt())).toLong(), 100) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                textView.text =
                    "Осталось: ${millisUntilFinished / 1000}.${millisUntilFinished % 1000 / 100}"
            }

            override fun onFinish() {
                textView.text = "Время вышло!"
                game.nextRound()
                game.nextWord()
                goToAproov(view)
                aprooving(view)
            }
        }.start()
    }

    fun doneWord(view: View) {
        game.doneWord()
        getNewWord(view)
    }

    fun doneSkip(view: View) {
        if (game.hasSkipWord()) {
            game.doneSkipWord()
            var text: TextView = findViewById<TextView>(R.id.round_skippedWord)
            text.text = ""
            var button = findViewById<Button>(R.id.round_buttonDoneSkip)
            button.text = "Пропустить слово"
        } else {
            game.skipWord()
            getNewWord(view)
            var text: TextView = findViewById<TextView>(R.id.round_skippedWord)
            text.text = game.skipWord
            var button = findViewById<Button>(R.id.round_buttonDoneSkip)
            button.text = "Угадано пропущенное"
        }
    }

    fun failWord(view: View) {
        game.failWord()
        getNewWord(view)
    }

    fun failSkip(view: View) {
        if (game.hasSkipWord()) {
            game.failSkipWord()
            var text: TextView = findViewById<TextView>(R.id.round_skippedWord)
            text.text = ""
            var button = findViewById<Button>(R.id.round_buttonDoneSkip)
            button.text = "Пропустить"
        }
    }

    fun getNewWord(view: View) {
        var word: Word = game.nextWord()
        var text: TextView = findViewById<TextView>(R.id.round_word)
        text.text = word.value
    }
}
