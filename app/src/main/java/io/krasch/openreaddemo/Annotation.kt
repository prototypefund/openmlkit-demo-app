package io.krasch.openreaddemo

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import io.krasch.openread.DetectionResult
import io.krasch.openread.OCRResult
import io.krasch.openread.geometry.types.AngledRectangle
import io.krasch.openread.geometry.types.Point


fun drawDetections(bitmap: Bitmap, result: List<DetectionResult>): Bitmap {
    val mutable = bitmap.copy(Bitmap.Config.ARGB_8888, true)
    val canvas = Canvas(mutable)

    for (r in result){
        drawSegmentation(canvas, r.segmentation)
        drawHull(canvas, r.hull)
        drawBoundingBox(canvas, r.rectangle)
    }

    return mutable
}

fun drawOCRResults(bitmap: Bitmap, result: List<OCRResult>): Bitmap {
    val mutable = bitmap.copy(Bitmap.Config.ARGB_8888, true)
    val canvas = Canvas(mutable)

    for (r in result){
        //drawHull(canvas, r.detection.hull)
        drawBoundingBox(canvas, r.detection.rectangle)
        //drawText(canvas, r)
    }

    return mutable
}

fun drawSegmentation(canvas: Canvas, result: List<Point>) {
    val paint = Paint()
    paint.style = Paint.Style.FILL
    paint.color = Color.RED

    for (pixel in result) {
        //canvas.drawPoint(100f, 100f, paint)
        canvas.drawRect(
            pixel.x.toFloat(),
            pixel.y.toFloat(),
            pixel.x.toFloat() + 20,
            pixel.y.toFloat() + 20,
            paint
        )
    }
}

fun drawLine(canvas: Canvas, start: Point, end: Point, paint: Paint) {
    canvas.drawLine(
        start.x.toFloat(),
        start.y.toFloat(),
        end.x.toFloat(),
        end.y.toFloat(),
        paint
    )
}

fun drawHull(canvas: Canvas, result: List<Point>) {
    val paint = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.STROKE
        strokeWidth = 50f
    }

    for (i in 1 until result.size) {
        canvas.drawLine(
            result[i - 1].x.toFloat(),
            result[i - 1].y.toFloat(),
            result[i].x.toFloat(),
            result[i].y.toFloat(),
            paint
        )
    }

    canvas.drawLine(
        result.last().x.toFloat(),
        result.last().y.toFloat(),
        result.first().x.toFloat(),
        result.first().y.toFloat(),
        paint
    )
}

fun drawBoundingBox(canvas: Canvas, box: AngledRectangle) {
    val paint = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.STROKE
        strokeWidth = 10f
    }

    drawLine(canvas, box.bottomLeft, box.bottomRight, paint)
    drawLine(canvas, box.bottomRight, box.topRight, paint)
    drawLine(canvas, box.topRight, box.topLeft, paint)
    drawLine(canvas, box.topLeft, box.bottomLeft, paint)
}

/*
fun drawText2(text: String): Bitmap {
    val textPaint = Paint().apply {
        color = Color.RED
        textSize = 128f
    }

    val textHeight = textPaint.fontMetrics.descent - textPaint.fontMetrics.ascent
    val textWidth = textPaint.measureText(text)

    val rect = AngledRectangle(
        Point(0.0, 0.0),
        textWidth.toDouble(),
        textHeight.toDouble(),
        Angle.fromDegree(-30f)
    )

    Log.v("bla", textWidth.toString())
    //Log.v("bla", textHeight.toString())
    //Log.v("bla", rotatedWidth.toString())
    Log.v("bla", rect.boxWidth.toString())
    // Log.v("bla", rect.boxHeight.toString())

    val left = 0
    val top = 0

    val bitmap = Bitmap.createBitmap(rect.boxWidth.toInt(), rect.boxHeight.toInt(), Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    canvas.rotate(-30.0f)
    canvas.drawText(text, 0.0f, (0.0f - rect.boxHeight).toFloat(), textPaint)
    //canvas.rotate(45.0f)

    return bitmap
}*/

fun drawText(canvas: Canvas, result: OCRResult){
    val textPaint = Paint().apply {
        color = Color.parseColor("#263d8c")
        textSize = 128f
    }

    val textBackgroundPaint = Paint().apply {
        color = Color.parseColor("#eeeeee")
        style = Paint.Style.FILL
    }

    val textHeight = textPaint.fontMetrics.descent - textPaint.fontMetrics.ascent

    val left = result.detection.rectangle.topLeft.x.toFloat()
    val top = result.detection.rectangle.topLeft.y.toFloat()

    val textWidth = textPaint.measureText(result.text)
    canvas.drawRect(left, top - textHeight, left + textWidth, top - 5f, textBackgroundPaint)

    canvas.drawText(result.text, left, top - 10f, textPaint)
}

/*
fun drawOCRResult(bitmap: Bitmap, texts: List<OCRResult>): Bitmap {
    val mutable = bitmap.copy(Bitmap.Config.ARGB_8888, true)

    val boxPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }

    val textPaint = Paint().apply {
        color = Color.RED
        textSize = 64f
    }

    val textBackgroundPaint = Paint().apply {
        color = Color.LTGRAY
        style = Paint.Style.FILL
    }


    val textHeight = textPaint.fontMetrics.descent - textPaint.fontMetrics.ascent

    val canvas = Canvas(mutable)
    for (result in texts) {
        canvas.drawRect(
            result.box.left.toFloat(),
            result.box.top.toFloat(),
            result.box.right.toFloat(),
            result.box.bottom.toFloat(),
            boxPaint
        )

        val textWidth = textPaint.measureText(result.text)
        canvas.drawRect(
            result.box.left.toFloat(),
            result.box.top.toFloat() - textHeight,
            result.box.left.toFloat() + textWidth,
            result.box.top.toFloat() - 5f,
            textBackgroundPaint);

        canvas.drawText(
            result.text,
            result.box.left.toFloat(),
            result.box.top.toFloat() - 10f,
            textPaint);
    }
    return mutable
}*/