package org.rasulov.tictactoe.tictactoe

enum class Cell {
    FIRST_PLAYER, SECOND_PLAYER, EMPTY
}
typealias OnFieldChangeListener = (TicTacToe) -> Unit

class TicTacToe( val rows: Int,  val columns: Int) {

    private val field = Array(rows) { Array(columns) { Cell.EMPTY } }

    private val listeners = mutableSetOf<OnFieldChangeListener>()

    fun getCell(row: Int, column: Int): Cell {
        if (row < 0 || column < 0 || row >= rows || column >= columns) return Cell.EMPTY
        return field[row][column]
    }

    fun setCell(row: Int, column: Int, cell: Cell) {
        if (row < 0 || column < 0 || row >= rows || column >= columns) return
        if (field[row][column] != cell) {
            field[row][column] = cell
            listeners.forEach { it.invoke(this) }
        }

    }

    fun addOnFieldChangeListener(listener: OnFieldChangeListener) {
        listeners.add(listener)
    }

    fun removeOnFieldChangeListener(listener: OnFieldChangeListener) {
        listeners.remove(listener)
    }

    fun clearOnFieldChangeListeners() {
        listeners.clear()
    }
}