package s1071928.pu.edu.tw.gesture

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatCallback
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_start.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                intent = Intent(this@MainActivity, GameStart::class.java)
                startActivity(intent)
            }
        })
        btn_zoo.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                intent = Intent(this@MainActivity, Zoo::class.java)
                startActivity(intent)
            }
        })
        var txv: TextView = findViewById(R.id.txv)
        txv.text = "森林王國"
                txv.setTypeface(Typeface.createFromAsset(assets,
            "font/HanyiSenty Candy-color-mono.ttf"))
              }
}

