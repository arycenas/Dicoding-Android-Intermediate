package com.intermediate.storyapp.utility

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val FILE_FORMAT = "yyyy-MM-dd"
private const val MAXIMUM_SIZE = 1000000
private val timestamp: String = SimpleDateFormat(FILE_FORMAT, Locale.US).format(Date())

private fun getImageUriForPreQ(context: Context): Uri {
    val fileDirectory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val file = File(fileDirectory, "$timestamp.jpg")

    if (!file.exists()) {
        file.createNewFile()
    }

    return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
}

fun getImageUri(context: Context): Uri {
    var uri: Uri? = null
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val contentValue = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$timestamp.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/MyCamera/")
        }

        uri = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValue
        )
    }
    return uri ?: getImageUriForPreQ(context)
}

fun createTempFile(context: Context): File {
    val fileDirectory = context.externalCacheDir
    return File.createTempFile(
        timestamp, ".jpg", fileDirectory
    )
}

fun uriToFile(uri: Uri, context: Context): File {
    val file = createTempFile(context)
    val inputStream = context.contentResolver.openInputStream(uri) as InputStream
    val outputStream = FileOutputStream(file)
    val buffer = ByteArray(1024)
    var length: Int

    while (inputStream.read(buffer).also {
            length = it
        } > 0) outputStream.write(buffer, 0, length)
    outputStream.close()
    inputStream.close()

    return file
}

@RequiresApi(Build.VERSION_CODES.Q)
fun File.reduceFileImage(): File {
    val file = this
    val bitmap = BitmapFactory.decodeFile(file.path)
    bitmap.getRotatedBitmap(file)
    var compressQuality = 100
    var streamLength: Int
    do {
        val bmpStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -= 5
    }while (streamLength > MAXIMUM_SIZE)
    bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
    return file
}

@RequiresApi(Build.VERSION_CODES.Q)
fun Bitmap.getRotatedBitmap(file: File): Bitmap? {
    val orientation = ExifInterface(file).getAttributeInt(
        ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED
    )
    return when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(this, 90F)
        ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(this, 180F)
        ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(this, 270F)
        ExifInterface.ORIENTATION_NORMAL -> this
        else -> this
    }
}


fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
    val matrix = Matrix()
    matrix.postRotate(angle)
    return Bitmap.createBitmap(
        source,0,0,source.width, source.height, matrix, true
    )
}