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

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        printMessage("NFC supported: ${(nfcAdapter != null)}\nenabled: ${(nfcAdapter?.isEnabled)}")

    }

    override fun onResume() {
        super.onResume()

        NfcAdapter.getDefaultAdapter(this)?.let { nfcAdapter ->
            // An Intent to start your current Activity. Flag to singleTop
            // to imply that it should only be delivered to the current
            // instance rather than starting a new instance of the Activity.
            val launchIntent = Intent(this, this.javaClass)
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

            // Supply this launch intent as the PendingIntent, set to cancel
            // one if it's already in progress. It never should be.
            val pendingIntent = PendingIntent.getActivity(
                this, 0, launchIntent, PendingIntent.FLAG_CANCEL_CURRENT
            )

            // Define your filters and desired technology types
            val filters = arrayOf(IntentFilter(ACTION_TAG_DISCOVERED))
            val techTypes = arrayOf(arrayOf(IsoDep::class.java.name))

            // And enable your Activity to receive NFC events. Note that there
            // is no need to manually disable dispatch in onPause() as the system
            // very strictly performs this for you. You only need to disable
            // dispatch if you don't want to receive tags while resumed.
            nfcAdapter.enableForegroundDispatch(
                this, pendingIntent, filters, techTypes
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        printMessage(intent.action ?: "Undefined")
        if (ACTION_TAG_DISCOVERED == intent.action) {

            val rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            var ndefMsg = rawMessages[0] as NdefMessage

            var text = ndefMsg.records.map { record -> record.toUri() ?: String(record.payload) }.joinToString("\n")
            printMessage(text)
        }
    }

    fun sendNotification(view: View) {
        printMessage("Geklickt")
    }

    fun printMessage(text: String) {
        val textView: TextView = findViewById(R.id.label) as TextView
        textView.text = text
    }
}
