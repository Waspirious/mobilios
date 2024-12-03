package com.myapplication.ui.map

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

data class ClosestCoordinate(
    val xCoordinate: Int,
    val yCoordinate: Int,
    val label: String // Use this for numbers or other identifiers
)

class MapView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val gridPaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }

    private val highlightedPaint = Paint().apply {
        color = Color.GREEN // Color for blocks in database
        style = Paint.Style.FILL
    }

    private val closestPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
    }

    private val textPaint = Paint().apply {
        color = Color.BLACK // Color for the text
        textSize = 30f // Adjust text size as needed
        style = Paint.Style.FILL
    }

    private val cellSize = 35f  // Adjust cell size as needed
    private val xRange = -5..6
    private val yRange = -11..35

    // List of points from the database that need to be highlighted
    private var closestCoordinates = listOf<ClosestCoordinate>()
    private var highlightedPoints = listOf<Pair<Int, Int>>()

    // Function to set highlighted points and redraw the view
    fun setHighlightedPoints(points: List<Pair<Int, Int>>) {
        highlightedPoints = points
        invalidate() // Triggers a redraw with new points
    }

    fun setClosestCoordinates(points: List<com.myapplication.database.ClosestCoordinate>) {
        closestCoordinates = points.mapIndexed { index, dbCoordinate ->
            ClosestCoordinate(
                xCoordinate = dbCoordinate.xCoordinate,
                yCoordinate = dbCoordinate.yCoordinate,
                label = (index + 1).toString() // Assign labels starting from 1
            )
        }
        invalidate() // Triggers a redraw with new points
    }

    // Function to draw the grid lines
    private fun drawGrid(canvas: Canvas) {
        for (x in xRange) {
            for (y in yRange) {
                val left = (x - xRange.first) * cellSize
                // Invert Y position by subtracting from the maximum Y value
                val top = (yRange.last - y) * cellSize // Adjusted here
                val right = left + cellSize
                val bottom = top + cellSize

                // Draw the cell border
                canvas.drawRect(left, top, right, bottom, gridPaint)
            }
        }
    }

    // Function to fill in grid blocks that are highlighted
    private fun fillHighlightedGridBlocks(canvas: Canvas) {
        for ((x, y) in highlightedPoints) {
            val left = (x - xRange.first) * cellSize
            // Invert Y position
            val top = (yRange.last - y) * cellSize // Adjusted here
            val right = left + cellSize
            val bottom = top + cellSize

            // Fill the cell with color
            canvas.drawRect(left, top, right, bottom, highlightedPaint)
        }
    }

    private fun fillClosestGridBlocks(canvas: Canvas) {
        for (point in closestCoordinates) {
            val left = (point.xCoordinate - xRange.first) * cellSize
            // Invert Y position
            val top = (yRange.last - point.yCoordinate) * cellSize // Adjusted here
            val right = left + cellSize
            val bottom = top + cellSize

            // Fill the cell with the highlighted color
            canvas.drawRect(left, top, right, bottom, closestPaint)

            // Draw the label in the center of the cell
            val textX = left + cellSize / 2
            val textY = top + cellSize / 2 - (textPaint.descent() + textPaint.ascent()) / 2 // Center vertically
            canvas.drawText(point.label, textX, textY, textPaint)
        }
    }

    // Function to draw coordinate numbers
    private fun drawCoordinates(canvas: Canvas) {
        // Draw X-axis labels
        for (x in xRange) {
            val xPos = (x - xRange.first) * cellSize + cellSize / 2
            canvas.drawText(x.toString(), xPos - 10, (yRange.last - yRange.first + 1) * cellSize + 30, textPaint)
        }

        // Draw Y-axis labels
        for (y in yRange) {
            val yPos = (yRange.last - y) * cellSize + cellSize / 2 // Inverted position
            val xOffset = (xRange.count() + 1) * cellSize // Offset for right positioning
            canvas.drawText(y.toString(), xOffset - 20, yPos+10, textPaint) // Position on the right side
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        fillHighlightedGridBlocks(canvas)
        fillClosestGridBlocks(canvas)

        // Draw coordinate numbers
        drawCoordinates(canvas)
        drawGrid(canvas)
    }
}
