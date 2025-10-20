package com.example.nuts.clasifications

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import com.example.nuts.utils.nuts
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import androidx.core.graphics.scale

class Classifier(private val context: Context) {

    private var interpreter: Interpreter? = null
    private val inputSize = 224
    private val labels = nuts

    init {
        interpreter = Interpreter(loadModelFile("MrBeansBest.tflite"))
    }
    fun classify(bitmap: Bitmap, start: Long): ClassificationResult? {
        val resized = bitmap.scale(inputSize, inputSize)

        val inputBuffer = ByteBuffer.allocateDirect(4 * inputSize * inputSize * 3)
        inputBuffer.order(ByteOrder.nativeOrder())

        val intValues = IntArray(inputSize * inputSize)
        resized.getPixels(intValues, 0, resized.width, 0, 0, resized.width, resized.height)

        for (pixel in intValues) {
            val r = (pixel shr 16 and 0xFF) / 255.0f
            val g = (pixel shr 8 and 0xFF) / 255.0f
            val b = (pixel and 0xFF) / 255.0f
            inputBuffer.putFloat(r)
            inputBuffer.putFloat(g)
            inputBuffer.putFloat(b)
        }

        val outputBuffer = Array(1) { FloatArray(labels.size) }
        interpreter?.run(inputBuffer, outputBuffer)

        val confidences = outputBuffer[0]
        val maxIdx = confidences.indices.maxByOrNull { confidences[it] } ?: return null
        val elapsed = SystemClock.uptimeMillis() - start

        return ClassificationResult(
            label = labels[maxIdx],
            confidence = (confidences[maxIdx] * 100),
            timeMs = elapsed,
        )
    }
    private fun loadModelFile(modelName: String): MappedByteBuffer {
        val afd = context.assets.openFd(modelName)
        val inputStream = FileInputStream(afd.fileDescriptor)
        val fileChannel = inputStream.channel
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, afd.startOffset, afd.declaredLength)
    }
}


