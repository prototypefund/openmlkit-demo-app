package io.krasch.openreaddemo

/*
// TODO somehow the bounding box ends up at the wrong place
class AnnotationView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var texts: List<OCRResult>? = null
    private var imageWidth: Int = -1
    private var imageHeight: Int = -1

    private val paint = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }

    override fun onDraw(canvas: Canvas) {

        val scaleX = width.toFloat() / imageWidth.toFloat()
        val scaleY = height.toFloat() / imageHeight.toFloat()

        Log.v("bla", "Image width: $imageWidth")
        Log.v("bla", "Image height: $imageHeight")
        Log.v("bla", "View width: $width")
        Log.v("bla", "View height: $height")

        Log.v("bla", "$scaleX $scaleY")

        /*texts?.map {
            Log.v("bla", it.text)
            canvas.drawRect(
                it.box.left * scaleX,
                it.box.top * scaleY,
                it.box.right * scaleX,
                it.box.bottom * scaleY,
                paint
            )
        }*/
    }

    fun setOCRResult(imageWidth: Int, imageHeight: Int, texts: List<OCRResult>) {
        Log.v("bla", texts.toString())

        this.imageWidth = imageWidth
        this.imageHeight = imageHeight
        this.texts = texts

        this.invalidate()
    }
}*/