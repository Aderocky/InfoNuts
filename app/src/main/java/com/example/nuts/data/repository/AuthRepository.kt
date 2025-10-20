package com.example.nuts.data.repository

import android.content.Context
import android.util.Log
import com.example.nuts.data.entity.UserEntity
import com.example.nuts.data.pref.SharePrefModel
import com.example.nuts.data.pref.SharePreferencesUser
import com.example.nuts.data.pref.dataStore
import com.example.nuts.state.AuthState
import com.example.nuts.state.ResultState
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.exceptions.BadRequestRestException
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.RealtimeChannel
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.withContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class AuthRepository(
    private val sbClient: SupabaseClient,
    context: Context
) {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: Flow<AuthState> = _authState

    private val _userState = MutableStateFlow<ResultState<SharePrefModel>>(ResultState.Loading)

    val userState: StateFlow<ResultState<SharePrefModel>>
        get() = _userState

    private var realtimeChannel: RealtimeChannel? = null

    init {
        CoroutineScope(Dispatchers.Main).launch {
            checkAuthSession()
            checkUserSession()
        }
    }
    private val userPref = SharePreferencesUser(context.dataStore)

    suspend fun refreshPremiumEveryOpen() {
        val session = userPref.getSessionData().first()
        if (session.id.isNotBlank()) {
            val user = fetchUserProfile(session.id)
            updateIsPremium(user.isPremium)
        }
    }
    fun register(
        email: String,
        name: String,
        password: String,
        confirmPassword: String
    ) {
        when {
            email.isBlank() || name.isBlank() || password.isBlank() || confirmPassword.isBlank() -> {
                _authState.value = AuthState.Error("Tolong isi semua kolom")
            }
            password != confirmPassword -> {
                _authState.value = AuthState.Error("Password tidak sama, periksa kembali")
            }
            else -> {
                _authState.value = AuthState.Loading
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        sbClient.auth.signUpWith(Email) {
                            this.email = email
                            this.password = password
                        }
                        val currentUser = sbClient.auth.currentUserOrNull()
                        if (currentUser != null) {
                            val userId = currentUser.id

                            sbClient.from("profile").insert(
                                UserEntity(
                                    userId = userId,
                                    name = name,
                                    email = email
                                )
                            )
                            _authState.value = AuthState.Success("Registrasi berhasil!")
                        }else {
                            _authState.value = AuthState.Error("Gagal mendapatkan userId dari Supabase")
                        }
                    } catch (e: Exception) {
                        _authState.value = AuthState.Error(e.message ?: "Terjadi kesalahan")
                    }
                }
            }
        }
    }

    fun login(
        email: String,
        password: String,
    ){
        when{
            email.isBlank() || password.isBlank() -> {
                _authState.value = AuthState.Error("Tolong isi semua kolom")
            }
            else -> {
                _authState.value = AuthState.Loading
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        sbClient.auth.signInWith(Email) {
                            this.email = email
                            this.password = password
                        }
                        val currentUser = sbClient.auth.currentUserOrNull()
                        if (currentUser == null) {
                            _authState.value =
                                AuthState.Error("Gagal login, periksa kembali email dan password")
                        }

                        val userId = currentUser?.id.orEmpty()

                        val user = fetchUserProfile(userId)
                        userPref(email, user.name, user.isPremium, user.isAdmin)
                    } catch (e: BadRequestRestException) {
                        _authState.value = AuthState.Error(e.error)
                    } catch (e: Exception) {
                        _authState.value = AuthState.Error(e.message ?: "Terjadi kesalahan")
                    }
                }
            }
        }
    }
    private suspend fun fetchUserProfile(userId: String): UserEntity {
        return sbClient.from("profile").select {
            filter { eq("id", userId) }
        }.decodeSingle()
    }
    private suspend fun userPref(
        email: String,
        name: String = "",
        isPremium: Boolean = false,
        isAdmin: Boolean= false,
    ){
        saveUserSession(
            SharePrefModel(
                id = sbClient.auth.currentUserOrNull()?.id.toString(),
                name = name,
                email = email,
                isPremium = isPremium,
                token = sbClient.auth.currentAccessTokenOrNull(),
                isLogin = true,
                isAdmin = isAdmin
            )
        )
        checkAuthSession()
        checkUserSession()
    }

    private suspend fun saveUserSession(user: SharePrefModel) {
        userPref.saveSessionData(user)
    }

    private fun checkAuthSession() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val session = userPref.getSessionData().first()
                if (session.isLogin && session.email.isNotBlank()){
                    _authState.value = AuthState.Authenticated
                }else{
                    _authState.value = AuthState.Unauthenticated
                }
            }catch (e: Exception){
                Log.e("Error check auth", e.message.toString())
            }
        }
    }

    suspend fun fetchUsers(): List<UserEntity>{
        val user = sbClient.from("profile").select {
            filter { eq("is_admin",false) }
            order(column = "is_premium", order = Order.DESCENDING)
        }.decodeList<UserEntity>()

        return user
    }
    private suspend fun checkUserSession() {
        userPref.getSessionData()
            .catch { _userState.value = ResultState.Error(it.message.toString()) }
            .collect { user ->
                _userState.value = ResultState.Success(user)
            }
    }
    fun resetState() {
        _authState.value = AuthState.Unauthenticated
    }
    suspend fun logout() {
        withContext(Dispatchers.IO) {
            try {
                sbClient.auth.signOut()
                userPref.clearSessionData()

                withContext(Dispatchers.Main) {
                    _authState.value = AuthState.Unauthenticated
                    _userState.value = ResultState.Success(
                        SharePrefModel(
                            id = "",
                            name = "",
                            email = "",
                            isPremium = false,
                            token = null,
                            isLogin = false,
                            isAdmin = false
                        )
                    )
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _authState.value = AuthState.Error(e.message ?: "Gagal logout")
                }
            }
        }
    }
    suspend fun updateUser(email: String, name: String, isPremium: Boolean, expDate: String): ResultState<UserEntity> {
        return try {
            val user = sbClient.from("profile").update(
                {
                    set("name", name)
                    set("is_premium", isPremium)
                    set("exp_date", if(expDate == "null") null else expDate)
                }
            ) {
                select()
                filter {
                    eq("email", email)
                }
            }.decodeSingle<UserEntity>()

            ResultState.Success(user)
        } catch (e: BadRequestRestException) {
            ResultState.Error(e.error)
        } catch (e: Exception) {
            ResultState.Error(e.message.orEmpty())
        }
    }
    private suspend fun updateIsPremium(isPremium: Boolean) {
        userPref.updatePremium(isPremium = isPremium)
        checkUserSession()
    }
    suspend fun getCurrentUserEmail(): String? {
        return try {
            val session = userPref.getSessionData().firstOrNull()
            session?.email
        } catch (e: Exception) {
            null
        }
    }
    suspend fun realtimeDb(scope: CoroutineScope) {
        if (realtimeChannel != null) {
            return
        }

        realtimeChannel = sbClient.channel("public:profile")

        val changeFlow = realtimeChannel!!.postgresChangeFlow<PostgresAction.Update>(
            schema = "public"
        ) {
            table = "profile"
        }

        changeFlow.onEach {
            val json = it.record.toString()
            try {
                val data = Json.decodeFromString<UserEntity>(json)
                val session = userPref.getSessionData().first()

                if (data.email == session.email) {
                    updateIsPremium(data.isPremium)
                }
            } catch (e: Exception) {
                Log.e("Realtime", "Error parsing data: ${e.message}")
            }
        }.launchIn(scope)

        realtimeChannel!!.subscribe()
    }
}
