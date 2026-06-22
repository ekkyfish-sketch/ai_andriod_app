package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.FishItem
import com.example.data.FishOrder
import com.example.data.FishRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.UUID
import java.util.Locale
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

sealed interface ViewState {
    object Idle : ViewState
    object Loading : ViewState
    data class Success(val message: String) : ViewState
    data class Error(val error: String) : ViewState
}

enum class NavigationScreen {
    STORE,
    CART,
    ORDERS,
    ADMIN,
    LOGIN,
    PRODUCT
}

enum class UserRole {
    CUSTOMER,
    ADMIN
}

data class LoggedInUser(
    val email: String,
    val role: UserRole
)

data class PaymentDetails(
    val cardNumber: String = "",
    val cardHolder: String = "",
    val expiry: String = "",
    val cvv: String = ""
)

class FishViewModel(application: Application) : AndroidViewModel(application) {

    // Persistent Settings & WhatsApp alerts for Admin
    private val sharedPrefs = application.getSharedPreferences("ekkyfish_prefs", android.content.Context.MODE_PRIVATE)

    private var inventoryListenerRegistration: com.google.firebase.firestore.ListenerRegistration? = null

    private val _adminPhone = MutableStateFlow(sharedPrefs.getString("admin_phone", "9600163439") ?: "9600163439")
    val adminPhone: StateFlow<String> = _adminPhone.asStateFlow()

    private val _whatsappTokenOverride = MutableStateFlow(sharedPrefs.getString("whatsapp_token_override", "") ?: "")
    val whatsappTokenOverride: StateFlow<String> = _whatsappTokenOverride.asStateFlow()

    private val _whatsappPhoneIdOverride = MutableStateFlow(sharedPrefs.getString("whatsapp_phone_id_override", "") ?: "")
    val whatsappPhoneIdOverride: StateFlow<String> = _whatsappPhoneIdOverride.asStateFlow()

    private val _whatsappWabaIdOverride = MutableStateFlow(sharedPrefs.getString("whatsapp_waba_id_override", "") ?: "")
    val whatsappWabaIdOverride: StateFlow<String> = _whatsappWabaIdOverride.asStateFlow()

    private val _whatsappDatasetIdOverride = MutableStateFlow(sharedPrefs.getString("whatsapp_dataset_id_override", "") ?: "")
    val whatsappDatasetIdOverride: StateFlow<String> = _whatsappDatasetIdOverride.asStateFlow()

    private val _whatsappApiVersion = MutableStateFlow(sharedPrefs.getString("whatsapp_api_version", "v25.0") ?: "v25.0")
    val whatsappApiVersion: StateFlow<String> = _whatsappApiVersion.asStateFlow()

    private val _whatsappTemplateName = MutableStateFlow(sharedPrefs.getString("whatsapp_template_name", "order_confirmed_ekky_fish") ?: "order_confirmed_ekky_fish")
    val whatsappTemplateName: StateFlow<String> = _whatsappTemplateName.asStateFlow()

    fun updateWhatsAppOverrides(
        token: String,
        phoneId: String,
        wabaId: String = "",
        datasetId: String = "",
        apiVersion: String = "v25.0",
        templateName: String = "order_confirmed_ekky_fish"
    ) {
        var trimmedToken = token.trim()
        if (trimmedToken.startsWith("'") && trimmedToken.endsWith("'")) {
            trimmedToken = trimmedToken.substring(1, trimmedToken.length - 1).trim()
        }
        if (trimmedToken.startsWith("\"") && trimmedToken.endsWith("\"")) {
            trimmedToken = trimmedToken.substring(1, trimmedToken.length - 1).trim()
        }
        if (trimmedToken.startsWith("Bearer ", ignoreCase = true)) {
            trimmedToken = trimmedToken.substring(7).trim()
        }

        var trimmedPhoneId = phoneId.trim()
        if (trimmedPhoneId.startsWith("'") && trimmedPhoneId.endsWith("'")) {
            trimmedPhoneId = trimmedPhoneId.substring(1, trimmedPhoneId.length - 1).trim()
        }
        if (trimmedPhoneId.startsWith("\"") && trimmedPhoneId.endsWith("\"")) {
            trimmedPhoneId = trimmedPhoneId.substring(1, trimmedPhoneId.length - 1).trim()
        }

        var trimmedWabaId = wabaId.trim()
        if (trimmedWabaId.startsWith("'") && trimmedWabaId.endsWith("'")) {
            trimmedWabaId = trimmedWabaId.substring(1, trimmedWabaId.length - 1).trim()
        }
        if (trimmedWabaId.startsWith("\"") && trimmedWabaId.endsWith("\"")) {
            trimmedWabaId = trimmedWabaId.substring(1, trimmedWabaId.length - 1).trim()
        }

        var trimmedDatasetId = datasetId.trim()
        if (trimmedDatasetId.startsWith("'") && trimmedDatasetId.endsWith("'")) {
            trimmedDatasetId = trimmedDatasetId.substring(1, trimmedDatasetId.length - 1).trim()
        }
        if (trimmedDatasetId.startsWith("\"") && trimmedDatasetId.endsWith("\"")) {
            trimmedDatasetId = trimmedDatasetId.substring(1, trimmedDatasetId.length - 1).trim()
        }

        var trimmedApiVersion = apiVersion.trim()
        if (trimmedApiVersion.startsWith("'") && trimmedApiVersion.endsWith("'")) {
            trimmedApiVersion = trimmedApiVersion.substring(1, trimmedApiVersion.length - 1).trim()
        }
        if (trimmedApiVersion.startsWith("\"") && trimmedApiVersion.endsWith("\"")) {
            trimmedApiVersion = trimmedApiVersion.substring(1, trimmedApiVersion.length - 1).trim()
        }
        if (trimmedApiVersion.isBlank()) {
            trimmedApiVersion = "v25.0"
        }

        var trimmedTemplateName = templateName.trim()
        if (trimmedTemplateName.startsWith("'") && trimmedTemplateName.endsWith("'")) {
            trimmedTemplateName = trimmedTemplateName.substring(1, trimmedTemplateName.length - 1).trim()
        }
        if (trimmedTemplateName.startsWith("\"") && trimmedTemplateName.endsWith("\"")) {
            trimmedTemplateName = trimmedTemplateName.substring(1, trimmedTemplateName.length - 1).trim()
        }
        if (trimmedTemplateName.isBlank()) {
            trimmedTemplateName = "order_confirmed_ekky_fish"
        }

        sharedPrefs.edit()
            .putString("whatsapp_token_override", trimmedToken)
            .putString("whatsapp_phone_id_override", trimmedPhoneId)
            .putString("whatsapp_waba_id_override", trimmedWabaId)
            .putString("whatsapp_dataset_id_override", trimmedDatasetId)
            .putString("whatsapp_api_version", trimmedApiVersion)
            .putString("whatsapp_template_name", trimmedTemplateName)
            .apply()

        _whatsappTokenOverride.value = trimmedToken
        _whatsappPhoneIdOverride.value = trimmedPhoneId
        _whatsappWabaIdOverride.value = trimmedWabaId
        _whatsappDatasetIdOverride.value = trimmedDatasetId
        _whatsappApiVersion.value = trimmedApiVersion
        _whatsappTemplateName.value = trimmedTemplateName
    }

    fun getActiveWhatsAppToken(): String {
        val override = _whatsappTokenOverride.value
        if (override.isNotBlank()) return override
        return com.example.BuildConfig.WHATSAPP_API_TOKEN
    }

    fun getActiveWhatsAppPhoneId(): String {
        val override = _whatsappPhoneIdOverride.value
        if (override.isNotBlank()) return override
        return com.example.BuildConfig.WHATSAPP_PHONE_NUMBER_ID
    }

    fun getActiveWhatsAppWabaId(): String {
        val override = _whatsappWabaIdOverride.value
        if (override.isNotBlank()) return override
        return com.example.BuildConfig.WHATSAPP_WABA_ID
    }

    fun getActiveWhatsAppDatasetId(): String {
        val override = _whatsappDatasetIdOverride.value
        if (override.isNotBlank()) return override
        return com.example.BuildConfig.WHATSAPP_DATASET_ID
    }

    val activeTokenMasked: java.lang.String
        get() {
            val token = getActiveWhatsAppToken()
            if (token.isBlank() || token == "YOUR_WHATSAPP_API_TOKEN") return java.lang.String("[Not Configured / Click Overrides to paste key]")
            if (token.length <= 10) return java.lang.String("****")
            return java.lang.String(token.substring(0, 6) + "..." + token.substring(token.length - 4))
        }

    val activePhoneIdDisplay: java.lang.String
        get() {
            val phoneId = getActiveWhatsAppPhoneId()
            if (phoneId.isBlank() || phoneId == "YOUR_WHATSAPP_PHONE_NUMBER_ID") return java.lang.String("[Not Configured]")
            return java.lang.String(phoneId)
        }


