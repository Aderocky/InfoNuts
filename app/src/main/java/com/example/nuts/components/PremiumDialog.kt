package com.example.nuts.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun PremiumDialog(
    onOpenWhatsApp: () -> Unit,
    onDismiss: () -> Unit
){
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Upgrade To Premium",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Text("Untuk mengakses beberapa fitur-fitur yang terkunci, kamu dapat upgrade aplikasi ke " +
                    "premium dengan mentransfer ke rekening BCA 1160502955 dengan harga Rp30.000/Bulan\n\n" +
                    "Kamu dapat mengirimkan bukti pembayaran kepada Admin lewat Whatsapp untuk diverifikasi")
        },
        confirmButton = {
            Button(onClick = onOpenWhatsApp, colors = ButtonDefaults.buttonColors(Color(0xFF25D366))) {
                Text("WhatsApp")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Okay")
            }
        }
    )
}