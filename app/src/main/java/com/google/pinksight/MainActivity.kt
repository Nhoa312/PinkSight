
package com.google.pinksight

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.custom.CustomImageLabelerOptions
import java.io.IOException

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val localModel = LocalModel.Builder()
            .setAssetFilePath("pinksight_classifier_metadata.tflite")
            .build()
        val img: ImageView = findViewById(R.id.imageToLabel)
        val fileName = "malignant (101).png"
        val bitmap: Bitmap? = assetsToBitmap(fileName)
        bitmap?.apply {
            img.setImageBitmap(this)
        }
        val txtOutput : TextView = findViewById(R.id.txtOutput)
        val btn: Button = findViewById(R.id.btnTest)
        btn.setOnClickListener {
            val options = CustomImageLabelerOptions.Builder(localModel)
                .setConfidenceThreshold(0.0f)
                .setMaxResultCount(5)
                .build()
            val labeler = ImageLabeling.getClient(options)
            val image = InputImage.fromBitmap(bitmap!!, 0)
            var outputText = ""
            labeler.process(image)
                .addOnSuccessListener { labels ->
                    for (label in labels) {
                        val text = label.text
                        val confidence = label.confidence
                        outputText += "$text : $confidence\n"
                    }
                    txtOutput.text = outputText
                }
                .addOnFailureListener { e ->

                }
        }

    }
}
fun Context.assetsToBitmap(fileName: String): Bitmap?{
    return try {
        with(assets.open(fileName)){
            BitmapFactory.decodeStream(this)
        }
    } catch (e: IOException) { null }
}