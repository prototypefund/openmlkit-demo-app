package io.krasch.openread.tflite

import android.content.Context
import android.graphics.Bitmap
import io.krasch.openread.models.DETECTION_MODEL_PATH
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.gpu.GpuDelegate
import java.nio.ByteBuffer
import java.nio.MappedByteBuffer

val compatList = CompatibilityList()

open class ImageModel(modelFile: MappedByteBuffer, hasGPUSupport: Boolean = false) {

    val options = Interpreter.Options().apply {
        if (hasGPUSupport and compatList.isDelegateSupportedOnThisDevice) {
            // if the device has a supported GPU, add the GPU delegate
            val delegateOptions = compatList.bestOptionsForThisDevice
            this.addDelegate(GpuDelegate(delegateOptions))
        } else {
            // if the GPU is not supported, run on 4 threads
            // this.numThreads = 4  // todo why does this not work?
        }
    }

    // actual tflite model is loaded here
    val model = Interpreter(modelFile, options)

    // these values are important for the caller to prepare the input image
    val inputHeight: Int
    val inputWidth: Int

    // this value is only needed internally, caller will always send us an RGB image
    // it will be this classes responsibility to convert to greyscale if necessary
    private val imageIsGreyscale: Boolean

    // constructor with lots of validations
    init {
        // only models with one input (an image) are supported
        require(model.inputTensorCount == 1)
        val inputTensor = model.getInputTensor(0)

        // only float representations of image supported
        require(inputTensor.dataType() == DataType.FLOAT32)

        // tensor shape must have 4 entries: batch_size, height, width, num_channels
        require(inputTensor.shape().size == 4)
        val (batchSize, height, width, numChannels) = inputTensor.shape()

        // only batch size = 1 is supported at the moment
        require(batchSize == 1)

        // only RGB images (3 channels) or greyscale images (1 channel) are supported
        require((numChannels == 3) or (numChannels == 1))

        imageIsGreyscale = numChannels == 1
        inputHeight = height
        inputWidth = width
    }

    suspend fun predict(bitmap: Bitmap): List<Array<Float>> {
        // convert the input image into a bytebuffer
        val inputBuffer = allocateByteBuffer(model.getInputTensor(0))
        bitmapToByteBuffer(bitmap, inputBuffer, imageIsGreyscale)

        // initialize a byte buffer for every output tensor (there can be multiple)
        val outputIndexes = 0 until model.outputTensorCount
        val outputBuffers = outputIndexes.map { allocateByteBuffer(model.getOutputTensor(it)) }

        // need to wrap our inputs/outputs so that they are in the format tflite expects
        // input should be an array of ByteBuffers (for us, this array only has one element)
        // outputs should be a map of {outputTensorIndex to outputTensor}
        val inputs = arrayOf(inputBuffer)
        val outputs = outputIndexes.zip(outputBuffers).toMap()

        // here is where the model is actually executed (in a different thread!)
        withContext(Dispatchers.IO) {
            model.runForMultipleInputsOutputs(inputs, outputs)
        }

        val parsed = outputIndexes.map {
            byteBufferToArray(outputBuffers[it], model.getOutputTensor(it).dataType())
        }

        return parsed
    }

    suspend fun predict(input: ByteBuffer, output: ByteBuffer) {
        withContext(Dispatchers.IO) {
            model.run(input, output)
        }
    }

    companion object {
        suspend fun initialize(
            assetsPath: String,
            context: Context,
            hasGPUSupport: Boolean
        ): ImageModel {
            return withContext(Dispatchers.IO) {
                val file = context.assets.openFd(assetsPath)
                val weights = fileToByteBuffer(file)
                file.close()
                ImageModel(weights, hasGPUSupport)
            }
        }

    }
}
