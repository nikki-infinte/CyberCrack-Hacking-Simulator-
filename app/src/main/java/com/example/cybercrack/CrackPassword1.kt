package com.example.cybercrack

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cybercrack.R.*
import com.google.android.material.button.MaterialButton
import java.util.Calendar
import kotlin.random.Random

class CrackPassword1 : AppCompatActivity() {

    private lateinit var tvScenario: TextView
    private lateinit var tvClue: TextView
    private lateinit var etGuessPassword: EditText
    private lateinit var btnSubmit: MaterialButton
    private lateinit var tvFeedback: TextView
    private lateinit var tvAttempts: TextView
    private lateinit var btnBruteForce: MaterialButton
    private lateinit var btnDictionary: MaterialButton
    private lateinit var btnClue: MaterialButton
    private lateinit var tvTerminal: TextView
    private lateinit var scrollView: ScrollView

    private var currentPassword: String = ""
    private var attemptsLeft = 5
    private var cluesRevealed = 0
    private var bruteForceUsed = false
    private var dictionaryUsed = false


    private val scenarios: List<Pair<String, String>> by lazy {
        val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH).toString()
        listOf(
            Pair(getString(R.string.scenario_1_desc), getString(R.string.scenario_1_answer)),
            Pair(getString(R.string.scenario_2_desc), getString(R.string.scenario_2_answer)),
            Pair(getString(R.string.scenario_3_desc), getString(R.string.scenario_3_answer)),
            Pair(getString(R.string.scenario_4_desc), getString(R.string.scenario_4_answer)),
            Pair(getString(R.string.scenario_5_desc), "Admin$currentDay"),
            Pair(getString(R.string.scenario_6_desc), getString(R.string.scenario_6_answer)),
            Pair(getString(R.string.scenario_7_desc), getString(R.string.scenario_7_answer)),
            Pair(getString(R.string.scenario_8_desc), getString(R.string.scenario_8_answer)),
            Pair(getString(R.string.scenario_9_desc), getString(R.string.scenario_9_answer)),
            Pair(getString(R.string.scenario_10_desc), getString(R.string.scenario_10_answer))
        )
    }

    private fun generateDynamicClues(password: String): List<String> {
        return listOf(
            getString(string.dynamic_clue_length, password.length),
            if (password.any { it.isDigit() }) getString(string.dynamic_clue_contains_numbers)
            else getString(string.dynamic_clue_letters_only),
            if (password.any { it.isUpperCase() }) getString(string.dynamic_clue_has_capitals)
            else getString(string.dynamic_clue_all_lowercase)
        )
    }

    private fun updateAttemptsText() {
        tvAttempts.text = getString(R.string.attempts_left, attemptsLeft)
    }

    private val dictionary = listOf(
        "password", "123456", "qwerty", "admin", "welcome",
        "monkey", "dragon", "sunshine", "master", "hello"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_crack_password1)

        initViews()
        setupWindowInsets()
        loadNewScenario()

        btnSubmit.setOnClickListener { checkPassword() }
        btnClue.setOnClickListener { revealClue() }
        btnBruteForce.setOnClickListener { launchBruteForce() }
        btnDictionary.setOnClickListener { launchDictionaryAttack() }
    }

    private fun initViews() {
        tvScenario = findViewById(id.tvScenario)
        tvClue = findViewById(id.tvClue)
        etGuessPassword = findViewById(id.etGuessPassword)
        btnSubmit = findViewById(id.btnSubmit)
        tvFeedback = findViewById(id.tvFeedback)
        tvAttempts = findViewById(id.tvAttempts)
        btnBruteForce = findViewById(id.btnBruteForce)
        btnDictionary = findViewById(id.btnDictionaryAttack)
        btnClue = findViewById(id.btnClue)
        tvTerminal = findViewById(id.tvTerminal)
        scrollView = findViewById(id.scrollView)
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun revealClue() {
        val clueParts = when (currentPassword) {
            getString(string.scenario_1_answer) -> listOf(
                getString(string.clue_1_1),
                getString(string.clue_1_2),
                getString(string.clue_1_3)
            )
            getString(string.scenario_2_answer) -> listOf(
                getString(string.clue_2_1),
                getString(string.clue_2_2),
                getString(string.clue_2_3)
            )
            // Add clues for other scenarios
            else -> generateDynamicClues(currentPassword)
        }

        if (cluesRevealed < clueParts.size) {
            tvClue.text = getString(string.clue_prefix, clueParts[cluesRevealed])
            cluesRevealed++
            addTerminalMessage(getString(string.clue_revealed))
        } else {
            tvClue.text = getString(string.no_more_clues)
            addTerminalMessage(getString(string.clue_limit_reached))
        }
    }



    private fun launchBruteForce() {
        if (!bruteForceUsed) {
            bruteForceUsed = true
            btnBruteForce.isEnabled = false
            addTerminalMessage(getString(string.bruteforce_started))

            val progressText = StringBuilder()
            currentPassword.forEachIndexed { index, c ->
                Handler(Looper.getMainLooper()).postDelayed({
                    progressText.append(c)
                    etGuessPassword.setText(progressText.toString())

                    if (index == currentPassword.lastIndex) {
                        addTerminalMessage(getString(string.bruteforce_complete))
                        tvFeedback.text = getString(string.bruteforce_data_collected)
                    }
                }, 300L * (index + 1))
            }
        }
    }

    private fun launchDictionaryAttack() {
        if (!dictionaryUsed) {
            dictionaryUsed = true
            btnDictionary.isEnabled = false
            addTerminalMessage(getString(string.dictionary_attack_started))

            val suggestions = dictionary.filter { word ->
                currentPassword.contains(word, ignoreCase = true)
            }.take(3)

            val message = if (suggestions.isNotEmpty()) {
                getString(string.dictionary_suggestions, suggestions.joinToString())
            } else {
                getString(string.no_dictionary_matches)
            }

            tvFeedback.text = message
            addTerminalMessage(message)
        }
    }

    private fun loadNewScenario() {
        val randomIndex = Random.nextInt(scenarios.size)
        val randomScenario = scenarios[randomIndex]
        tvScenario.text = randomScenario.first
        currentPassword = randomScenario.second
        attemptsLeft = 5
        cluesRevealed = 0
        bruteForceUsed = false
        dictionaryUsed = false

        tvAttempts.text = getString(R.string.attempts_left, attemptsLeft)
        tvFeedback.text = getString(string.result_placeholder)
        etGuessPassword.text.clear()

        btnBruteForce.isEnabled = true
        btnDictionary.isEnabled = true
        btnSubmit.isEnabled = true
        etGuessPassword.isEnabled = true

        addTerminalMessage(getString(string.new_target_acquired))
    }

    private fun checkPassword() {
        if (attemptsLeft > 0) {
            val guessedPassword = etGuessPassword.text.toString().trim()

            if (guessedPassword.equals(currentPassword, ignoreCase = true)) {
                tvFeedback.text = getString(string.access_granted)
                tvFeedback.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_light))
                btnSubmit.isEnabled = false
                addTerminalMessage(getString(string.password_cracked))
            } else {
                attemptsLeft--
                tvFeedback.text = getString(string.intrusion_detected)
                tvFeedback.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light))
                tvAttempts.text = getString(R.string.attempts_left, attemptsLeft)
                addTerminalMessage(getString(string.failed_attempt, attemptsLeft))

                if (attemptsLeft == 0) {
                    tvFeedback.text = getString(string.system_locked)
                    btnSubmit.isEnabled = false
                    etGuessPassword.isEnabled = false
                    Handler(Looper.getMainLooper()).postDelayed({
                        loadNewScenario()
                    }, 2000)
                }
            }
        }
    }

    private fun addTerminalMessage(message: String) {
        tvTerminal.text = "${tvTerminal.text}\n>_ $message"
        tvTerminal.post {
            scrollView.fullScroll(View.FOCUS_DOWN)
        }
    }
}