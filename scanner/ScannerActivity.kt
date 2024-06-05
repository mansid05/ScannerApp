package com.example.scanner

import android.Manifest.permission.CAMERA
import android.Manifest.permission.VIBRATE
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import eu.livotov.labs.android.camview.ScannerLiveView
import eu.livotov.labs.android.camview.scanner.decoder.zxing.ZXDecoder

class ScannerActivity : Activity() {
    private lateinit var camera: ScannerLiveView
    private lateinit var scannedTV: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)

        if (checkPermission()) {
            Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show()
        } else {
            requestPermission()
        }

        scannedTV = findViewById(R.id.idTVscanned)
        camera = findViewById(R.id.camview)

        camera.setScannerViewEventListener(object : ScannerLiveView.ScannerViewEventListener {
            override fun onScannerStarted(scanner: ScannerLiveView) {
                Toast.makeText(this@ScannerActivity, "Scanner Started", Toast.LENGTH_SHORT).show()
            }

            override fun onScannerStopped(scanner: ScannerLiveView) {
                Toast.makeText(this@ScannerActivity, "Scanner Stopped", Toast.LENGTH_SHORT).show()
            }

            override fun onScannerError(err: Throwable) {
                Toast.makeText(this@ScannerActivity, "Scanner Error: ${err.message}", Toast.LENGTH_SHORT).show()
            }

            override fun onCodeScanned(data: String) {
                scannedTV.text = data
            }
        })
    }

    override fun onResume() {
        super.onResume()
        val decoder = ZXDecoder()
        decoder.scanAreaPercent = 0.8
        camera.decoder = decoder
        camera.startScanner()
    }

    override fun onPause() {
        camera.stopScanner()
        super.onPause()
    }

    private fun checkPermission(): Boolean {
        val cameraPermission = ContextCompat.checkSelfPermission(applicationContext, CAMERA)
        val vibratePermission = ContextCompat.checkSelfPermission(applicationContext, VIBRATE)
        return cameraPermission == PackageManager.PERMISSION_GRANTED && vibratePermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        val PERMISSION_REQUEST_CODE = 200
        ActivityCompat.requestPermissions(this, arrayOf(CAMERA, VIBRATE), PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == 200 && grantResults.isNotEmpty()) {
            val cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
            val vibrateAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
            if (cameraAccepted && vibrateAccepted) {
                Toast.makeText(this, "Permission granted..", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission Denied. You cannot use app without providing permission", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