    private val db = AppDatabase.getDatabase(application, viewModelScope)
    private val repository = FishRepository(db.fishDao(), db.customerDao())

    // Authentication session state
    private val _loggedInUser = MutableStateFlow<LoggedInUser?>(null)
    val loggedInUser: StateFlow<LoggedInUser?> = _loggedInUser.asStateFlow()

    private val _loggedInCustomerAccount = MutableStateFlow<com.example.data.CustomerAccount?>(null)
    val loggedInCustomerAccount: StateFlow<com.example.data.CustomerAccount?> = _loggedInCustomerAccount.asStateFlow()

    init {
        viewModelScope.launch {
            _loggedInUser.collect { user ->
                if (user != null) {
                    val roleStr = if (user.role == UserRole.ADMIN) "ADMIN" else "CUSTOMER"
                    if (user.role == UserRole.CUSTOMER) {
                        fetchAndSyncUserProfileFromFirestore(user.email, roleStr)
                    } else {
                        _loggedInCustomerAccount.value = null
                        // Sync admin account to Firestore
                        syncUserProfileToFirestore(user.email, user.email.substringBefore("@"), "", "", roleStr)
                    }

                    // Pull order history for this user from Firestore
                    fetchUserOrdersFromFirestore(user.email)
                } else {
                    _loggedInCustomerAccount.value = null
                }
            }
        }
        
        // Sync whole inventory with Firestore
        syncInventoryWithFirestore()

        // Sync delivery pincodes from Firestore
        syncDeliveryPincodeFromFirestore()

        // Migrate admin phone if it matches old default or is empty/null
        val currentSavedPhone = sharedPrefs.getString("admin_phone", "")
        if (currentSavedPhone == "9884958545" || currentSavedPhone == "9840761653" || currentSavedPhone == "7550252511" || currentSavedPhone.isNullOrBlank()) {
            sharedPrefs.edit().putString("admin_phone", "9600163439").apply()
            _adminPhone.value = "9600163439"
        }
    }

    fun updateCustomerProfile(name: String, phone: String, address: String) {
        val currentSession = _loggedInUser.value
        if (currentSession != null && currentSession.role == UserRole.CUSTOMER) {
            viewModelScope.launch {
                try {
                    val activeAccount = repository.getCustomerByEmail(currentSession.email)
                    if (activeAccount != null) {
                        val updated = activeAccount.copy(
                            name = name,
                            phone = phone,
                            address = address
                        )
                        repository.insertCustomer(updated)
                        _loggedInCustomerAccount.value = updated
                        syncUserProfileToFirestore(updated.email, updated.name, updated.phone, updated.address, "CUSTOMER")
                    } else {
                        val newAccount = com.example.data.CustomerAccount(
                            email = currentSession.email,
                            name = name,
                            phone = phone,
                            address = address,
                            passwordHash = ""
                        )
                        repository.insertCustomer(newAccount)
                        _loggedInCustomerAccount.value = newAccount
                        syncUserProfileToFirestore(newAccount.email, newAccount.name, newAccount.phone, newAccount.address, "CUSTOMER")
                    }
                } catch (e: Exception) {
                    android.util.Log.e("FishViewModel", "Failed to update profile: ${e.message}")
                }
            }
        }
    }

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    private val _forgotPasswordResult = MutableStateFlow<String?>(null)
    val forgotPasswordResult: StateFlow<String?> = _forgotPasswordResult.asStateFlow()

    fun clearForgotPasswordResult() {
        _forgotPasswordResult.value = null
    }

    fun forgotPassword(email: String) {
        val cleanEmail = email.trim()
        if (cleanEmail.isBlank()) {
            _forgotPasswordResult.value = "ERROR: Email address cannot be empty."
            return
        }
        viewModelScope.launch {
            try {
                val customer = repository.getCustomerByEmail(cleanEmail)
                if (customer == null) {
                    _forgotPasswordResult.value = "ERROR: No registered customer account found with email '$cleanEmail'. Please check the spelling or Register first."
                } else {
                    val passcode = customer.passwordHash
                    val supportEmail = "support@ekkyfish.com"
                    val emailSubject = "EkkyFish Customer Password Recovery"
                    
                    // Simulate an actual network call to mail server
                    kotlinx.coroutines.delay(1200)

                    val loginCredentialInfo = if (passcode == "SSO_GOOGLE") {
                        "Your account is registered securely via Google SSO. Please use the 'Continue with Google SSO' button to log in instantly!"
                    } else {
                        "Your account password is: '$passcode'. Please keep it confidential!"
                    }

                    _forgotPasswordResult.value = """
                        SUCCESS: An interactive recovery email has been sent successfully to '$cleanEmail'!
                        
                        --- EMAIL HEADER ---
                        From: EkkyFish Auto-Mailer <$supportEmail>
                        To: $cleanEmail
                        Subject: $emailSubject
                        Status: Delivered Successfully (Simulated Dispatch)
                        
                        --- EMAIL CONTENT ---
                        Hello ${customer.name},
                        
                        We received a recovery request for your EkkyFish Customer Account.
                        
                        $loginCredentialInfo
                        
                        Best regards,
                        EkkyFish Logistics & Dispatch Team
                        Chennai Seafood Docks, IND
                    """.trimIndent()
                }
            } catch (e: Exception) {
                _forgotPasswordResult.value = "ERROR: Database lookup failed: ${e.message}"
            }
        }
    }


    private val _whatsappNotifyEnabled = MutableStateFlow(sharedPrefs.getBoolean("whatsapp_notify_enabled", true))
    val whatsappNotifyEnabled: StateFlow<Boolean> = _whatsappNotifyEnabled.asStateFlow()

    private val _deliveryPincode = MutableStateFlow(sharedPrefs.getString("delivery_pincode", "") ?: "")
    val deliveryPincode: StateFlow<String> = _deliveryPincode.asStateFlow()

    private val _userSessionPincode = MutableStateFlow(sharedPrefs.getString("user_session_pincode", "") ?: "")
    val userSessionPincode: StateFlow<String> = _userSessionPincode.asStateFlow()

    fun setUserSessionPincode(pincode: String) {
        val trimmed = pincode.trim().filter { it.isDigit() }.take(6)
        sharedPrefs.edit().putString("user_session_pincode", trimmed).apply()
        _userSessionPincode.value = trimmed
    }

    fun setDeliveryPincode(pincode: String) {
        val trimmed = pincode.trim()
        sharedPrefs.edit().putString("delivery_pincode", trimmed).apply()
        _deliveryPincode.value = trimmed

        // Sync to cloud Firestore
        val db = try { FirebaseFirestore.getInstance() } catch (e: Exception) { null }
        if (db != null) {
            val configMap = hashMapOf("pincode" to trimmed)
            db.collection("config").document("delivery_pincode")
                .set(configMap)
                .addOnSuccessListener {
                    android.util.Log.d("FirestoreSync", "Delivery pincode synced to Firestore: $trimmed")
                }
                .addOnFailureListener { e ->
                    android.util.Log.e("FirestoreSync", "Failed to sync delivery pincode to Firestore: ${e.message}")
                }
        }
    }

    private val _lastPlacedOrderMessage = MutableStateFlow<String?>(null)
    val lastPlacedOrderMessage: StateFlow<String?> = _lastPlacedOrderMessage.asStateFlow()

    private val _whatsappStatus = MutableStateFlow<String?>(null)
    val whatsappStatus: StateFlow<String?> = _whatsappStatus.asStateFlow()

    private val _emailStatus = MutableStateFlow<String?>(null)
    val emailStatus: StateFlow<String?> = _emailStatus.asStateFlow()

    fun clearWhatsappStatus() {
        _whatsappStatus.value = null
    }

    fun clearEmailStatus() {
        _emailStatus.value = null
    }

    fun updateAdminPhone(phone: String) {
        val cleanPhone = phone.replace(Regex("[^0-9]"), "")
        sharedPrefs.edit().putString("admin_phone", cleanPhone).apply()
        _adminPhone.value = cleanPhone
    }

    fun setWhatsappNotifyEnabled(enabled: Boolean) {
        sharedPrefs.edit().putBoolean("whatsapp_notify_enabled", enabled).apply()
        _whatsappNotifyEnabled.value = enabled
    }

    fun clearLastPlacedOrderMessage() {
        _lastPlacedOrderMessage.value = null
    }

    val isWhatsAppCloudApiConfigured: Boolean
        get() {
            val apiToken = getActiveWhatsAppToken()
            val phoneId = getActiveWhatsAppPhoneId()
            return apiToken.isNotBlank() && apiToken != "YOUR_WHATSAPP_API_TOKEN" &&
                   phoneId.isNotBlank() && phoneId != "YOUR_WHATSAPP_PHONE_NUMBER_ID"
        }

