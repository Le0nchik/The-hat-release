package com.example.the_hat

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.icu.text.DecimalFormat
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
import com.example.the_hat.backendClasses.Player
import com.example.the_hat.backendClasses.dataClasses.DataRound
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

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.main)
        Log.i("SwitchScreen", "To main")
    }


    @SuppressLint("UseSwitchCompatOrMaterialCode")
    fun goToMain(view: View) {
        var switch: Switch = findViewById<Switch>(R.id.game_out_allow)
        if (switch.isChecked) {
            setContentView(R.layout.main)
        }
    }

    fun goToApprove(view: View) {
        setContentView(R.layout.approve)
        Log.i("SwitchScreen", "To Aproov")
    }

    fun goToCreatePlayers(view: View) {
        setContentView(R.layout.create_players)
        var containerNames = findViewById<LinearLayout>(R.id.create_players_containerTexts)
        for (i in 0 until players.size) {
            addPlayer(view)
            var text: EditText =
                (containerNames.getChildAt(i) as LinearLayout).getChildAt(1) as EditText
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
            var text: EditText =
                (containerNames.getChildAt(i) as LinearLayout).getChildAt(1) as EditText
            players.add(Player(text.text.toString()))
        }
        setContentView(R.layout.game_settings)
        var teamButton = findViewById<CheckBox>(R.id.game_settings_teamMode)
        teamButton.isChecked = configs["teamMode"] == 1
        containerNames.removeAllViews()

        var time = findViewById<TextView>(R.id.game_settings_time)
        time.text = configs["time"].toString()


        var switchEasy = findViewById<Switch>(R.id.game_settings_easyDict)
        var switchMedium = findViewById<Switch>(R.id.game_settings_mediumDict)
        var switchHard = findViewById<Switch>(R.id.game_settings_hardDict)

        switchEasy.isChecked = configs["dictEasy"] == 1

        switchMedium.isChecked = configs["dictMedium"] == 1

        switchHard.isChecked = configs["dictHard"] == 1

        Log.i("SwitchScreen", "To Setting")
    }

    fun goToResults(view: View) {
        setContentView(R.layout.results)
        Log.i("SwitchScreen", "To Results")
        updateResultsWords(view)
    }

    fun goToGame(view: View) {
        setContentView(R.layout.game)
        updateGame(view)
    }

    fun goToHistory(view: View) {
        setContentView(R.layout.history)
        updateHistory(view)
    }

    fun goToHistoryX(view: View) {
        goToHistoryX(view)
        game.setBackRound()
    }

    fun goToReplay(view: View) {
        setContentView(R.layout.replay)
        updateReplay(view)
    }

    @SuppressLint("SetTextI18n")
    fun updateReplay(view: View) {
        var text = findViewById<TextView>(R.id.replay_who_by_who)
        text.text = game.cur!!.explainer.name + " -> " +game.cur!!.guesser.name
    }

    @SuppressLint("SetTextI18n")
    private fun updateHistory(view: View) {
        var layout: LinearLayout = findViewById<LinearLayout>(R.id.history_scroll_ll)

        for (i in 0 until game.data.size) {
            var tempLL1 = LinearLayout(this)
            var textView = TextView(this).apply {
                text =
                    players[game.data[game.data.size - i - 1].p1].name + " ->" + players[game.data[game.data.size - i - 1].p2].name
                gravity = Gravity.CENTER
            }
            tempLL1.addView(textView)


            var tempLL2 = LinearLayout(this).apply {
                gravity = Gravity.RIGHT
            }
            var button = Button(this).apply {
                text = "Переиграть"
            }
            button.setOnClickListener {
                game.replay(game.data.size - i - 1)
                goToReplay(view)
            }
            tempLL2.addView(button)

            var tempLL3 = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
            }

            for (j in 0 until game.data[game.data.size - i - 1].words.words.size) {
                var ll3 = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                }

                var word = TextView(this).apply {
                    text = game.data[game.data.size - i - 1].words.words[j].value
                }

                var check = CheckBox(this).apply {
                    id = 10000 * i + j
                    isChecked = game.data[game.data.size - i - 1].words.types[j] == DONE
                }

                check.setOnClickListener {
                    game.setWordType(
                        game.data.size - check.id / 10000 - 1,
                        check.id % 10000,
                        game.data[game.data.size - i - 1].words.types[j] == SKIP
                    )
                }

                ll3.addView(check)
                ll3.addView(word)

                tempLL3.addView(ll3)
            }


            layout.addView(tempLL1)
            layout.addView(tempLL2)
            layout.addView(tempLL3)
        }
    }


    fun updateResultsWords(view: View) {
        Log.d("Update", view.toString())
        val table: TableLayout = findViewById(R.id.results_all)
        table.removeAllViews()

        var row1: TableRow = TableRow(this)

        var text: TextView = TextView(this)
        text.text = ""
        row1.addView(text)

        for (i in 0 until players.size) {
            var text1: TextView = TextView(this)
            text1.text = players[i].name[0].toString()
            row1.addView(text1)
        }
        table.addView(row1)

        for (i in 0 until players.size) {
            var row2: TableRow = TableRow(this)
            var text1: TextView = TextView(this)
            text1.text = players[i].name[0].toString()
            row2.addView(text1)

            for (j in 0 until players.size) {
                var text2: TextView = TextView(this)
                if (i != j) {
                    text2.text = game.countOfWords(i, j).toString()
                }
                row2.addView(text2)
            }
            table.addView(row2)
        }
    }

    fun updateResultsTimes(view: View) {
        Log.d("Update", view.toString())
        val table: TableLayout = findViewById(R.id.results_all)
        table.removeAllViews()

        var row1: TableRow = TableRow(this)

        var text: TextView = TextView(this)
        text.text = ""
        row1.addView(text)

        for (i in 0 until players.size) {
            var text1: TextView = TextView(this)
            text1.text = players[i].name[0].toString()
            row1.addView(text1)
        }
        table.addView(row1)

        for (i in 0 until players.size) {
            var row2: TableRow = TableRow(this)
            var text1: TextView = TextView(this)
            text1.text = players[i].name[0].toString()
            row2.addView(text1)

            for (j in 0 until players.size) {
                var text2: TextView = TextView(this)
                if (i != j) {
                    if (game.countOfRounds(i, j) == 0) {
                        text2.text = "0"
                    } else {
                        text2.text = DecimalFormat("#0.00").format((game.countOfRounds(i, j) * configs["time"]!!.toFloat()) / game.countOfWords(i, j))
                    }
                }
                row2.addView(text2)
            }
            table.addView(row2)
        }
    }


    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private fun updateGame(view: View) {
        var text: TextView = findViewById<TextView>(R.id.game_whoByWho)
        text.text = game.playerNames
        var switch: Switch = findViewById<Switch>(R.id.game_out_allow)
        switch.isChecked = false
    }

    fun checkRes(view: View) {
        var switch = findViewById<Switch>(R.id.history_mode)

        if (switch.isChecked) {
            updateResultsTimes(view)
        } else {
            updateResultsWords(view)
        }
    }

    @SuppressLint("WrongViewCast")
    fun approving(view: View) {
        var ll = findViewById<ScrollView>(R.id.aproov_ll).getChildAt(0) as LinearLayout

        ll.removeAllViews()

        var arrayWords = game.cur!!.wordsInRound
        var arrayWordsType = game.cur!!.typeOfWordsInRound

        for (i in 0 until arrayWords.size) {
            var linearLayout = LinearLayout(this)
            linearLayout.addView(CheckBox(this).apply { isChecked = arrayWordsType[i] == DONE })
            linearLayout.addView(TextView(this).apply { text = arrayWords[i].value })
            ll.addView(linearLayout)
        }
    }


    fun approve(view: View) {
        var ll = findViewById<ScrollView>(R.id.aproov_ll).getChildAt(0) as LinearLayout
        var words = game.cur

        for (i in 0 until words!!.wordsInRound.size) {
            var linearLayout: LinearLayout = ll.getChildAt(i) as LinearLayout
            var v: CheckBox = linearLayout.getChildAt(0) as CheckBox
            if (v.isChecked) {
                words.typeOfWordsInRound[i] = DONE
                game.cur!!.typeOfWordsInRound[i] = DONE
            } else {
                words.typeOfWordsInRound[i] = SKIP
                game.cur!!.typeOfWordsInRound[i] = SKIP

            }
        }
        game.nextRound(words.result())
        game.nextWord()
        goToGame(view)
    }

    @SuppressLint("ResourceAsColor")
    fun addPlayer(view: View) {
        var containerNames = findViewById<LinearLayout>(R.id.create_players_containerTexts)

        if (containerNames.childCount > 12) {
            Toast.makeText(this, "Максимум 13 игроков", Toast.LENGTH_SHORT).show()
            return
        }

        var horizontalL = LinearLayout(this).apply {
            id = 900000 + containerNames.childCount
            orientation = LinearLayout.HORIZONTAL
        }

        val button1 = Button(this).apply {
            id = 900000 + containerNames.childCount
//            setPadding(0, 15, 0, 15)
            height = 125
            text = "X"
        }

        val button2 = Button(this).apply {
            id = 900000 + containerNames.childCount
            text = "⇵"
//            setPadding(0, 15, 0, 15)
            height = 125
        }

        val textView = EditText(this).apply {
//            setPadding(10, 0, 10, 0)
            height = 125
            minWidth =
                (resources.displayMetrics.widthPixels / resources.displayMetrics.density).toInt() * 3 / 2
            gravity = Gravity.CENTER_HORIZONTAL
            isSingleLine = true
            maxWidth =
                (resources.displayMetrics.widthPixels / resources.displayMetrics.density).toInt() * 3 / 2
        }

        button1.setOnClickListener {
            Log.e("pipiska", button1.id.toString())
            containerNames.removeViewAt(button1.id - 900000)
            for (i in 0 until containerNames.childCount) {
                var ll: LinearLayout = containerNames.getChildAt(i) as LinearLayout
                ll.id = 900000 + i
                ll.getChildAt(0).id = 900000 + i
                ll.getChildAt(2).id = 900000 + i
            }
        }

        button2.setBackgroundColor(Color.parseColor("#D5D6D6"))

        button2.setOnClickListener {
            if (configs["swapPlayers"] != -1) {
                for (i in 0 until containerNames.childCount) {
                    var ll: LinearLayout = containerNames.getChildAt(i) as LinearLayout
                    ll.getChildAt(0).setBackgroundColor(Color.parseColor("#D5D6D6"))
                }
                if (configs["swapPlayers"] != button2.id) {
                    val number: Int = configs["swapPlayers"]!!.toInt()
                    var ll1: LinearLayout =
                        containerNames.getChildAt(button2.id - 900000) as LinearLayout
                    var ll2: LinearLayout = containerNames.getChildAt(number) as LinearLayout
                    var text1: EditText = ll1.getChildAt(1) as EditText
                    var text2: EditText = ll2.getChildAt(1) as EditText
                    var swapText = text2.text

                    text2.text = text1.text
                    text1.text = swapText
                }
                configs["swapPlayers"] = -1
            } else {
                configs["swapPlayers"] = button2.id - 900000
                button2.setBackgroundColor(Color.parseColor("#ffdd00"))
            }
        }

        horizontalL.addView(button2)
        horizontalL.addView(textView)
        horizontalL.addView(button1)
        containerNames.addView(horizontalL)
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
            val nameText: EditText =
                (namesLayout.getChildAt(i) as LinearLayout).getChildAt(1) as EditText
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
            val nameText: EditText =
                (namesLayout.getChildAt(i) as LinearLayout).getChildAt(1) as EditText
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
                goToApprove(view)
                approving(view)
//                game.nextRound()
//                game.nextWord()
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
            button.text = "Пропустить"
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
