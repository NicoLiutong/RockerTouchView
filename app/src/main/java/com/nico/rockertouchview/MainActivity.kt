package com.nico.rockertouchview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import java.math.RoundingMode
import java.text.DecimalFormat

class MainActivity : AppCompatActivity(),RockerTouchView.RockerTouchViewListener {

    private lateinit var view: RockerTouchView
    private lateinit var angleText: TextView
    private lateinit var anglePercentText: TextView
    private lateinit var directionText: TextView
    private lateinit var directionPercentText: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        view = findViewById(R.id.rockerTouchView)
        angleText = findViewById(R.id.angleText)
        anglePercentText = findViewById(R.id.anglePercentText)
        directionText = findViewById(R.id.directionText)
        directionPercentText = findViewById(R.id.directionPercentText)
        view.setRockerTouchViewListener(this)

    }

    override fun onAllChange(angle: Int, percent: Float) {
        angleText.text = angle.toString()
        anglePercentText.text = getTwoDigits(percent)

    }

    override fun onFourChange(direction: RockerTouchView.Direction, percent: Float) {
        directionText.text = direction.toString()
        directionPercentText.text = getTwoDigits(percent)
    }

    private fun getTwoDigits(number: Float): String{
        val format = DecimalFormat("0.##")
        format.roundingMode = RoundingMode.FLOOR
        return format.format(number)
    }
}