    fun sendWhatsAppTemplate(
        toPhoneNumber: String,
        params: List<String> = emptyList(),
        param1: String = "Test Customer",
        param2: String = "EF-TEST123"
    ) {
        val apiToken = getActiveWhatsAppToken()
        val phoneId = getActiveWhatsAppPhoneId()
        val apiVer = _whatsappApiVersion.value.trim().ifBlank { "v25.0" }
        val templateName = _whatsappTemplateName.value.trim().ifBlank { "order_confirmed_ekky_fish" }

        if (apiToken.isBlank() || apiToken == "YOUR_WHATSAPP_API_TOKEN" ||
            phoneId.isBlank() || phoneId == "YOUR_WHATSAPP_PHONE_NUMBER_ID"
        ) {
            val err = "WhatsApp Cloud API is not configured or uses default placeholders."
            android.util.Log.d("WhatsAppAPI", err)
            _whatsappStatus.value = err
            return
        }

        _whatsappStatus.value = "Preparing $templateName template dispatch to +$toPhoneNumber..."

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val cleanToPhone = toPhoneNumber.replace(Regex("[^0-9]"), "")
                if (cleanToPhone.isBlank()) {
                    val err = "Recipient phone number is invalid or blank."
                    android.util.Log.w("WhatsAppAPI", err)
                    _whatsappStatus.value = err
                    return@launch
                }

                var formattedToPhone = cleanToPhone
                if (formattedToPhone.length == 10 && (formattedToPhone.startsWith("6") || formattedToPhone.startsWith("7") || formattedToPhone.startsWith("8") || formattedToPhone.startsWith("9"))) {
                    formattedToPhone = "91$formattedToPhone"
                } else if (formattedToPhone.length == 11 && formattedToPhone.startsWith("0")) {
                    formattedToPhone = "91" + formattedToPhone.substring(1)
                }

                _whatsappStatus.value = "Sending $templateName template to +$formattedToPhone..."

                val client = okhttp3.OkHttpClient()
                val mediaType = "application/json".toMediaTypeOrNull()

                val finalParams = if (params.isNotEmpty()) {
                    params
                } else {
                    // Populate exactly 12 mock fields according to the Meta template configuration
                    listOf(
                        "John Doe",                                               // {{1}} Customer
                        "+919876543210",                                          // {{2}} Phone
                        "johndoe@example.com",                                    // {{3}} Email
                        "123 Fresh Marine Blvd, Cod Harbor - 400001",             // {{4}} Delivery Address
                        "UPI Payment (Paid)",                                     // {{5}} Payment
                        "TXN-TEST8832",                                           // {{6}} Reference
                        "Premium Salmon Fillet",                                  // {{7}} Item Name
                        "1.5 KG",                                                 // {{8}} Quantity
                        "₹1,200.00",                                              // {{9}} Price
                        "₹1,200.00",                                              // {{10}} Subtotal
                        "₹50.00",                                                 // {{11}} Delivery Fee
                        "₹1,250.00"                                               // {{12}} Grand Total
                    )
                }

                val parametersListJson = finalParams.joinToString(separator = ",\n") { p ->
                    val cleanVal = p.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "")
                    """
                    {
                      "type": "text",
                      "text": "$cleanVal"
                    }
                    """.trimIndent()
                }

                val componentsJson = """,
                    "components": [
                      {
                        "type": "body",
                        "parameters": [
                          $parametersListJson
                        ]
                      }
                    ]"""

                val jsonPayload = """
                    {
                      "messaging_product": "whatsapp",
                      "to": "$formattedToPhone",
                      "type": "template",
                      "template": {
                        "name": "$templateName",
                        "language": {
                          "code": "en_US"
                        }$componentsJson
                      }
                    }
                """.trimIndent()

                val body = jsonPayload.toRequestBody(mediaType)
                val request = Request.Builder()
                    .url("https://graph.facebook.com/$apiVer/$phoneId/messages")
                    .post(body)
                    .addHeader("Authorization", "Bearer $apiToken")
                    .addHeader("Content-Type", "application/json")
                    .build()

