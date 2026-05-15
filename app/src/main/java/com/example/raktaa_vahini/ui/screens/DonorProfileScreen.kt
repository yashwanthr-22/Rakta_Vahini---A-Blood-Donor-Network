package com.example.raktaa_vahini.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.raktaa_vahini.data.BloodGroup
import com.example.raktaa_vahini.data.Donor
import com.example.raktaa_vahini.ui.viewmodel.DonorViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonorProfileScreen(viewModel: DonorViewModel, onLogout: () -> Unit) {
    val donor by viewModel.currentDonor.collectAsState()
    var isEditing by remember { mutableStateOf(false) }
    
    var name by remember { mutableStateOf("") }
    var selectedGroup by remember { mutableStateOf(BloodGroup.O_POSITIVE) }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var pincode by remember { mutableStateOf("") }
    var isReady by remember { mutableStateOf(true) }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(donor) {
        donor?.let {
            name = it.name
            selectedGroup = it.bloodGroup
            email = it.email
            phoneNumber = it.phoneNumber
            area = it.area
            district = it.district
            pincode = it.pincode.toString()
            isReady = it.isReadyToDonate
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "My Donor Profile", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        if (!isEditing) {
            // Display only Name and Blood Group
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Name: $name", style = MaterialTheme.typography.titleLarge)
                    Text(text = "Blood Group: ${selectedGroup.displayName}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { isEditing = true }, modifier = Modifier.fillMaxWidth()) {
                Text("Edit Profile Details")
            }
        } else {
            // Edit mode: Show all details
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedGroup.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Blood Group") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable).fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    BloodGroup.entries.forEach { group ->
                        DropdownMenuItem(
                            text = { Text(group.displayName) },
                            onClick = {
                                selectedGroup = group
                                expanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = area,
                onValueChange = { area = it },
                label = { Text("Area / Village") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = district,
                onValueChange = { district = it },
                label = { Text("District") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = pincode,
                onValueChange = { if (it.all { char -> char.isDigit() }) pincode = it },
                label = { Text("Pincode") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Ready to Donate Now", modifier = Modifier.weight(1f))
                Switch(checked = isReady, onCheckedChange = { isReady = it })
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = {
                    viewModel.updateProfile(
                        (donor ?: Donor()).copy(
                            name = name,
                            bloodGroup = selectedGroup,
                            email = email,
                            phoneNumber = phoneNumber,
                            area = area,
                            district = district,
                            pincode = pincode,
                            isReadyToDonate = isReady
                        )
                    )
                    isEditing = false
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Changes")
            }
            
            TextButton(onClick = { isEditing = false }, modifier = Modifier.fillMaxWidth()) {
                Text("Cancel")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        donor?.lastDonationDate?.let { date ->
            val formattedDate = Instant.ofEpochMilli(date)
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
            Text(text = "Last Donation: $formattedDate", style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = Modifier.height(24.dp))

        val isEligible = donor?.isEligible ?: true

        Button(
            onClick = { viewModel.logDonation(donor ?: Donor()) },
            modifier = Modifier.fillMaxWidth(),
            enabled = isEligible,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text("LOG NEW DONATION")
        }

        if (!isEligible) {
            Text(
                text = "Next eligible donation after 90 days from last donation",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Donation History",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Start)
        )
        
        donor?.donationHistory?.sortedDescending()?.forEach { timestamp ->
            val date = Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm"))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = "Donated on: $date",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedButton(
            onClick = { viewModel.logout(onLogout) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Logout")
        }
    }
}
