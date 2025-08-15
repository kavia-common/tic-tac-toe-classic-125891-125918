package org.example.app

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView

/**
 * PUBLIC_INTERFACE
 * MainActivity
 *
 * This is the single-screen entry point for the Tic Tac Toe app. It renders a minimalistic,
 * light-themed 3x3 board, supports two-player gameplay (X and O), performs win and draw detection,
 * indicates which player's turn it is, announces the winner or a draw, and provides a restart button
 * to reset the game.
 *
 * UI elements:
 * - Turn indicator (TextView) showing which player's turn it is.
 * - Centered 3x3 board (GridLayout with 9 Buttons).
 * - Status message (TextView) to announce winner or draw.
 * - Restart button (Button) below the board.
 *
 * Game logic:
 * - The board is represented by a CharArray of size 9 ('X', 'O', or ' ' for empty).
 * - After each move, we check for a winning combination or a draw.
 * - Winning cells are highlighted with the accent color.
 * - The game state is preserved across configuration changes (e.g., rotation).
 */
class MainActivity : Activity() {

    // UI references
    private lateinit var tvTurnIndicator: TextView
    private lateinit var tvStatusMessage: TextView
    private lateinit var btnRestart: Button
    private lateinit var gridLayout: GridLayout
    private lateinit var cells: Array<Button>

    // Game state
    private var board: CharArray = CharArray(9) { ' ' }
    private var currentPlayer: Char = 'X'
    private var gameOver: Boolean = false