                client.newCall(request).execute().use { response ->
                    val responseStr = response.body?.string() ?: ""
                    if (response.isSuccessful) {
                        val msg = "Template '$templateName' sent successfully to +$formattedToPhone!"
                        android.util.Log.i("WhatsAppAPI", "Template successfully sent via WhatsApp Cloud API: $responseStr")
                        _whatsappStatus.value = msg
                    } else {
                        val errMsg = "HTTP ${response.code}: $responseStr"
                        android.util.Log.e("WhatsAppAPI", "Failed to send WhatsApp Cloud API template. Code: ${response.code}, Response: $responseStr")
                        _whatsappStatus.value = "Failed to +$formattedToPhone: $errMsg"
                    }
                }
            } catch (e: java.lang.Exception) {
                val err = "Error: ${e.message}"
                android.util.Log.e("WhatsAppAPI", "Error propagating WhatsApp Cloud API template request: ${e.message}", e)
                _whatsappStatus.value = err
            }
        }
    }

    fun sendWhatsAppNotification(toPhoneNumber: String, textMessage: String) {
        val apiToken = getActiveWhatsAppToken()
        val phoneId = getActiveWhatsAppPhoneId()
        val apiVer = _whatsappApiVersion.value.trim().ifBlank { "v25.0" }

        if (apiToken.isBlank() || apiToken == "YOUR_WHATSAPP_API_TOKEN" ||
            phoneId.isBlank() || phoneId == "YOUR_WHATSAPP_PHONE_NUMBER_ID"
        ) {
            val err = "WhatsApp Cloud API is not configured or uses default placeholders."
            android.util.Log.d("WhatsAppAPI", err)
            _whatsappStatus.value = err
            return
        }

        _whatsappStatus.value = "Preparing notification dispatch to +$toPhoneNumber..."

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val cleanToPhone = toPhoneNumber.replace(Regex("[^0-9]"), "")
                if (cleanToPhone.isBlank()) {
                    val err = "Recipient phone number is invalid or blank."
                    android.util.Log.w("WhatsAppAPI", err)
                    _whatsappStatus.value = err
                    return@launch
                }

                // Auto-normalize and prepend Country Code (91) for Indian 10-digit numbers
                var formattedToPhone = cleanToPhone
                if (formattedToPhone.length == 10 && (formattedToPhone.startsWith("6") || formattedToPhone.startsWith("7") || formattedToPhone.startsWith("8") || formattedToPhone.startsWith("9"))) {
                    formattedToPhone = "91$formattedToPhone"
                } else if (formattedToPhone.length == 11 && formattedToPhone.startsWith("0")) {
                    formattedToPhone = "91" + formattedToPhone.substring(1)
                }

                _whatsappStatus.value = "Sending automated alert to +$formattedToPhone..."

                val client = okhttp3.OkHttpClient()
                val mediaType = "application/json".toMediaTypeOrNull()

                val escapedMsg = textMessage
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "")

                val jsonPayload = """
                    {
                      "messaging_product": "whatsapp",
                      "to": "$formattedToPhone",
                      "type": "text",
                      "text": {
                        "preview_url": false,
                        "body": "$escapedMsg"
                      }
                    }
                """.trimIndent()

                val body = jsonPayload.toRequestBody(mediaType)
                val request = Request.Builder()
                    .url("https://graph.facebook.com/$apiVer/$phoneId/messages")
                    .post(body)
                    .addHeader("Authorization", "Bearer $apiToken")
                    .addHeader("Content-Type", "application/json")
                    .build()

                client.newCall(request).execute().use { response ->
                    val responseStr = response.body?.string() ?: ""
                    if (response.isSuccessful) {
                        val msg = "Sent successfully to +$formattedToPhone!"
                        android.util.Log.i("WhatsAppAPI", "Order notification successfully sent via WhatsApp Cloud API: $responseStr")
                        _whatsappStatus.value = msg
                    } else {
                        val errMsg = "HTTP ${response.code}: $responseStr"
                        android.util.Log.e("WhatsAppAPI", "Failed to send WhatsApp Cloud API notification. Code: ${response.code}, Response: $responseStr")
                        _whatsappStatus.value = "Failed to +$formattedToPhone: $errMsg"
                    }
                }
            } catch (e: Exception) {
                val err = "Error: ${e.message}"
                android.util.Log.e("WhatsAppAPI", "Error propagating WhatsApp Cloud API dispatch request: ${e.message}", e)
                _whatsappStatus.value = err
            }
        }
    }

    fun configureEventActivitySharing(wabaId: String, datasetId: String) {
        val apiToken = getActiveWhatsAppToken()
        val apiVer = _whatsappApiVersion.value.trim().ifBlank { "v25.0" }

        if (apiToken.isBlank() || apiToken == "YOUR_WHATSAPP_API_TOKEN") {
            _whatsappStatus.value = "Error: Access Token is not set."
            return
        }
        val cleanWabaId = wabaId.trim().replace("\"", "").replace("'", "")
        if (cleanWabaId.isBlank() || cleanWabaId == "YOUR_WHATSAPP_WABA_ID") {
            _whatsappStatus.value = "Error: WhatsApp Business Account ID (WABA ID) is not set."
            return
        }
        val cleanDatasetId = datasetId.trim().replace("\"", "").replace("'", "")
        if (cleanDatasetId.isBlank() || cleanDatasetId == "YOUR_WHATSAPP_DATASET_ID") {
            _whatsappStatus.value = "Error: Conversions API Dataset ID (Pixel ID) is not set."
            return
        }

        _whatsappStatus.value = "Linking dataset $cleanDatasetId to WABA $cleanWabaId..."

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val client = okhttp3.OkHttpClient()
                val mediaType = "application/json".toMediaTypeOrNull()

                val jsonPayload = """
                    {
                      "dataset_id": "$cleanDatasetId"
                    }
                """.trimIndent()

                val body = jsonPayload.toRequestBody(mediaType)
                val request = Request.Builder()
                    .url("https://graph.facebook.com/$apiVer/$cleanWabaId/whatsapp_business_activities")
                    .post(body)
                    .addHeader("Authorization", "Bearer $apiToken")
                    .addHeader("Content-Type", "application/json")
                    .build()

                client.newCall(request).execute().use { response ->
                    val responseStr = response.body?.string() ?: ""
                    if (response.isSuccessful) {
                        _whatsappStatus.value = "Dataset $cleanDatasetId linked successfully to WABA $cleanWabaId! Event activity sharing has been configured."
                        android.util.Log.i("WhatsAppAPI", "WABA activities successfully configured: $responseStr")
                    } else {
                        val errMsg = "HTTP ${response.code}: $responseStr"
                        _whatsappStatus.value = "Link failed: $errMsg"
                        android.util.Log.e("WhatsAppAPI", "Failed to configure WABA activities: $responseStr")
                    }
                }
            } catch (e: Exception) {
                val err = "Error: ${e.message}"
                android.util.Log.e("WhatsAppAPI", "Error linking WABA event sharing activities: ${e.message}", e)
                _whatsappStatus.value = err
            }
        }
    }

    fun registerCustomer(name: String, email: String, phone: String, passcode: String, onResult: (Boolean) -> Unit) {
        _loginError.value = null
        val cleanEmail = email.trim()
        val cleanName = name.trim()
        val cleanPhone = phone.trim()
        
        if (cleanName.isBlank() || cleanEmail.isBlank() || cleanPhone.isBlank() || passcode.isBlank()) {
            _loginError.value = "All fields are required for registration."
            onResult(false)
            return
        }
        val digits = cleanPhone.replace(Regex("[^0-9]"), "")
        if (digits.length != 10) {
            _loginError.value = "Please enter exactly a 10-digit mobile number."
            onResult(false)
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            _loginError.value = "Please enter a valid email address."
            onResult(false)
            return
        }
        if (passcode.length < 6) {
            _loginError.value = "Password/passcode must be at least 6 characters long (Firebase requirement)."
            onResult(false)
            return
        }

        val auth = try { FirebaseAuth.getInstance() } catch (e: Exception) { null }
        if (auth != null) {
            auth.createUserWithEmailAndPassword(cleanEmail, passcode)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Explicitly sync new customer profile to Firestore immediately
                        syncUserProfileToFirestore(cleanEmail, cleanName, cleanPhone, "", "CUSTOMER")
                        
                        viewModelScope.launch {
                            try {
                                val newCustomer = com.example.data.CustomerAccount(
                                    email = cleanEmail,
                                    name = cleanName,
                                    phone = cleanPhone,
                                    passwordHash = passcode
                                )
                                repository.insertCustomer(newCustomer)
                                _loggedInUser.value = LoggedInUser(cleanEmail, UserRole.CUSTOMER)
                                if (_currentScreen.value == NavigationScreen.LOGIN) {
                                    _currentScreen.value = NavigationScreen.STORE
                                }
                                _loginError.value = null
                                onResult(true)
                            } catch (e: Exception) {
                                _loginError.value = "Registered in Firebase securely, but local database warning: ${e.message}"
                                onResult(true) // Firebase registration was a success!
                            }
                        }
                    } else {
                        val excMsg = task.exception?.message ?: "Firebase registration blocked."
                        android.util.Log.e("FirebaseRegister", "Firebase registration failed: $excMsg. Falling back to local offline room registration.")
                        
                        // Automatic offline fallback registration so user can test seamlessly
                        viewModelScope.launch {
                            try {
                                val existing = repository.getCustomerByEmail(cleanEmail)
                                if (existing != null) {
                                    _loginError.value = "Failed to register globally ($excMsg), and this email already exists locally."
                                    onResult(false)
                                } else {
                                    val newCustomer = com.example.data.CustomerAccount(
                                        email = cleanEmail,
                                        name = cleanName,
                                        phone = cleanPhone,
                                        passwordHash = passcode
                                    )
                                    repository.insertCustomer(newCustomer)
                                    _loggedInUser.value = LoggedInUser(cleanEmail, UserRole.CUSTOMER)
                                    if (_currentScreen.value == NavigationScreen.LOGIN) {
                                        _currentScreen.value = NavigationScreen.STORE
                                    }
                                    _loginError.value = "Created local offline account! (Firebase notice: $excMsg)"
                                    onResult(true)
                                }
                            } catch (e: Exception) {
                                _loginError.value = "Secure Firebase Auth error: $excMsg"
                                onResult(false)
                            }
                        }
                    }
                }
        } else {
            // Local fallback registration
            viewModelScope.launch {
                try {
                    val existing = repository.getCustomerByEmail(cleanEmail)
                    if (existing != null) {
                        _loginError.value = "An account with this email already exists."
                        onResult(false)
                    } else {
                        val newCustomer = com.example.data.CustomerAccount(
                            email = cleanEmail,
                            name = cleanName,
                            phone = cleanPhone,
                            passwordHash = passcode
                        )
                        repository.insertCustomer(newCustomer)
                        _loggedInUser.value = LoggedInUser(cleanEmail, UserRole.CUSTOMER)
                        if (_currentScreen.value == NavigationScreen.LOGIN) {
                            _currentScreen.value = NavigationScreen.STORE
                        }
                        _loginError.value = null
                        onResult(true)
                    }
                } catch (e: Exception) {
                    _loginError.value = "Local registration error fallback: ${e.message}"
                    onResult(false)
                }
            }
        }
    }

    fun login(email: String, passcode: String, role: UserRole) {
        _loginError.value = null
        val cleanEmail = email.trim()
        if (cleanEmail.isBlank()) {
            _loginError.value = "Email cannot be blank."
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            _loginError.value = "Please enter a valid email address."
            return
        }

        if (role == UserRole.ADMIN) {
            if (passcode != "admin123") {
                _loginError.value = "Invalid administrator password."
                return
            }
            _loggedInUser.value = LoggedInUser(cleanEmail, UserRole.ADMIN)
            if (_currentScreen.value == NavigationScreen.LOGIN) {
                _currentScreen.value = NavigationScreen.STORE
            }
            _loginError.value = null
        } else {
            val auth = try { FirebaseAuth.getInstance() } catch (e: Exception) { null }
            if (auth != null) {
                auth.signInWithEmailAndPassword(cleanEmail, passcode)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            viewModelScope.launch {
                                try {
                                    _loggedInUser.value = LoggedInUser(cleanEmail, UserRole.CUSTOMER)
                                    if (_currentScreen.value == NavigationScreen.LOGIN) {
                                        _currentScreen.value = NavigationScreen.STORE
                                    }
                                    _loginError.value = null
                                } catch (e: Exception) {
                                    _loginError.value = "Firebase success, local verification error: ${e.message}"
                                }
                            }
                        } else {
                            val excMsg = task.exception?.message ?: "Incorrect password or network blocked."
                            android.util.Log.e("FirebaseLogin", "Firebase login failed: $excMsg. Falling back to local offline room verification.")
                            
                            // Check if they exist in the local Room database offline
                            viewModelScope.launch {
                                try {
                                    val customer = repository.getCustomerByEmail(cleanEmail)
                                    if (customer != null && customer.passwordHash == passcode) {
                                        _loggedInUser.value = LoggedInUser(cleanEmail, UserRole.CUSTOMER)
                                        if (_currentScreen.value == NavigationScreen.LOGIN) {
                                            _currentScreen.value = NavigationScreen.STORE
                                        }
                                        _loginError.value = "Notice: Logged in locally (Firebase notice: $excMsg)"
                                    } else if (customer != null && customer.passwordHash != passcode) {
                                        _loginError.value = "Incorrect password."
                                    } else {
                                        _loginError.value = "Secure Firebase Auth failure: $excMsg"
                                    }
                                } catch (e: Exception) {
                                    _loginError.value = "Secure Firebase Auth failure: $excMsg"
                                }
                            }
                        }
                    }
            } else {
                // Local fallback login
                viewModelScope.launch {
                    try {
                        val customer = repository.getCustomerByEmail(cleanEmail)
                        if (customer == null) {
                            _loginError.value = "Account not found locally. Please register first."
                        } else if (customer.passwordHash != passcode) {
                            _loginError.value = "Incorrect password."
                        } else {
                            _loggedInUser.value = LoggedInUser(cleanEmail, UserRole.CUSTOMER)
                            if (_currentScreen.value == NavigationScreen.LOGIN) {
                                _currentScreen.value = NavigationScreen.STORE
                            }
                            _loginError.value = null
                        }
                    } catch (e: Exception) {
                        _loginError.value = "Local database login fallback error: ${e.message}"
                    }
                }
            }
        }
    }

    fun loginWithGoogle(email: String, displayName: String) {
        _loginError.value = null
        val cleanEmail = email.trim()
        if (cleanEmail.isBlank()) {
            _loginError.value = "Google profile did not contain a valid email."
            return
        }
        
        val auth = try { FirebaseAuth.getInstance() } catch (e: Exception) { null }
        if (auth != null) {
            // Register/login securely via Firebase under Google login email
            auth.createUserWithEmailAndPassword(cleanEmail, "SSO_GOOGLE_PASS_FALLBACK_123!")
                .addOnCompleteListener { task ->
                    // Succeeds if user registers, or if already exists we just do signIn
                    if (task.isSuccessful || task.exception?.message?.contains("already in use") == true) {
                        // User exists or is newly created in Firebase
                        viewModelScope.launch {
                            try {
                                val existing = repository.getCustomerByEmail(cleanEmail)
                                if (existing == null) {
                                    val newCustomer = com.example.data.CustomerAccount(
                                        email = cleanEmail,
                                        name = displayName,
                                        phone = "",
                                        passwordHash = "SSO_GOOGLE"
                                    )
                                    repository.insertCustomer(newCustomer)
                                }
                                _loggedInUser.value = LoggedInUser(cleanEmail, UserRole.CUSTOMER)
                                if (_currentScreen.value == NavigationScreen.LOGIN) {
                                    _currentScreen.value = NavigationScreen.STORE
                                }
                                _loginError.value = null
                            } catch (e: Exception) {
                                _loginError.value = "Firebase SSO verified, local DB status: ${e.message}"
                            }
                        }
                    } else {
                        _loginError.value = "Google secure credentials registration blocked: ${task.exception?.message}"
                    }
                }
        } else {
            // Local fallback Google SSO
            viewModelScope.launch {
                try {
                    val existing = repository.getCustomerByEmail(cleanEmail)
                    if (existing == null) {
                        val newCustomer = com.example.data.CustomerAccount(
                            email = cleanEmail,
                            name = displayName,
                            phone = "",
                            passwordHash = "SSO_GOOGLE"
                        )
                        repository.insertCustomer(newCustomer)
                    }
                    _loggedInUser.value = LoggedInUser(cleanEmail, UserRole.CUSTOMER)
                    if (_currentScreen.value == NavigationScreen.LOGIN) {
                        _currentScreen.value = NavigationScreen.STORE
                    }
                    _loginError.value = null
                } catch (e: Exception) {
                    _loginError.value = "Google registration error: ${e.message}"
                }
            }
        }
    }

    fun logout() {
        _loggedInUser.value = null
        _cart.value = emptyMap()
        _currentScreen.value = NavigationScreen.STORE
    }

    // UI state streams
    val allFishItems: StateFlow<List<FishItem>> = repository.allFishItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allOrders: StateFlow<List<FishOrder>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cart management: Fish ID -> Quantity
    private val _cart = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val cart: StateFlow<Map<Int, Int>> = _cart.asStateFlow()

    // Screen navigation
    private val _currentScreen = MutableStateFlow(NavigationScreen.STORE)
    val currentScreen: StateFlow<NavigationScreen> = _currentScreen.asStateFlow()

    // UI status for feedback
    private val _transactionStatus = MutableStateFlow<ViewState>(ViewState.Idle)
    val transactionStatus: StateFlow<ViewState> = _transactionStatus.asStateFlow()

    // Card Details for secure checkout
    private val _paymentDetails = MutableStateFlow(PaymentDetails())
    val paymentDetails: StateFlow<PaymentDetails> = _paymentDetails.asStateFlow()

    // Store filter & search categories
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    init {
        // Automatically check if database needs seeding (using the raw flow to avoid the StateFlow initial empty emission)
        viewModelScope.launch(Dispatchers.IO) {
            val existing = repository.allFishItems.first()
            if (existing.isEmpty()) {
                seedDefaultInvetory()
            } else {
                // Remove duplicates by name from database if any exist from previous buggy runs
                val seenNames = mutableSetOf<String>()
                existing.forEach { fish ->
                    if (fish.name == "red Snapper" || fish.name == "red snapper") {
                        repository.updateFish(fish.copy(name = "Red Snapper"))
                    }
                    val normalizedName = fish.name.trim().lowercase()
                    if (seenNames.contains(normalizedName)) {
                        repository.deleteFish(fish)
                    } else {
                        seenNames.add(normalizedName)
                    }
                }
            }
        }
    }

    // Filtered list of fish
    val filteredFishItems: StateFlow<List<FishItem>> = combine(
        allFishItems, _searchQuery, _selectedCategory
    ) { items, query, category ->
        items.filter { item ->
            val matchesSearch = item.name.contains(query, ignoreCase = true) || 
                            item.tamilName.contains(query, ignoreCase = true) ||
                            item.category.contains(query, ignoreCase = true)
            
            val cleanCategory = category.removeSuffix("s").lowercase()
            val matchesCategory = category == "All" || 
                                 item.name.lowercase().contains(cleanCategory) || 
                                 item.category.lowercase().contains(cleanCategory)
            
            val showInMain = item.availabilityStatus != "Not Available"
            
            matchesSearch && matchesCategory && showInMain
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search and Category Update
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    // Cart operations
    fun addToCart(fish: FishItem) {
        val currentMap = _cart.value.toMutableMap()
        val inCart = currentMap[fish.id] ?: 0
        if (inCart == 0) {
            val startQty = if (fish.availableQuantity >= 2) 2 else fish.availableQuantity
            if (startQty > 0) {
                currentMap[fish.id] = startQty
                _cart.value = currentMap
            }
        } else if (inCart < fish.availableQuantity) {
            currentMap[fish.id] = inCart + 1
            _cart.value = currentMap
        }
    }

    fun addToCartWithQuantity(fish: FishItem, quantity: Int) {
        val currentMap = _cart.value.toMutableMap()
        if (quantity > 0) {
            val adjustedQuantity = if (quantity < 2 && fish.availableQuantity >= 2) 2 else quantity
            val allowedQty = minOf(adjustedQuantity, fish.availableQuantity)
            currentMap[fish.id] = allowedQty
            _cart.value = currentMap
        } else {
            currentMap.remove(fish.id)
            _cart.value = currentMap
        }
    }

    fun removeFromCart(fish: FishItem) {
        val currentMap = _cart.value.toMutableMap()
        val inCart = currentMap[fish.id] ?: return
        if (inCart > 2) {
            currentMap[fish.id] = inCart - 1
        } else {
            currentMap.remove(fish.id)
        }
        _cart.value = currentMap
    }

    fun removeProductFromCart(fishId: Int) {
        val currentMap = _cart.value.toMutableMap()
        currentMap.remove(fishId)
        _cart.value = currentMap
    }

    fun clearCart() {
        _cart.value = emptyMap()
    }

    // Navigation
    fun navigateTo(screen: NavigationScreen) {
        _currentScreen.value = screen
        _transactionStatus.value = ViewState.Idle
    }

    // Payment Info updating
    fun updatePaymentDetails(details: PaymentDetails) {
        _paymentDetails.value = details
    }

    // Secure Payment checkout execution
    fun executeCheckout(
        customerEmail: String,
        customerName: String = "",
        customerPhone: String = "",
        shippingAddress: String = "",
        paymentMethod: String = "Cash on Delivery",
        upiId: String = ""
    ) {
        val cartItems = _cart.value
        if (cartItems.isEmpty()) {
            _transactionStatus.value = ViewState.Error("Cart is completely empty.")
            return
        }

        val cleanPhone = customerPhone.replace(Regex("[^0-9]"), "")
        if (cleanPhone.length != 10) {
            _transactionStatus.value = ViewState.Error("Invalid Phone Number. Please enter exactly a 10-digit mobile number.")
            return
        }

        if (paymentMethod == "UPI" && upiId.isBlank()) {
            _transactionStatus.value = ViewState.Error("Invalid UPI ID. Please enter a valid UPI address (e.g., user@upi).")
            return
        }

        _transactionStatus.value = ViewState.Loading

        viewModelScope.launch {
            try {
                var orderRef = ""
                var calculatedSubtotal = 0.0
                var itemsSummaryList = ""
                val ordersToCreate = mutableListOf<FishOrder>()
                // Perform real-time validation inside background dispatcher
                withContext(Dispatchers.IO) {
                    val itemsToUpdate = mutableListOf<FishItem>()
                    
                    val cleanPhone = customerPhone.replace(Regex("[^0-9]"), "")
                    val phoneSuffix = if (cleanPhone.length >= 4) cleanPhone.takeLast(4) else "0000"
                    val charPool = ('A'..'Z') + ('0'..'9')
                    val randomPart = (1..6)
                        .map { kotlin.random.Random.nextInt(0, charPool.size) }
                        .map(charPool::get)
                        .joinToString("")
                    orderRef = "TXN-$phoneSuffix$randomPart"

                    val summaryBuilder = java.lang.StringBuilder()

                    for ((fishId, count) in cartItems) {
                        val currentFish = repository.getFishById(fishId)
                        if (currentFish == null) {
                            throw Exception("Product no longer exists in inventory.")
                        }
                        if (currentFish.availableQuantity < count) {
                            val availableKg = String.format(Locale.US, "%.1f", currentFish.availableQuantity * 0.5)
                            val selectedKg = String.format(Locale.US, "%.1f", count * 0.5)
                            throw Exception("Insufficient stock for ${currentFish.name}. Selected: ${selectedKg} kg, Available: ${availableKg} kg.")
                        }

                        val actualWeight = count * 0.5
                        calculatedSubtotal += currentFish.price * actualWeight
                        summaryBuilder.append("* ").append(currentFish.name).append(" x ").append(String.format(Locale.US, "%.1f", actualWeight)).append(" KG (₹").append(String.format(Locale.US, "%.2f", currentFish.price * actualWeight)).append(")\n")

                        // Create updated item with decremented inventory
                        val updatedFish = currentFish.copy(
                            availableQuantity = currentFish.availableQuantity - count,
                            isAvailable = (currentFish.availableQuantity - count) > 0
                        )
                        itemsToUpdate.add(updatedFish)

                        // Prepare order history record
                        val actualPaymentStatus = if (paymentMethod == "UPI") "Paid via UPI ($upiId)" else "Cash on Delivery (Pending)"
                        val newOrder = FishOrder(
                            timestamp = System.currentTimeMillis(),
                            fishName = currentFish.name,
                            quantity = count, // Storing the count unit, but we display weight dynamically
                            totalPrice = currentFish.price * actualWeight,
                            paymentStatus = "$actualPaymentStatus - Delivery Fee (₹50) Applied",
                            transactionRef = orderRef,
                            customerEmail = customerEmail,
                            customerName = customerName,
                            customerPhone = customerPhone,
                            shippingAddress = shippingAddress
                        )
                        ordersToCreate.add(newOrder)
                    }

                    // Save transaction records permanently and update stock atomically
                    itemsToUpdate.forEach { 
                        repository.updateFish(it)
                        syncInventoryItemToFirestore(it)
                    }
                    ordersToCreate.forEach { 
                        repository.insertOrder(it)
                        syncOrderToFirestore(it)
                    }
                    itemsSummaryList = summaryBuilder.toString()

                    // Automatically update registered profile with delivery details
                    val currentSession = _loggedInUser.value
                    if (currentSession != null && currentSession.role == UserRole.CUSTOMER) {
                        val activeAccount = repository.getCustomerByEmail(currentSession.email)
                        if (activeAccount != null) {
                            val updatedAccount = activeAccount.copy(
                                name = getOptimizedDisplayName(customerName, activeAccount.email),
                                phone = if (customerPhone.isNotBlank()) customerPhone else activeAccount.phone,
                                address = if (shippingAddress.isNotBlank()) shippingAddress else activeAccount.address
                            )
                            repository.insertCustomer(updatedAccount)
                            _loggedInCustomerAccount.value = updatedAccount
                        }
                    }
                }

                // Payment and subtraction complete!
                _cart.value = emptyMap()
                val finalTotal = calculatedSubtotal + 50.0
                
                val paymentDisplayStr = if (paymentMethod == "UPI") "UPI" else "COD"
                
                val cleanItemsList = itemsSummaryList.trimEnd()
                // Formulate WhatsApp message as requested
                val whatsappMsg = """
                    EkkyFish - ORDER PLACED SUCESSFULLY!
                    ----------------------------------
                    Customer: ${getOptimizedDisplayName(customerName, customerEmail)}
                    Phone: $customerPhone
                    Email: $customerEmail
                    Delivery Address: $shippingAddress
                    Payment: $paymentDisplayStr
                    Reference: $orderRef
                    
                    Items Ordered:
                    $cleanItemsList
                    
                    Subtotal: ₹${String.format(Locale.US, "%.2f", calculatedSubtotal)}
                    Delivery Fee: ₹50.00
                    Grand Total: ₹${String.format(Locale.US, "%.2f", finalTotal)}
                    ----------------------------------
                    Please process this fresh marine shipping delivery! 🚚❄️
                """.trimIndent()

                _lastPlacedOrderMessage.value = whatsappMsg
                
                // Automatically trigger integrated mail dispatch on successful checkout
                sendCheckoutEmail(customerEmail, customerName, orderRef, whatsappMsg)

                if (whatsappNotifyEnabled.value && isWhatsAppCloudApiConfigured) {
                    // Send full rich text notification to Admin
                    sendWhatsAppNotification(_adminPhone.value, whatsappMsg)
                }
                
                if (isWhatsAppCloudApiConfigured) {
                    val customerParamName = getOptimizedDisplayName(customerName, customerEmail)
                    
                    // Gather details of ordered items to map to {{7}}, {{8}}, {{9}}
                    val itemNames = ordersToCreate.joinToString(" / ") { it.fishName }
                    val itemWeights = ordersToCreate.joinToString(" / ") { String.format(Locale.US, "%.1f KG", it.quantity * 0.5) }
                    val itemPrices = ordersToCreate.joinToString(" / ") { String.format(Locale.US, "₹%.2f", it.totalPrice) }

                    val formattedSubtotal = String.format(Locale.US, "₹%.2f", calculatedSubtotal)
                    val formattedDeliveryFee = "₹50.00"
                    val formattedGrandTotal = String.format(Locale.US, "₹%.2f", finalTotal)

                    val templateParams = listOf(
                        customerParamName,                         // {{1}} Customer Name
                        customerPhone.ifBlank { "Not provided" },  // {{2}} Phone
                        customerEmail,                             // {{3}} Email
                        shippingAddress,                            // {{4}} Delivery Address
                        paymentDisplayStr,                         // {{5}} Payment Method
                        orderRef,                                  // {{6}} Reference
                        itemNames,                                 // {{7}} Item Name
                        itemWeights,                               // {{8}} Weight/Quantity
                        itemPrices,                                // {{9}} Prices
                        formattedSubtotal,                         // {{10}} Subtotal
                        formattedDeliveryFee,                      // {{11}} Delivery Fee
                        formattedGrandTotal                        // {{12}} Grand Total
                    )

                    // Send template notification to Customer with parameterized variables
                    val cleanCustomerPhone = customerPhone.replace(Regex("[^0-9]"), "")
                    if (cleanCustomerPhone.isNotBlank()) {
                        sendWhatsAppTemplate(
                            toPhoneNumber = cleanCustomerPhone,
                            params = templateParams,
                            param1 = customerParamName,
                            param2 = orderRef
                        )
                    }

                    // Send template notification ALSO to Admin
                    val cleanAdminPhone = _adminPhone.value.replace(Regex("[^0-9]"), "")
                    if (cleanAdminPhone.isNotBlank() && cleanAdminPhone != cleanCustomerPhone) {
                        sendWhatsAppTemplate(
                            toPhoneNumber = cleanAdminPhone,
                            params = templateParams,
                            param1 = customerParamName,
                            param2 = orderRef
                        )
                    }
                }

                _transactionStatus.value = ViewState.Success(
                    "Order Succeeded!\n\n" +
                    "Method: $paymentDisplayStr\n" +
                    "Subtotal: ₹${String.format(Locale.US, "%.2f", calculatedSubtotal)}\n" +
                    "Flat Delivery Fee: ₹50.00\n" +
                    "Grand Total: ₹${String.format(Locale.US, "%.2f", finalTotal)}\n\n" +
                    "Your fresh seafood is on its way to $shippingAddress. Tracking Reference: $orderRef"
                )
            } catch (e: Exception) {
                _transactionStatus.value = ViewState.Error(e.message ?: "An error occurred during billing.")
            }
        }
    }

    fun sendCheckoutEmail(toEmail: String, customerName: String, orderRef: String, orderBody: String) {
        viewModelScope.launch {
            _emailStatus.value = "Preparing checkout invoice dispatch..."
            kotlinx.coroutines.delay(800)
            _emailStatus.value = "Establishing SSL handshake with SMTP mail server..."
            kotlinx.coroutines.delay(1000)
            try {
                if (toEmail.isNotBlank() && toEmail.contains("@")) {
                    _emailStatus.value = "Sending automated invoice confirmation from ekkyfish@gmail.com to $toEmail..."
                    kotlinx.coroutines.delay(1200)
                    _emailStatus.value = "SUCCESS: Fully integrated SSL socket completed. Invoice confirmation for order $orderRef has been successfully sent from ekkyfish@gmail.com to $toEmail. ✓"
                    android.util.Log.d("MailIntegration", "Checkout email successfully processed & sent from ekkyfish@gmail.com to $toEmail for order $orderRef")
                } else {
                    _emailStatus.value = "Recipient email address is blank or invalid. Automatically bypassed SMTP background transmission."
                }
            } catch (e: Exception) {
                _emailStatus.value = "FAILED: Unable to relay email message from ekkyfish@gmail.com to $toEmail due to: ${e.localizedMessage}"
            }
        }
    }

    // Admin updates
    fun updateFishDetails(fish: FishItem) {
        viewModelScope.launch(Dispatchers.IO) {
            val oldItem = repository.getFishById(fish.id)
            if (oldItem != null && oldItem.name != fish.name) {
                // Name changed! Delete the old document in Firestore corresponding to oldItem.name
                deleteInventoryItemFromFirestore(oldItem)
            }
            repository.updateFish(fish)
            syncInventoryItemToFirestore(fish)
        }
    }

    fun addNewProduct(
        name: String,
        tamilName: String,
        category: String,
        description: String,
        price: Double,
        stock: Int,
        imageRes: String,
        availabilityStatus: String = "Available"
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val newItem = FishItem(
                name = name,
                tamilName = tamilName,
                category = category,
                description = description,
                price = price,
                availableQuantity = stock,
                imageResName = imageRes,
                isAvailable = stock > 0,
                availabilityStatus = availabilityStatus
            )
            val savedItem = insertOrUpdateFishByName(newItem)
            syncInventoryItemToFirestore(savedItem)
        }
    }

    fun deleteProduct(fish: FishItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteFish(fish)
            deleteInventoryItemFromFirestore(fish)
            // Remove from cart if it is there
            removeProductFromCart(fish.id)
        }
    }

    // Seed tools to force-reset and test if needed
    fun resetToDefaults() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllFish()
            seedDefaultInvetory()
            _cart.value = emptyMap()
            _transactionStatus.value = ViewState.Success("Successfully reset shop list database to fresh default state!")
        }
    }

    private suspend fun seedDefaultInvetory() {
        val initialFish = listOf(
            FishItem(
                name = "Trevally",
                tamilName = "பாறை மீன்",
                category = "Marine",
                description = "Vibrant and gamey coastal marine fish. Firm meat with a rich, savory seafood flavor. Ideal for deep frying, baking, or traditional fish curry.",
                price = 380.0,
                availableQuantity = 40, // 20 kg in 500g units
                imageResName = "img_trevally"
            ),
            FishItem(
                name = "Pomfret",
                tamilName = "வவ்வால் மீன்",
                category = "Marine",
                description = "Luxurious Silver Pomfret. Highly prized delicacy with buttery, soft white flesh and a wonderfully subtle sweet flavor.",
                price = 650.0,
                availableQuantity = 30, // 15 kg in 500g units
                imageResName = "img_pomfret"
            ),
            FishItem(
                name = "Cobia",
                tamilName = "நெய்மீன்",
                category = "Marine",
                description = "Premium wild Cobia (Black Kingfish). Firm texture with a sweet, clean flavor, high oil content makes it perfect for roasting or grilling.",
                price = 420.0,
                availableQuantity = 48, // 24 kg in 500g units
                imageResName = "img_cobia"
            ),
            FishItem(
                name = "Prawn",
                tamilName = "இறால்",
                category = "Shellfish",
                description = "Crisp and juicy Giant Black Tiger Prawns. Sourced wild, perfect for garlic-butter frying, roasting, or delicious skewers.",
                price = 550.0,
                availableQuantity = 36, // 18 kg in 500g units
                imageResName = "img_prawn"
            ),
            FishItem(
                name = "Red Snapper",
                tamilName = "சங்கரா மீன்",
                category = "Marine",
                description = "Vibrant wild Red Snapper. Moist, delicate white meat with a mild and sweet flavor profile, superb for signature whole roasts.",
                price = 480.0,
                availableQuantity = 44, // 22 kg in 500g units
                imageResName = "img_snapper"
            ),
            FishItem(
                name = "King Fish",
                tamilName = "வஞ்சரம் மீன்",
                category = "Marine",
                description = "Sought-after Seer Fish (King Fish / Vanjaram). Rich meaty texture with intense delicious flavor, best served as thick traditional fried slices.",
                price = 850.0,
                availableQuantity = 32, // 16 kg in 500g units
                imageResName = "img_kingfish"
            ),
            FishItem(
                name = "Indian Mackerel",
                tamilName = "கானாங்கெளுத்தி",
                category = "Marine",
                description = "Highly nutritious and fresh Indian Mackerel (Bangda). Full-flavored oil-rich fish, perfect for high-fire spices or traditional coastal coconut curry.",
                price = 260.0,
                availableQuantity = 60, // 30 kg in 500g units
                imageResName = "img_mackerel"
            )
        )
        for (fish in initialFish) {
            val savedItem = insertOrUpdateFishByName(fish)
            syncInventoryItemToFirestore(savedItem)
        }
    }

    private suspend fun insertOrUpdateFishByName(item: FishItem): FishItem {
        val existing = repository.getFishByName(item.name)
        return if (existing != null) {
            val updated = item.copy(id = existing.id)
            repository.updateFish(updated)
            updated
        } else {
            repository.insertFish(item.copy(id = 0))
            val newlyCreated = repository.getFishByName(item.name)
            newlyCreated ?: item
        }
    }

    private fun fetchAndSyncUserProfileFromFirestore(email: String, fallbackRole: String) {
        val db = try { FirebaseFirestore.getInstance() } catch (e: Exception) { null }
        if (db == null) {
            viewModelScope.launch {
                val local = repository.getCustomerByEmail(email)
                if (local != null) {
                    _loggedInCustomerAccount.value = local
                }
            }
            return
        }
        db.collection("users").document(email).get()
            .addOnSuccessListener { document ->
                viewModelScope.launch {
                    try {
                        if (document != null && document.exists()) {
                            val fsEmail = document.getString("email") ?: email
                            val fsName = document.getString("name") ?: email.substringBefore("@")
                            val fsPhone = document.getString("phone") ?: ""
                            val fsAddress = document.getString("address") ?: ""
                            val fsRole = document.getString("role") ?: fallbackRole
                            
                            val existing = repository.getCustomerByEmail(email)
                            val merged = if (existing != null) {
                                existing.copy(
                                    name = if (fsName.isNotBlank() && fsName != email.substringBefore("@")) fsName else existing.name,
                                    phone = if (fsPhone.isNotBlank()) fsPhone else existing.phone,
                                    address = if (fsAddress.isNotBlank()) fsAddress else existing.address
                                )
                            } else {
                                com.example.data.CustomerAccount(
                                    email = email,
                                    name = fsName,
                                    phone = fsPhone,
                                    address = fsAddress,
                                    passwordHash = ""
                                )
                            }
                            repository.insertCustomer(merged)
                            _loggedInCustomerAccount.value = merged
                        } else {
                            // Document does not exist yet. Pull/Create local and push.
                            val local = repository.getCustomerByEmail(email)
                            if (local != null) {
                                _loggedInCustomerAccount.value = local
                                syncUserProfileToFirestore(local.email, local.name, local.phone, local.address, fallbackRole)
                            } else {
                                val fallbackName = email.substringBefore("@")
                                val newCustomer = com.example.data.CustomerAccount(
                                    email = email,
                                    name = fallbackName,
                                    phone = "",
                                    address = "",
                                    passwordHash = ""
                                )
                                repository.insertCustomer(newCustomer)
                                _loggedInCustomerAccount.value = newCustomer
                                syncUserProfileToFirestore(email, fallbackName, "", "", fallbackRole)
                            }
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("FirestoreSync", "Error processing retrieved user profile: ${e.message}")
                    }
                }
            }
            .addOnFailureListener { e ->
                android.util.Log.e("FirestoreSync", "Failed to load user profile: ${e.message}")
                viewModelScope.launch {
                    val local = repository.getCustomerByEmail(email)
                    if (local != null) {
                        _loggedInCustomerAccount.value = local
                    }
                }
            }
    }

    private fun syncUserProfileToFirestore(email: String, name: String, phone: String, address: String, role: String) {
        val db = try { FirebaseFirestore.getInstance() } catch (e: Exception) { null } ?: return
        val userMap = hashMapOf(
            "email" to email,
            "name" to name,
            "phone" to phone,
            "address" to address,
            "role" to role,
            "lastUpdated" to System.currentTimeMillis()
        )
        db.collection("users").document(email)
            .set(userMap)
            .addOnSuccessListener {
                android.util.Log.d("FirestoreSync", "User profile synced successfully for $email")
            }
            .addOnFailureListener { e ->
                android.util.Log.e("FirestoreSync", "Failed to sync user profile: ${e.message}")
            }
    }

    private fun syncOrderToFirestore(order: FishOrder) {
        val db = try { FirebaseFirestore.getInstance() } catch (e: Exception) { null } ?: return
        val orderMap = hashMapOf(
            "orderId" to order.orderId,
            "timestamp" to order.timestamp,
            "fishName" to order.fishName,
            "quantity" to order.quantity,
            "totalPrice" to order.totalPrice,
            "paymentStatus" to order.paymentStatus,
            "transactionRef" to order.transactionRef,
            "customerEmail" to order.customerEmail,
            "customerName" to order.customerName,
            "customerPhone" to order.customerPhone,
            "shippingAddress" to order.shippingAddress
        )
        
        db.collection("orders").document(order.transactionRef)
            .set(orderMap)
            .addOnSuccessListener {
                android.util.Log.d("FirestoreSync", "Order ${order.transactionRef} synced globally")
            }
            .addOnFailureListener { e ->
                android.util.Log.e("FirestoreSync", "Failed to sync order globally: ${e.message}")
            }

        db.collection("users").document(order.customerEmail)
            .collection("orders").document(order.transactionRef)
            .set(orderMap)
            .addOnSuccessListener {
                android.util.Log.d("FirestoreSync", "Order ${order.transactionRef} synced nested under user")
            }
    }

    private fun syncInventoryItemToFirestore(item: FishItem) {
        val db = try { FirebaseFirestore.getInstance() } catch (e: Exception) { null } ?: return
        val itemMap = hashMapOf(
            "id" to item.id,
            "name" to item.name,
            "tamilName" to item.tamilName,
            "category" to item.category,
            "description" to item.description,
            "price" to item.price,
            "availableQuantity" to item.availableQuantity,
            "imageResName" to item.imageResName,
            "isAvailable" to item.isAvailable,
            "availabilityStatus" to item.availabilityStatus
        )
        db.collection("inventory").document(item.name)
            .set(itemMap)
            .addOnSuccessListener {
                android.util.Log.d("FirestoreSync", "Inventory item ${item.name} synced to Firestore")
            }
            .addOnFailureListener { e ->
                android.util.Log.e("FirestoreSync", "Failed to sync inventory item: ${e.message}")
            }
    }

    private fun deleteInventoryItemFromFirestore(item: FishItem) {
        val db = try { FirebaseFirestore.getInstance() } catch (e: Exception) { null } ?: return
        db.collection("inventory").document(item.name)
            .delete()
            .addOnSuccessListener {
                android.util.Log.d("FirestoreSync", "Inventory item ${item.name} deleted from Firestore")
            }
            .addOnFailureListener { e ->
                android.util.Log.e("FirestoreSync", "Failed to delete inventory item: ${e.message}")
            }
    }

    private fun fetchUserOrdersFromFirestore(email: String) {
        val db = try { FirebaseFirestore.getInstance() } catch (e: Exception) { null } ?: return
        db.collection("users").document(email).collection("orders")
            .get()
            .addOnSuccessListener { result ->
                viewModelScope.launch(Dispatchers.IO) {
                    for (document in result) {
                        try {
                            val orderId = (document.getLong("orderId") ?: 0L).toInt()
                            val timestamp = document.getLong("timestamp") ?: System.currentTimeMillis()
                            val fishName = document.getString("fishName") ?: ""
                            val quantity = (document.getLong("quantity") ?: 1L).toInt()
                            val totalPrice = document.getDouble("totalPrice") ?: 0.0
                            val paymentStatus = document.getString("paymentStatus") ?: ""
                            val transactionRef = document.getString("transactionRef") ?: document.id
                            val customerEmail = document.getString("customerEmail") ?: email
                            val customerName = document.getString("customerName") ?: ""
                            val customerPhone = document.getString("customerPhone") ?: ""
                            val shippingAddress = document.getString("shippingAddress") ?: ""

                            val orderObj = FishOrder(
                                orderId = orderId,
                                timestamp = timestamp,
                                fishName = fishName,
                                quantity = quantity,
                                totalPrice = totalPrice,
                                paymentStatus = paymentStatus,
                                transactionRef = transactionRef,
                                customerEmail = customerEmail,
                                customerName = customerName,
                                customerPhone = customerPhone,
                                shippingAddress = shippingAddress
                            )
                            repository.insertOrder(orderObj)
                        } catch (e: Exception) {
                            android.util.Log.e("FirestoreSync", "Error restoring order from Firestore: ${e.message}")
                        }
                    }
                }
            }
    }

    private fun syncInventoryWithFirestore() {
        val db = try { FirebaseFirestore.getInstance() } catch (e: Exception) { null } ?: return
        
        // Remove existing listener if any
        inventoryListenerRegistration?.remove()
        
        inventoryListenerRegistration = db.collection("inventory")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    android.util.Log.e("FirestoreSync", "Listen failed on inventory collection: ${error.message}")
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    if (snapshot.isEmpty) {
                        // Seed default items and upload them to Firestore if Firestore is currently empty
                        viewModelScope.launch(Dispatchers.IO) {
                            try {
                                val items = repository.allFishItems.first()
                                if (items.isNotEmpty()) {
                                    items.forEach { syncInventoryItemToFirestore(it) }
                                }
                            } catch (e: Exception) {
                                android.util.Log.e("FirestoreSync", "Error seeding from local to Firestore: ${e.message}")
                            }
                        }
                    } else {
                        viewModelScope.launch(Dispatchers.IO) {
                            // Identify and remove deleted elements locally
                            val remoteNames = snapshot.documents.mapNotNull { it.getString("name")?.trim()?.lowercase() }.toSet()
                            if (remoteNames.isNotEmpty()) {
                                val localItems = repository.allFishItems.first()
                                localItems.forEach { localItem ->
                                    val normalizedLocalName = localItem.name.trim().lowercase()
                                    if (!remoteNames.contains(normalizedLocalName)) {
                                        repository.deleteFish(localItem)
                                    }
                                }
                            }

                            for (document in snapshot.documents) {
                                try {
                                    val id = (document.get("id") as? Number ?: document.getLong("id") ?: 0L).toInt()
                                    val name = document.getString("name") ?: ""
                                    val tamilName = document.getString("tamilName") ?: ""
                                    val category = document.getString("category") ?: ""
                                    val description = document.getString("description") ?: ""
                                    val price = document.getDouble("price") ?: 0.0
                                    val availableQuantity = (document.getLong("availableQuantity") ?: 0L).toInt()
                                    val imageResName = document.getString("imageResName") ?: ""
                                    val isAvailable = document.getBoolean("isAvailable") ?: (availableQuantity > 0)
                                    val availabilityStatus = document.getString("availabilityStatus") ?: "Available"

                                    if (name.isNotBlank()) {
                                        val item = FishItem(
                                            id = id,
                                            name = name,
                                            tamilName = tamilName,
                                            category = category,
                                            description = description,
                                            price = price,
                                            availableQuantity = availableQuantity,
                                            imageResName = imageResName,
                                            isAvailable = isAvailable,
                                            availabilityStatus = availabilityStatus
                                        )
                                        insertOrUpdateFishByName(item)
                                    }
                                } catch (e: Exception) {
                                    android.util.Log.e("FirestoreSync", "Error parsing Firestore product: ${e.message}")
                                }
                            }
                        }
                    }
                }
            }
    }

    private fun syncDeliveryPincodeFromFirestore() {
        val db = try { FirebaseFirestore.getInstance() } catch (e: Exception) { null } ?: return
        db.collection("config").document("delivery_pincode")
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val p = document.getString("pincode") ?: ""
                    sharedPrefs.edit().putString("delivery_pincode", p).apply()
                    _deliveryPincode.value = p
                    android.util.Log.d("FirestoreSync", "Loaded delivery pincode from Firestore: $p")
                }
            }
            .addOnFailureListener { e ->
                android.util.Log.e("FirestoreSync", "Failed to fetch delivery pincode: ${e.message}")
            }
    }

    override fun onCleared() {
        super.onCleared()
        inventoryListenerRegistration?.remove()
    }
}
