package io.krasch.openreaddemo.image

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.Log
import io.krasch.openread.geometry.types.AngledRectangle
import io.krasch.openreaddemo.TextRecognitionResult
import kotlin.math.min


// todo maybe this should take a mutable bitmap instead to make code more obvious?
class RecognitionResultsDrawer(val original: Bitmap){
    private val mutable: Bitmap = original.copy(Bitmap.Config.ARGB_8888, true)
    private val canvas = Canvas(mutable)

    val image: Bitmap
        get() = mutable


    private val solidLinePaint = Paint().apply {
        color = Color.parseColor("#4e2780")
        style = Paint.Style.STROKE
        strokeWidth = 10f
    }

    private val dashedLinePaint = Paint(solidLinePaint).apply {
        color = Color.GREEN
        strokeWidth = 10f
        pathEffect = DashPathEffect(floatArrayOf(10f, 10f, 10f, 10f), 0f)
    }

    private val textPaint = Paint().apply {
        color = Color.parseColor("#4e2780")
        textSize = 50f
    }

    private val textBackgroundPaint = Paint().apply {
        color = Color.parseColor("#ddd9e4ea")
        //alpha = 100
        style = Paint.Style.FILL
    }

    fun drawResults(results: List<TextRecognitionResult>) {
        results.map {(box, text) -> drawResult(box, text) }
    }

    private fun drawResult(box: AngledRectangle, text: String?) {
        // set canvas so that origin goes through bottom left point of box and text is horizontal
        canvas.save()
        canvas.translate(box.bottomLeft.x.toFloat(), box.bottomLeft.y.toFloat())
        canvas.rotate(-box.angleBottom.degree.toFloat())

        // now only need to care about the width and height of the box
        // ovserve that -box.height because canvas draws upwards rather than downwards
        val translatedBox = RectF(0f, 0f, box.width.toFloat(), -box.height.toFloat())

        val cornerRadius = min(box.width, box.height).toFloat() * 0.1f

        // text recognition has not run yet
        if (text == null){
            // draw just a dashed bounding box, no text to be drawn
            canvas.drawRoundRect(translatedBox, cornerRadius, cornerRadius, dashedLinePaint)
        }
        // text recognition is finished, draw bounding box and text
        else {
            // background for the text
            canvas.drawRoundRect(translatedBox, cornerRadius, cornerRadius, textBackgroundPaint)

            // box around that background
            canvas.drawRoundRect(translatedBox, cornerRadius, cornerRadius, solidLinePaint)

            // write the text itself (observe that now undoing the above -box.height)
            if (text.isNotEmpty())
                writeText(canvas, translatedBox.width(), -translatedBox.height(), text)
            else
                writeText(canvas, translatedBox.width(), -translatedBox.height(), "?")
        }

        canvas.restore()
    }


    private fun writeText(canvas: Canvas, boxWidth: Float, boxHeight: Float, text: String) {
        // measure how large the text would be with our default text paint
        val defaultTextRect = Rect()
        textPaint.getTextBounds(text, 0, text.length, defaultTextRect)

        // how much to scale the canvas so that the text perfectly fits into the bounding box?
        val scaleHeight = boxHeight / defaultTextRect.height()
        val scaleWidth = boxWidth / defaultTextRect.width()
        val scale = min(scaleWidth, scaleHeight)

        // when we scale the canvas by this amount, how wide and tall will the final text be?
        val actualWidth = scale * defaultTextRect.width()
        val actualHeight = scale * defaultTextRect.height()

        // want the text to be centered, so apply padding accordingly
        val paddingLeft = (boxWidth - actualWidth) / 2f
        val paddingTop = (boxHeight - actualHeight) / 2f

        // drawText includes a little whitespace included before the first letter
        // need to consider this when calculating the padding
        val whitespaceLeft = defaultTextRect.left * scale

        // finally apply all the transformations and write the text on scaled canvas
        canvas.save()
        canvas.translate(paddingLeft - whitespaceLeft, -paddingTop)
        canvas.scale(scale, scale)
        canvas.drawText(text, 0f, 0f, textPaint)
        canvas.restore()
    }
}