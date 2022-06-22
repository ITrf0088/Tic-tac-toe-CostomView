package org.rasulov.tictactoe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.rasulov.tictactoe.databinding.ActivityMainBinding
import org.rasulov.tictactoe.tictactoe.Cell
import org.rasulov.tictactoe.tictactoe.TicTacToe
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding
    private var isFirstPlayer  = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // setup UI
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Restore or init field and variables
        val ticTacToe = TicTacToe(5, 5)
        binding.ticTacToeView.gameField = ticTacToe

        // listening user actions
        binding.ticTacToeView.actionListener = { row, column, currentField ->

            val cell = currentField.getCell(row, column)
            if (cell == Cell.EMPTY) {
                // cell is empty, changing it to X or O
                if (isFirstPlayer) {
                    currentField.setCell(row, column, Cell.FIRST_PLAYER)
                } else {
                    currentField.setCell(row, column, Cell.SECOND_PLAYER)
                }
                isFirstPlayer = !isFirstPlayer
            }
        }

    }
}