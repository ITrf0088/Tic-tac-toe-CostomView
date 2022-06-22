package org.rasulov.tictactoe.tictactoe

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import org.rasulov.tictactoe.R
import kotlin.math.max
import kotlin.math.min

class TicTacToeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.ticTacToeStyleAttr,
    defStyleRes: Int = R.style.ticTacToeDefaultStyle
) : View(context, attrs, defStyleAttr, defStyleRes) {

    var actionListener: ((Int, Int, TicTacToe) -> Unit)? = null

    var gameField: TicTacToe? = null
        set(value) {
            field?.clearOnFieldChangeListeners()
            field = value
            field?.addOnFieldChangeListener(listener)
            requestLayout()
            invalidate()
        }

    private val fieldRect = RectF(0f, 0f, 0f, 0f)

    // size of one cell
    private var cellSize: Float = 0f

    // padding in the cell
    private var cellPadding: Float = 0f

    private var firstPlayerColor = 0
    private var secondPlayerColor = 0
    private var gridColor = 0
    private val listener: OnFieldChangeListener = { invalidate() }

    private lateinit var firstPlayerPaint: Paint
    private lateinit var secondPlayerPaint: Paint
    private lateinit var currentCellPaint: Paint
    private lateinit var gridPaint: Paint


    init {
        initAttributes(attrs, defStyleAttr, defStyleRes)
        initPaints()
        if (isInEditMode) {
            gameField = TicTacToe(8, 6)
            gameField?.setCell(1, 1, Cell.FIRST_PLAYER)
            gameField?.setCell(1, 2, Cell.SECOND_PLAYER)
            gameField?.setCell(5, 1, Cell.FIRST_PLAYER)
            gameField?.setCell(5, 2, Cell.SECOND_PLAYER)
        }
    }


    private fun initAttributes(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        if (attrs == null) {
            Log.d("it0088", "initAttributes: attrs = null")
            firstPlayerColor = Color.BLACK
            secondPlayerColor = Color.YELLOW
            gridColor = Color.GRAY
            return
        }
        Log.d("it0088", "initAttributes: attrs != null")

        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.TicTacToeView,
            defStyleAttr,
            defStyleRes
        )

        firstPlayerColor =
            typedArray.getColor(R.styleable.TicTacToeView_firstPlayerColor, Color.BLACK)
        secondPlayerColor =
            typedArray.getColor(R.styleable.TicTacToeView_secondPlayerColor, Color.YELLOW)
        gridColor = typedArray.getColor(R.styleable.TicTacToeView_gridColor, Color.GRAY)

        typedArray.recycle()
    }

    private fun initPaints() {
        // paint for drawing X
        firstPlayerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        firstPlayerPaint.color = firstPlayerColor
        firstPlayerPaint.style = Paint.Style.STROKE
        firstPlayerPaint.strokeWidth =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, resources.displayMetrics)

        // paint for drawing O
        secondPlayerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        secondPlayerPaint.color = secondPlayerColor
        secondPlayerPaint.style = Paint.Style.STROKE
        secondPlayerPaint.strokeWidth =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, resources.displayMetrics)

        // paint for drawing grid
        gridPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        gridPaint.color = gridColor
        gridPaint.style = Paint.Style.STROKE
        gridPaint.strokeWidth =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, resources.displayMetrics)

        // paint for drawing current cell
        currentCellPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        currentCellPaint.color = Color.rgb(230, 230, 230)
        currentCellPaint.style = Paint.Style.FILL
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        gameField?.addOnFieldChangeListener(listener)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        gameField?.clearOnFieldChangeListeners()
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (gameField == null) return
        if (cellSize == 0f) return
        if (fieldRect.width() <= 0) return
        if (fieldRect.height() <= 0) return

        drawGrid(canvas)
        drawCurrentCell(canvas)
        drawCells(canvas)


    }

    private fun drawGrid(canvas: Canvas) {
        val field = this.gameField ?: return

        val xStart = fieldRect.left
        val xEnd = fieldRect.right
        for (i in 0..field.rows) {
            val y = fieldRect.top + cellSize * i
            canvas.drawLine(xStart, y, xEnd, y, gridPaint)
        }

        val yStart = fieldRect.top
        val yEnd = fieldRect.bottom
        for (i in 0..field.columns) {
            val x = fieldRect.left + cellSize * i
            canvas.drawLine(x, yStart, x, yEnd, gridPaint)
        }
    }

    private fun drawCells(canvas: Canvas) {
        val rows = gameField?.rows ?: return
        val columns = gameField?.columns ?: return

        val radius = ((cellSize - cellPadding) / 2)
        val bias = cellSize / 2

        for (i in (0 until rows)) {
            val y = (fieldRect.top + cellSize * i)
            for (j in (0 until columns)) {
                val x = (fieldRect.left + cellSize * j)
                if (gameField?.getCell(i, j) == Cell.FIRST_PLAYER) {
                    drawX(x, y, canvas)
                } else if (gameField?.getCell(i, j) == Cell.SECOND_PLAYER) {
                    drawO(x + bias, y + bias, radius, canvas)
                }
            }
        }
    }

    private fun drawX(x: Float, y: Float, canvas: Canvas) {
        val startX = x + cellPadding
        val startY = y + cellPadding
        val cellSizeWithPadding = cellSize - cellPadding
        val endX = x + cellSizeWithPadding
        val endY = y + cellSizeWithPadding
        canvas.drawLine(startX, startY, endX, endY, firstPlayerPaint)
        canvas.drawLine(endX, startY, startX, endY, firstPlayerPaint)
    }

    private fun drawO(x: Float, y: Float, radius: Float, canvas: Canvas) {
        canvas.drawCircle(x, y, radius, secondPlayerPaint)
    }

    private fun drawCurrentCell(canvas: Canvas) {
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val minWidth = suggestedMinimumWidth + paddingLeft + paddingRight
        val minHeight = suggestedMinimumHeight + paddingTop + paddingBottom

        val desiredCellSizeInPixels = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, DESIRED_CELL_SIZE,
            resources.displayMetrics
        ).toInt()
        val rows = gameField?.rows ?: 0
        val columns = gameField?.columns ?: 0

        val desiredWith =
            max(minWidth, columns * desiredCellSizeInPixels + paddingLeft + paddingRight)
        val desiredHeight =
            max(minHeight, rows * desiredCellSizeInPixels + paddingTop + paddingBottom)

        setMeasuredDimension(
            resolveSize(desiredWith, widthMeasureSpec),
            resolveSize(desiredHeight, heightMeasureSpec)
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateViewSizes()
    }

    private fun updateViewSizes() {
        val field = this.gameField ?: return

        val safeWidth = width - paddingLeft - paddingRight
        val safeHeight = height - paddingTop - paddingBottom

        val cellWidth = safeWidth / field.columns.toFloat()
        val cellHeight = safeHeight / field.rows.toFloat()

        cellSize = min(cellWidth, cellHeight)
        cellPadding = cellSize * 0.2f

        val fieldWidth = cellSize * field.columns
        val fieldHeight = cellSize * field.rows

        fieldRect.left = paddingLeft + (safeWidth - fieldWidth) / 2
        fieldRect.top = paddingTop + (safeHeight - fieldHeight) / 2
        fieldRect.right = fieldRect.left + fieldWidth
        fieldRect.bottom = fieldRect.top + fieldHeight
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                return true
            }
            MotionEvent.ACTION_UP -> {
                Log.d("it0088", "event.x: ${event.x}")
                Log.d("it0088", "event.y: ${event.y}")
                Log.d("it0088", "x: ${fieldRect.left}")
                Log.d("it0088", "y: ${fieldRect.top}")
                Log.d("it0088", "cellsize: $cellSize")
                val row = getRow(event)
                val column = getColumn(event)

                actionListener?.invoke(row, column, gameField!!)
                return true
            }
        }

        return false
    }

    private fun getRow(event: MotionEvent): Int {
        return ((event.y - fieldRect.top) / cellSize).toInt()
    }

    private fun getColumn(event: MotionEvent): Int {
        return ((event.x - fieldRect.left) / cellSize).toInt()
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    companion object {
        const val DESIRED_CELL_SIZE = 50f
    }

}