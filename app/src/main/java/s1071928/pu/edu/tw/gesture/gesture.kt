package s1071928.pu.edu.tw.gesture

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.Bitmap.createBitmap
import android.media.Image
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.Size
import android.view.TextureView
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions
import kotlinx.android.synthetic.main.activity_gesture.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_watch.*
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


typealias LumaListener = (luma: Double, image: ImageProxy) -> Unit


class Gesture : AppCompatActivity() {

    private lateinit var cameraExecutor: ExecutorService

    //private var cameraExecutor = Executors.newSingleThreadExecutor()

    lateinit var bitmap: Bitmap
    lateinit var pager: ViewPager
    val images: IntArray = intArrayOf(R.drawable.no, R.drawable.hi)
    val backG: IntArray = intArrayOf(
        R.drawable.bg01, R.drawable.bg02,
        R.drawable.bg03, R.drawable.bg04, R.drawable.bg05, R.drawable.bg06,
        R.drawable.bg07, R.drawable.bg08, R.drawable.bg09, R.drawable.bg10,
        R.drawable.bg11, R.drawable.bg12, R.drawable.bg13, R.drawable.bg14,
        R.drawable.bg15, R.drawable.bg16
    )

    lateinit var previewView: PreviewView

    // Accurate pose detector on static images, when depending on the pose-detection-accurate sdk
    val options = AccuratePoseDetectorOptions.Builder()
        .setDetectorMode(AccuratePoseDetectorOptions.STREAM_MODE)
        .build()
    val poseDetector = PoseDetection.getClient(options)

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 10
    }

    private lateinit var textureView: TextureView

    fun success() {
        // Task completed successfully
        Toast.makeText(baseContext, "偵測成功", Toast.LENGTH_LONG).show()
        val alertDialog: android.app.AlertDialog.Builder =
            android.app.AlertDialog.Builder(this@Gesture)
        alertDialog.setTitle("辨識結果")
        alertDialog.setMessage("辨識成功")
        alertDialog.setPositiveButton("確定",
            DialogInterface.OnClickListener { dialog, which ->
                Toast.makeText(
                    baseContext,
                    "確定",
                    Toast.LENGTH_SHORT
                ).show()
                intent = Intent(this@Gesture, Zoo::class.java)
                intent.putExtra("vis", "VISIBLE")
                startActivity(intent)
            })
        alertDialog.setNeutralButton("取消") { dialog: DialogInterface?, which: Int -> ("取消") }
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    fun render(pose: Pose) {

        if (pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER) == null) {
            return
        }

        val dstBitmap = createBitmap(
            bitmap.width, bitmap.height,
            Bitmap.Config.ARGB_8888
        )
        var canvas = Canvas(dstBitmap)

        //繪製原始圖片
        canvas.drawBitmap(bitmap, 0f, 0f, null)

        //設定畫筆
        val paint = Paint()
        paint.color = Color.RED
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 10f

        //Left
        val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
        val leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
        val leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)
        val leftPinky = pose.getPoseLandmark(PoseLandmark.LEFT_PINKY)
        val leftIndex = pose.getPoseLandmark(PoseLandmark.LEFT_INDEX)
        val leftThumb = pose.getPoseLandmark(PoseLandmark.LEFT_THUMB)

        //Right
        val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
        val rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
        val rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)
        val rightPinky = pose.getPoseLandmark(PoseLandmark.RIGHT_PINKY)
        val rightIndex = pose.getPoseLandmark(PoseLandmark.RIGHT_INDEX)
        val rightThumb = pose.getPoseLandmark(PoseLandmark.RIGHT_THUMB)

        /*val left = arrayOf(leftShoulder, leftElbow, leftWrist, leftPinky, leftIndex, leftThumb)
        val right = arrayOf(rightShoulder, rightElbow, rightWrist, rightPinky, rightIndex, rightThumb)*/

        var xShouder = leftShoulder.position.x
        var yShouder = leftShoulder.position.y
        var xElbow = leftElbow.position.x
        var yElbow = leftElbow.position.y
        var xWrist = leftWrist.position.x
        var yWrist = leftWrist.position.y
        var xIndex = leftIndex.position.x
        var yIndex = leftIndex.position.y
        var xThumb = leftThumb.position.x
        var yThumb = leftThumb.position.y
        var xPinky = leftPinky.position.x
        var yPinky = leftPinky.position.y

        canvas.drawCircle(xShouder, yShouder, 10f, paint)
        canvas.drawCircle(xElbow, yElbow, 10f, paint)
        canvas.drawCircle(xWrist, yWrist, 10f, paint)
        canvas.drawCircle(xIndex, yIndex, 10f, paint)
        canvas.drawCircle(xThumb, yThumb, 10f, paint)
        canvas.drawCircle(xPinky, yPinky, 10f, paint)

        canvas.drawLine(xShouder, yShouder, xElbow, yElbow, paint)
        canvas.drawLine(xElbow, yElbow, xWrist, yWrist, paint)
        canvas.drawLine(xWrist, yWrist, xIndex, yIndex, paint)
        canvas.drawLine(xWrist, yWrist, xThumb, yThumb, paint)
        canvas.drawLine(xWrist, yWrist, xPinky, yPinky, paint)

        var xShouder1 = rightShoulder.position.x
        var yShouder1 = rightShoulder.position.y
        //canvas.drawCircle(xShouder, yShouder, 10f, paint)
        var xElbow1 = rightElbow.position.x
        var yElbow1 = rightElbow.position.y
        var xWrist1 = rightWrist.position.x
        var yWrist1 = rightWrist.position.y
        var xPinky1 = rightPinky.position.x
        var yPinky1 = rightPinky.position.y
        var xIndex1 = rightIndex.position.x
        var yIndex1 = rightIndex.position.y
        var xThumb1 = rightThumb.position.x
        var yThumb1 = rightThumb.position.y

        canvas.drawCircle(xShouder1, yShouder1, 10f, paint)
        canvas.drawCircle(xElbow1, yElbow1, 10f, paint)
        canvas.drawCircle(xWrist1, yWrist1, 10f, paint)
        canvas.drawCircle(xIndex1, yIndex1, 10f, paint)
        canvas.drawCircle(xThumb1, yThumb1, 10f, paint)
        canvas.drawCircle(xPinky1, yPinky1, 10f, paint)

        canvas.drawLine(xShouder1, yShouder1, xElbow1, yElbow1, paint)
        canvas.drawLine(xElbow1, yElbow1, xWrist1, yWrist1, paint)
        canvas.drawLine(xWrist1, yWrist1, xIndex1, yIndex1, paint)
        canvas.drawLine(xWrist1, yWrist1, xThumb1, yThumb1, paint)
        canvas.drawLine(xWrist1, yWrist1, xPinky1, yPinky1, paint)

        //img.setImageBitmap(null);
        img.setImageBitmap(dstBitmap)

        /*val allPoseLandmarks = pose.getAllPoseLandmarks()
        for (item in allPoseLandmarks) {
            val xPos = item.position.x
            val yPos = item.position.y
            canvas.drawCircle(xPos, yPos, 10f, paint)
        }

        img.setImageBitmap(dstBitmap)*/

        /*fun drawLine(
            canvas: Canvas,
            startLandmark: PoseLandmark?,
            endLandmark: PoseLandmark?,
            paint: Paint
        ){}

        drawLine(canvas, leftShoulder, leftElbow, paint)
        drawLine(canvas, leftElbow, leftWrist, paint)*/
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gesture)

        cameraExecutor = Executors.newSingleThreadExecutor()

        var animal01 = intent.getIntExtra("animal", 0)
        bg.setBackgroundResource(backG[animal01])

        previewView = findViewById(R.id.viewFinder)
        textureView = findViewById(R.id.texture_view)

        // Request camera permissions
        if (isCameraPermissionGranted()) {
            textureView.post { startCamera() }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION
            )
        }

        btnpose.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                intent.setClass(this@Gesture, Gesture::class.java)
                startActivity(intent)
            }
        })

        //將drawable的圖片轉換成Bitmap

        bitmap = BitmapFactory.decodeResource(getResources(), images[0])

        img.setImageBitmap(bitmap)

        //Prepare the input image
        var image = InputImage.fromBitmap(bitmap, 0)

        var p = 0
        val imlength = images.size


        btnpose.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                /*
                poseDetector.process(image)
                    .addOnSuccessListener { pose ->
                        success()
                        render(pose)
                    }
                    .addOnFailureListener { e ->
                        // Task failed with an exception
                        Toast.makeText(
                            baseContext, "抱歉，發生錯誤！", Toast.LENGTH_SHORT
                        ).show()
                    }

                 */
            }
        })


    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun startCamera() {

        /*
        val previewConfig = PreviewConfig.Builder()
            // We want to show input from back camera of the device
            .setLensFacing(CameraX.LensFacing.BACK)
            .build()

        val preview = Preview(previewConfig)

        preview.setOnPreviewOutputUpdateListener { previewOutput ->
            textureView.setSurfaceTexture(previewOutput.surfaceTexture)
        }

        CameraX.bindToLifecycle(this as LifecycleOwner, preview)

         */


        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

            var metrics = DisplayMetrics().also { viewFinder.display.getRealMetrics(it) }
            val imageAnalyzer = ImageAnalysis.Builder()
                .setTargetResolution(Size(metrics.widthPixels, metrics.heightPixels))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalyzer.setAnalyzer(cameraExecutor,  { imageProxy ->
                //Log.d("[COUNT]", "Average luminosity: $luma " + image.image)

                val image: Image? = imageProxy.image


                image?.let {
                    val inputImage = InputImage
                        .fromMediaImage(it, imageProxy.imageInfo.rotationDegrees)

                    poseDetector.process(inputImage)
                        .addOnSuccessListener { pose ->
                            //success()
                            render(pose)
                            Log.w("[DECTER]", "OK")

                        }
                        .addOnFailureListener { e ->
                            // Task failed with an exception
                            Toast.makeText(
                                baseContext, "001 抱歉，發生錯誤！", Toast.LENGTH_SHORT
                            ).show()

                            Log.w("[ERR]", e.toString())

                            imageProxy.close()

                        }.addOnCompleteListener {
                            imageProxy.close()
                        }
                }
            })


            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, imageAnalyzer, preview
                )

            } catch (exc: Exception) {
                Log.e("Use case binding failed", exc.toString())
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun isCameraPermissionGranted(): Boolean {
        val selfPermission =
            ContextCompat.checkSelfPermission(baseContext, Manifest.permission.CAMERA)
        return selfPermission == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (isCameraPermissionGranted()) {
                textureView.post { startCamera() }
            } else {
                Toast.makeText(this, "Camera permission is required.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun imageProxyToBitmap(image: ImageProxy): Bitmap? {
        val planeProxy = image.planes[0]
        val buffer = planeProxy.buffer
        val bytes = ByteArray(buffer.remaining())
        buffer[bytes]
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

}

typealias ImageProxyListener = (bmp: Bitmap) -> Unit

private class ImageAnalyzer(ctx: Context, private val listener: ImageProxyListener) :
    ImageAnalysis.Analyzer {
    /**
     * Convert Image Proxy to Bitmap
     */
    private val yuvToRgbConverter = YuvToRgbConverter(ctx)
    private lateinit var bitmapBuffer: Bitmap
    private lateinit var rotationMatrix: Matrix

    override fun analyze(imageProxy: ImageProxy) {
        // Convert Image to Bitmap
        val bmp:Bitmap? = toBitmap(imageProxy)

        if (bmp != null) {
            listener(bmp)
        }
        // Close the image,this tells CameraX to feed the next image to the analyzer
        imageProxy.close()
    }

    @SuppressLint("UnsafeExperimentalUsageError", "UnsafeOptInUsageError")
    private fun toBitmap(imageProxy: ImageProxy): Bitmap? {
        val image = imageProxy.image ?: return null
        // Initialise Buffer
        if (!::bitmapBuffer.isInitialized) {
            // The image rotation and RGB image buffer are initialized only once
            rotationMatrix = Matrix()
            rotationMatrix.postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
            bitmapBuffer = Bitmap.createBitmap(
                imageProxy.width, imageProxy.height, Bitmap.Config.ARGB_8888
            )
        }

        // Pass image to an image analyser
        yuvToRgbConverter.yuvToRgb(image, bitmapBuffer)

        // Create the Bitmap in the correct orientation
        return Bitmap.createBitmap(bitmapBuffer, 0, 0,
            bitmapBuffer.width, bitmapBuffer.height, rotationMatrix, false
        )
    }
}



    private class LuminosityAnalyzer(private val listener: LumaListener) : ImageAnalysis.Analyzer {

    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()    // Rewind the buffer to zero
        val data = ByteArray(remaining())
        get(data)   // Copy the buffer into a byte array
        return data // Return the byte array
    }

    override fun analyze(image: ImageProxy) {

        val buffer = image.planes[0].buffer
        val data = buffer.toByteArray()
        val pixels = data.map { it.toInt() and 0xFF }
        val luma = pixels.average()



        listener(luma, image)

        image.close()
    }
}
