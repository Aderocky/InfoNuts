package com.example.nuts.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.nuts.ui.theme.NutPrimaryDark
import com.example.nuts.ui.theme.NutPrimaryLight

@Composable
fun LogoutConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
){
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Logout",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Text("Are you sure you want to log out?", color = Color.White, fontWeight = FontWeight.SemiBold)
        },
        confirmButton = {
            Button(onClick = onConfirm, colors = ButtonDefaults.buttonColors(NutPrimaryDark)) {
                Text("Yes", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(NutPrimaryLight)) {
                Text("No",color = Color.White,fontWeight = FontWeight.Bold)
            }
        }
    )
}