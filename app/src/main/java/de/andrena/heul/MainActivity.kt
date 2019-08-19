package de.andrena.heul

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.NfcAdapter.ACTION_NDEF_DISCOVERED
import android.nfc.NfcAdapter.ACTION_TAG_DISCOVERED
import android.nfc.Tag
import android.nfc.tech.IsoDep
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import khttp.get
import registeredTag
import android.os.StrictMode
import khttp.post


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        setContentView(R.layout.activity_main)

        var nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        printMessage("NFC supported: ${(nfcAdapter != null)}\nenabled: ${(nfcAdapter?.isEnabled)}")

    }

    override fun onResume() {
        super.onResume()

        NfcAdapter.getDefaultAdapter(this)?.let { nfcAdapter ->
            val launchIntent = Intent(this, this.javaClass)
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

            val pendingIntent = PendingIntent.getActivity(
                this, 0, launchIntent, PendingIntent.FLAG_CANCEL_CURRENT
            )

            val filters = arrayOf(IntentFilter(ACTION_TAG_DISCOVERED))
            val techTypes = arrayOf(arrayOf(IsoDep::class.java.name))

            nfcAdapter.enableForegroundDispatch(
                this, pendingIntent, filters, techTypes
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        if (ACTION_TAG_DISCOVERED == intent.action) {

            val rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            val ndefMsg = rawMessages[0] as NdefMessage

            val text = ndefMsg.records.map { record -> record.toUri() ?: String(record.payload) }.joinToString("\n")
            if (text == registeredTag) {
                checkInOrOut()
            } else {
                Toast.makeText(this, "Falsche Tag", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun checkInOrOut() {
        var currentState = get("http://10.89.1.228:8080/stempel")

        if (currentState.text == "eingestempelt") {
            Toast.makeText(this, "Stemple aus", Toast.LENGTH_LONG).show()
//            post("https://jsonplaceholder.typicode.com/todos/122", data = "{\"name\":\"Nase\"}")
        } else {
            Toast.makeText(this, "Stemple ein", Toast.LENGTH_LONG).show()
//            post("https://jsonplaceholder.typicode.com/todos/122", data = "{\"name\":\"Nase\"}")
        }
//        Toast.makeText(this, currentState.text, Toast.LENGTH_LONG).show()
    }

    fun openRegisterTagActivity(view: View) {
        val intent = Intent(this, Registerer::class.java)
        startActivity(intent)
    }

    fun printMessage(text: String) {
        val textView: TextView = findViewById(R.id.label) as TextView
        textView.text = text
    }
}