    /**
     * PUBLIC_INTERFACE
     * Called when the activity is starting.
     *
     * Parameters:
     * - savedInstanceState: Bundle? - If non-null, this activity is being reinitialized after
     *   previously being shut down. Contains the previous state including the board, the current player,
     *   and whether the game is over.
     *
     * Returns:
     * - Unit (no return value).
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Bind UI elements
        tvTurnIndicator = findViewById(R.id.tvTurnIndicator)
        tvStatusMessage = findViewById(R.id.tvStatusMessage)
        btnRestart = findViewById(R.id.btnRestart)
        gridLayout = findViewById(R.id.gridBoard)

        // Collect cell buttons in a fixed order (0..8)
        cells = arrayOf(
            findViewById(R.id.cell0),
            findViewById(R.id.cell1),
            findViewById(R.id.cell2),
            findViewById(R.id.cell3),
            findViewById(R.id.cell4),
            findViewById(R.id.cell5),
            findViewById(R.id.cell6),
            findViewById(R.id.cell7),
            findViewById(R.id.cell8),
        )

        // Set click listeners for each cell
        cells.forEachIndexed { index, button ->
            button.setOnClickListener { onCellClicked(index) }
        }

        // Restart button
        btnRestart.setOnClickListener { restartGame() }

        // Restore state if available
        if (savedInstanceState != null) {
            val savedBoard = savedInstanceState.getCharArray(KEY_BOARD)
            val savedPlayer = savedInstanceState.getChar(KEY_PLAYER, 'X')
            val savedGameOver = savedInstanceState.getBoolean(KEY_GAME_OVER, false)

            if (savedBoard != null && savedBoard.size == 9) {
                board = savedBoard
            }
            currentPlayer = savedPlayer
            gameOver = savedGameOver
            // Repaint the UI based on restored state
            repaintBoard()
            updateTurnIndicator()
            updateStatusMessageFromBoard()
        } else {
            // Fresh start
            updateTurnIndicator()
            tvStatusMessage.text = ""
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putCharArray(KEY_BOARD, board)
        outState.putChar(KEY_PLAYER, currentPlayer)
        outState.putBoolean(KEY_GAME_OVER, gameOver)
    }

    private fun onCellClicked(index: Int) {
        if (gameOver) return
        if (index !in 0..8) return
        if (board[index] != ' ') return

        // Make the move
        board[index] = currentPlayer
        cells[index].text = currentPlayer.toString()
        cells[index].isEnabled = false

        // Evaluate game state
        val winningLine = getWinningLine(board)
        if (winningLine != null) {
            gameOver = true
            highlightWinningCells(winningLine)
            tvStatusMessage.setText(
                if (currentPlayer == 'X') R.string.win_x else R.string.win_o
            )
            // Keep the turn indicator as last player who won
            updateTurnIndicator()
            disableAllCells()
            return
        }

        if (isDraw(board)) {
            gameOver = true
            tvStatusMessage.setText(R.string.status_draw)
            // Turn indicator no longer relevant, but keep it visible
            disableAllCells()
            return
        }

        // Continue game: switch player and update UI
        currentPlayer = if (currentPlayer == 'X') 'O' else 'X'
        updateTurnIndicator()
    }

    private fun updateTurnIndicator() {
        tvTurnIndicator.setText(
            if (currentPlayer == 'X') R.string.turn_player_x else R.string.turn_player_o
        )
    }

    private fun updateStatusMessageFromBoard() {
        val winningLine = getWinningLine(board)
        if (winningLine != null) {
            tvStatusMessage.setText(
                if (currentPlayer == 'X' && !gameOver) {
                    // Not typical path, but keep consistent
                    R.string.win_x
                } else if (currentPlayer == 'O' && !gameOver) {
                    R.string.win_o
                } else {
                    // Determine by board
                    val winner = board[winningLine[0]]
                    if (winner == 'X') R.string.win_x else R.string.win_o
                }
            )
            // Highlight and ensure disabled if the game is over
            if (gameOver) {
                highlightWinningCells(winningLine)
                disableAllCells()
            }
        } else if (isDraw(board) && gameOver) {
            tvStatusMessage.setText(R.string.status_draw)
        } else {
            tvStatusMessage.text = ""
        }
    }

    private fun repaintBoard() {
        // Reset all cell visuals first
        for (i in 0..8) {
            cells[i].setBackgroundResource(R.drawable.cell_background)
            cells[i].text = if (board[i] == ' ') "" else board[i].toString()
            cells[i].isEnabled = !gameOver && board[i] == ' '
        }

        // If there's a winning line, highlight it
        val winningLine = getWinningLine(board)
        if (winningLine != null) {
            highlightWinningCells(winningLine)
        }
    }

    private fun highlightWinningCells(line: IntArray) {
        val accent = getColor(R.color.colorAccent)
        line.forEach { idx ->
            cells[idx].setBackgroundColor(accent)
        }
    }

    private fun disableAllCells() {
        for (i in 0..8) {
            cells[i].isEnabled = false
        }
    }

    private fun enableAllEmptyCells() {
        for (i in 0..8) {
            cells[i].isEnabled = board[i] == ' '
        }
    }

    private fun isDraw(b: CharArray): Boolean {
        // Draw if no empty spaces and no winner
        if (getWinningLine(b) != null) return false
        return b.none { it == ' ' }
    }

    private fun getWinningLine(b: CharArray): IntArray? {
        val lines = arrayOf(
            intArrayOf(0, 1, 2),
            intArrayOf(3, 4, 5),
            intArrayOf(6, 7, 8),
            intArrayOf(0, 3, 6),
            intArrayOf(1, 4, 7),
            intArrayOf(2, 5, 8),
            intArrayOf(0, 4, 8),
            intArrayOf(2, 4, 6)
        )
        for (line in lines) {
            val (a, bIdx, c) = line
            val ch = b[a]
            if (ch != ' ' && ch == b[bIdx] && ch == b[c]) {
                return line
            }
        }
        return null
    }

    private fun clearBoard() {
        for (i in 0..8) {
            board[i] = ' '
        }
    }

    private fun resetCellViews() {
        for (i in 0..8) {
            cells[i].text = ""
            cells[i].isEnabled = true
            cells[i].setBackgroundResource(R.drawable.cell_background)
        }
    }

    private fun restartGame() {
        gameOver = false
        currentPlayer = 'X'
        clearBoard()
        resetCellViews()
        tvStatusMessage.text = ""
        updateTurnIndicator()
        enableAllEmptyCells()
    }

    companion object {
        private const val KEY_BOARD = "state_board"
        private const val KEY_PLAYER = "state_player"
        private const val KEY_GAME_OVER = "state_game_over"
    }
}
