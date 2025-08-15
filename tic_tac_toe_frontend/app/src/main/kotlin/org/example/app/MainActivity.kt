package org.example.app

import android.app.Activity

import android.os.Bundle
import android.widget.Button
import android.widget.TextView


/**
 * PUBLIC_INTERFACE
 * MainActivity hosts the Tic Tac Toe game UI and logic.
 *
 * Responsibilities:
 * - Render a 3x3 board of buttons.
 * - Handle two-player turns (X and O).
 * - Detect win and draw conditions.
 * - Display minimalistic status feedback.
 * - Provide a restart action to reset the board.
 */
class MainActivity : Activity() {

    // Game UI
    private lateinit var statusText: TextView
    private lateinit var restartButton: Button
    private lateinit var cells: Array<Button>

    // Game State
    private var currentPlayer: Char = 'X'
    private val board: CharArray = CharArray(9) { ' ' }
    private var gameOver: Boolean = false

    // Winning triplets (indices in the 1D board)
    private val winningLines = arrayOf(
        intArrayOf(0, 1, 2),
        intArrayOf(3, 4, 5),
        intArrayOf(6, 7, 8),
        intArrayOf(0, 3, 6),
        intArrayOf(1, 4, 7),
        intArrayOf(2, 5, 8),
        intArrayOf(0, 4, 8),
        intArrayOf(2, 4, 6)
    )

    /**
     * PUBLIC_INTERFACE
     * Standard Activity lifecycle entrypoint.
     *
     * Sets up the UI, initializes state, and wires event listeners for the game.
     *
     * Parameters:
     * - savedInstanceState: Bundle? - Previously saved state, not used here.
     *
     * Return value:
     * - None.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusText = findViewById(R.id.statusText)
        restartButton = findViewById(R.id.restartButton)

        // Prepare cell references
        cells = arrayOf(
            findViewById(R.id.cell0),
            findViewById(R.id.cell1),
            findViewById(R.id.cell2),
            findViewById(R.id.cell3),
            findViewById(R.id.cell4),
            findViewById(R.id.cell5),
            findViewById(R.id.cell6),
            findViewById(R.id.cell7),
            findViewById(R.id.cell8)
        )

        // Attach click listeners to each cell
        cells.forEachIndexed { index, button ->
            button.setOnClickListener { onCellClicked(index) }
        }

        // Restart button
        restartButton.setOnClickListener { resetGame() }

        // Initialize UI state
        resetGame()
    }

    // Handle a user tapping on a cell.
    private fun onCellClicked(index: Int) {
        if (gameOver) return
        if (board[index] != ' ') return

        // Mark move
        board[index] = currentPlayer
        cells[index].text = currentPlayer.toString()
        cells[index].isEnabled = false

        // Check game status
        val winnerLine = findWinningLine()
        if (winnerLine != null) {
            gameOver = true
            highlightWinningCells(winnerLine)
            statusText.text = getString(R.string.player_wins, currentPlayer.toString())
            disableAllCells()
            return
        }

        if (isBoardFull()) {
            gameOver = true
            statusText.text = getString(R.string.draw)
            disableAllCells()
            return
        }

        // Next turn
        currentPlayer = if (currentPlayer == 'X') 'O' else 'X'
        updateTurnStatus()
    }

    private fun findWinningLine(): IntArray? {
        for (line in winningLines) {
            val a = line[0]
            val b = line[1]
            val c = line[2]
            if (board[a] != ' ' && board[a] == board[b] && board[b] == board[c]) {
                return line
            }
        }
        return null
    }

    private fun isBoardFull(): Boolean {
        for (c in board) {
            if (c == ' ') return false
        }
        return true
    }

    private fun updateTurnStatus() {
        statusText.text = getString(R.string.turn, currentPlayer.toString())
    }

    private fun disableAllCells() {
        cells.forEach { it.isEnabled = false }
    }

    private fun enableAllCells() {
        cells.forEach { it.isEnabled = true }
    }

    private fun clearCells() {
        val defaultBgColor = resources.getColor(R.color.board_cell_bg, theme)
        cells.forEach {
            it.text = ""
            it.backgroundTintList = android.content.res.ColorStateList.valueOf(defaultBgColor)
        }
    }

    private fun highlightWinningCells(line: IntArray) {
        val accent = resources.getColor(R.color.accent, theme)
        line.forEach { idx ->
            cells[idx].backgroundTintList = android.content.res.ColorStateList.valueOf(accent)
        }
    }

    private fun resetBoardState() {
        for (i in board.indices) {
            board[i] = ' '
        }
        currentPlayer = 'X'
        gameOver = false
    }

    private fun resetGame() {
        resetBoardState()
        clearCells()
        enableAllCells()
        updateTurnStatus()
    }
}
