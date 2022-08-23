package io.krasch.openreaddemo.image

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log
import io.krasch.openread.geometry.types.AngledRectangle
import io.krasch.openread.geometry.types.Point
import io.krasch.openreaddemo.TextRecognitionResult
import kotlin.math.min


class RecognitionResultsDrawer(val image: Bitmap){

}

fun drawOCRResults(canvas: Canvas, result: List<TextRecognitionResult>) {
    for (r in result) {
        if (r.text != null) {
            drawBoundingBox(canvas, r.box)
            drawText2(canvas, r.box, r.text)
        } else {
            drawBoundingBox(canvas, r.box, dotted = true)
        }
    }
}


private fun drawLine(canvas: Canvas, start: Point, end: Point, paint: Paint) {
    canvas.drawLine(
        start.x.toFloat(),
        start.y.toFloat(),
        end.x.toFloat(),
        end.y.toFloat(),
        paint,
    )
}

private fun drawBoundingBox(canvas: Canvas, box: AngledRectangle, dotted: Boolean = false) {
    val paint = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.STROKE
        strokeWidth = 10f
    }

    if (dotted)
        paint.pathEffect = DashPathEffect(floatArrayOf(10f, 10f, 10f, 10f), 0f)

    drawLine(canvas, box.bottomLeft, box.bottomRight, paint)
    drawLine(canvas, box.bottomRight, box.topRight, paint)
    drawLine(canvas, box.topRight, box.topLeft, paint)
    drawLine(canvas, box.topLeft, box.bottomLeft, paint)
}

private fun drawText(canvas: Canvas, box: AngledRectangle, text: String) {
    val textPaint = Paint().apply {
        color = Color.parseColor("#263d8c")
        textSize = 128f
    }

    val textBackgroundPaint = Paint().apply {
        color = Color.parseColor("#eeeeee")
        style = Paint.Style.FILL
    }

    val textHeight = textPaint.fontMetrics.descent - textPaint.fontMetrics.ascent

    val left = box.topLeft.x.toFloat()
    val top = box.topLeft.y.toFloat()

    val textWidth = textPaint.measureText(text)
    canvas.drawRect(left, top - textHeight, left + textWidth, top - 5f, textBackgroundPaint)

    canvas.drawText(text, left, top - 10f, textPaint)
}


private fun drawText2(canvas: Canvas, box: AngledRectangle, text: String){
    val angle = box.angleBottom.degree.toFloat()
    val x = box.bottomLeft.x.toFloat()
    val y = box.bottomLeft.y.toFloat()
    //val x = 0f
    //val y = 0f
    val width = box.boxWidth.toFloat()
    val height = box.boxHeight.toFloat()

    val textPaint = Paint().apply {
        color = Color.parseColor("#263d8c")
        textSize = 100f
    }

    val textBackgroundPaint = Paint().apply {
        color = Color.parseColor("#eeeeee")
        style = Paint.Style.FILL
    }

    val rect = Rect()
    textPaint.getTextBounds(text, 0, text.length, rect)

    val scaleHeight = box.height.toFloat() / rect.height()
    val scaleWidth = box.width.toFloat() / rect.width()
    val scale = min(scaleWidth, scaleHeight)

    canvas.save()
    canvas.translate(x, y)
    canvas.rotate(-box.angleBottom.degree.toFloat())
    canvas.scale(scale, scale)
    canvas.drawRect(0f, 0f, box.boxWidth.toFloat() / scale, - box.boxHeight.toFloat() / scale, textBackgroundPaint)
    canvas.drawText(text, 0f, 0f, textPaint)
    canvas.restore()



    /*val textHeight = textPaint.fontMetrics.descent - textPaint.fontMetrics.ascent

    val rect = Rect()
    textPaint.getTextBounds(text, 0, text.length, rect)
    Log.v("bla", "${rect.width()}, ${rect.height()}")

    val textWidth = textPaint.measureText(text)

    canvas.rotate(-angle, x, y)
    //canvas.drawRect(x, y, x + width, y - height, textBackgroundPaint)
    canvas.drawText(text, x, y, textPaint)
    canvas.rotate(angle, x, y)*/


    /*val rect = Rect()
    textPaint.getTextBounds(text, 0, text.length, rect)

    canvas.translate(box.bottomLeft.x.toFloat(), box.bottomLeft.y.toFloat())*/



    //val textBitmap = Bitmap.createBitmap(width=textWidth, height=TextH)
}
