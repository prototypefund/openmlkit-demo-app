package io.krasch.openread

import android.content.Context
import android.graphics.Bitmap
import io.krasch.openread.geometry.types.AngledRectangle
import io.krasch.openread.image.rotateAndCutout
import io.krasch.openread.models.DetectionModel
import io.krasch.openread.models.RecognitionModel


class Openread(
    private val detectionModel: DetectionModel,
    private val recognitionModel: RecognitionModel
) {

    suspend fun run(image: Bitmap): List<Pair<AngledRectangle, String>> {
        val detections = detectionModel.run(image)

        val results = detections.boxes.map { box ->
            val cutout = rotateAndCutout(image, box)
            val word = recognitionModel.run(cutout)
            Pair(box, word)
        }

        return results
    }

    companion object {
        suspend fun initialize(context: Context): Openread {
            return Openread(
                DetectionModel.initialize(context),
                RecognitionModel.initialize(context)
            )
        }
    }
}
