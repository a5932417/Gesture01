package s1071928.pu.edu.tw.gesture

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_watch.*
import kotlinx.android.synthetic.main.activity_zoo.*

class Zoo : AppCompatActivity() {
    var buttons = arrayOfNulls<Button>(2)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buttons[0] = findViewById<Button>(R.id.an_01);
        buttons[1] = findViewById<Button>(R.id.an_02);

        setContentView(R.layout.activity_zoo)
        var intent = intent
        var name = intent.getIntExtra("animal",0)
        //if(an_01.visibility == Button.INVISIBLE)
            //an_01.visibility = Button.VISIBLE
        zoo_btnBack.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                intent = Intent(this@Zoo, MainActivity::class.java)
                startActivity(intent)
            }
        })
        var txv3: TextView = findViewById(R.id.txv3)
        txv3.text = "動物園"
        txv3.setTypeface(
            Typeface.createFromAsset(assets,
                "font/HanyiSenty Candy-color-mono.ttf"))
    }
}