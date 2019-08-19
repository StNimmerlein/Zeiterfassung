package de.andrena.heul

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.tech.IsoDep
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import registeredTag

class Registerer : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registerer)
    }

    override fun onResume() {
        super.onResume()

        NfcAdapter.getDefaultAdapter(this)?.let { nfcAdapter ->
            val launchIntent = Intent(this, this.javaClass)
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

            val pendingIntent = PendingIntent.getActivity(
                this, 0, launchIntent, PendingIntent.FLAG_CANCEL_CURRENT
            )

            val filters = arrayOf(IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED))
            val techTypes = arrayOf(arrayOf(IsoDep::class.java.name))

            nfcAdapter.enableForegroundDispatch(
                this, pendingIntent, filters, techTypes
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        if (NfcAdapter.ACTION_TAG_DISCOVERED == intent.action) {

            val rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            val ndefMsg = rawMessages[0] as NdefMessage

            val text = ndefMsg.records.map { record -> record.toUri() ?: String(record.payload) }.joinToString("\n")
            registeredTag = text
            finish()
        }
    }

    fun cancel(view: View) {
        finish()
    }
}
