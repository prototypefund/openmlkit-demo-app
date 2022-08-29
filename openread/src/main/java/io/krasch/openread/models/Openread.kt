package io.krasch.openread.models

import android.content.Context
import android.graphics.Bitmap
import io.krasch.openread.geometry.types.AngledRectangle
import io.krasch.openread.image.rotateAndCutout

// todo located here because some duplicate package name issue when releasing app
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
