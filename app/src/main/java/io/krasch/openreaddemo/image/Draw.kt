package io.krasch.openreaddemo.image

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import io.krasch.openread.geometry.types.AngledRectangle
import io.krasch.openread.geometry.types.Point
import io.krasch.openreaddemo.OCRResult


fun drawOCRResults(bitmap: Bitmap, result: List<OCRResult>): Bitmap {
    val mutable = bitmap.copy(Bitmap.Config.ARGB_8888, true)
    val canvas = Canvas(mutable)

    for (r in result){
        if (r.text != null){
            drawBoundingBox(canvas, r.box)
            drawText(canvas, r.box, r.text)
        }
        else {
            drawBoundingBox(canvas, r.box, dotted=true)
        }
    }

    return mutable
}

fun drawLine(canvas: Canvas, start: Point, end: Point, paint: Paint) {
    canvas.drawLine(
        start.x.toFloat(),
        start.y.toFloat(),
        end.x.toFloat(),
        end.y.toFloat(),
        paint,
    )
}

fun drawBoundingBox(canvas: Canvas, box: AngledRectangle, dotted: Boolean = false) {
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

fun drawText(canvas: Canvas, box: AngledRectangle, text: String){
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
