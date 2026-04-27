package com.nevaxr.foundation.car

import android.accounts.Account
import android.accounts.AccountManager
import android.accounts.AccountManagerFuture
import android.Manifest
import android.content.Context
import android.os.Bundle
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import kotlin.coroutines.resume

/**
 * AccountManager configuration used to identify the active Tru.ID account.
 *
 * Consuming apps must be signed with the TOGG-approved keystore that is allowed to access the
 * Tru.ID authenticator. The library manifest declares GET_ACCOUNTS and the required package query,
 * but the platform can still reject access when the APK is not signed with the expected key.
 */
object TruIdAccountManagerConfig {
  const val ACCOUNT_TYPE: String = "Tru.ID"
  const val AUTH_TYPE: String = "bearer"
  const val AUTHENTICATOR_PACKAGE: String = "tr.com.togg.idcc.core.togg_toggid_account_authenticator_service"

  const val ACTIVE_FLAG: String = "tr.com.togg.account.flag.IS_ACTIVE"
  const val ACTION_STATUS_FLAG: String = "tr.com.togg.account.flag.ACTION_RESULT_STATUS"
  const val TOKEN_EXPIRE_TIME_FLAG: String = "tr.com.togg.account.flag.TOKEN_EXPIRE_TIME"
}

class TruIdAccountManagerProvider(
  context: Context,
  private val scope: CoroutineScope,
) : NCarPropertyProvider {

  private val appContext = context.applicationContext
  private val tokenHandlers = mutableSetOf<suspend (TruIdAuthResult) -> Unit>()
  private val tokenJobs = mutableListOf<Job>()
  private var isRunning = false

  fun subscribeToken(handler: suspend (TruIdAuthResult) -> Unit) {
    tokenHandlers += handler
    if (isRunning) {
      launchTokenRead(handler)
    }
  }

  suspend fun currentToken(): TruIdAuthResult {
    return getTruIdToken(appContext).first()
  }

  override suspend fun start() {
    if (isRunning) return
    isRunning = true
    tokenHandlers.forEach(::launchTokenRead)
  }

  override fun stop() {
    tokenJobs.forEach { it.cancel() }
    tokenJobs.clear()
    isRunning = false
  }

  override fun release() {
    stop()
    tokenHandlers.clear()
  }

  private fun launchTokenRead(handler: suspend (TruIdAuthResult) -> Unit) {
    tokenJobs += scope.launch {
      getTruIdToken(appContext).collect { result ->
        handler(result)
      }
    }
  }
}

object TruIdTokenProperty : NCarStateProperty<TruIdAuthResult?> {
  override val displayName: String = "Tru.ID Token"
  override val requiredPermissions: Set<String> = setOf(Manifest.permission.GET_ACCOUNTS)

  override fun subscribe(carService: NCarServiceBase, rate: NSensorRate): SharedFlow<TruIdAuthResult?> {
    val flow = MutableSharedFlow<TruIdAuthResult?>(replay = 1)
    flow.tryEmit(null)
    val provider = carService.propertyProviderOfOrNull(TruIdAccountManagerProvider::class)
      ?: return flow.asSharedFlow()
    provider.subscribeToken { result ->
      flow.emit(result)
    }
    return flow.asSharedFlow()
  }

  override suspend fun getProperty(carService: NCarServiceBase): TruIdAuthResult? {
    return carService.propertyProviderOfOrNull(TruIdAccountManagerProvider::class)?.currentToken()
  }

  override fun subscribeState(carService: NCarServiceBase, rate: NSensorRate): State<TruIdAuthResult?> {
    val state = mutableStateOf<TruIdAuthResult?>(null)
    val provider = carService.propertyProviderOfOrNull(TruIdAccountManagerProvider::class) ?: return state
    provider.subscribeToken { result ->
      state.value = result
    }
    return state
  }

  override fun subscribeStateFlow(carService: NCarServiceBase, rate: NSensorRate): StateFlow<TruIdAuthResult?> {
    val state = MutableStateFlow<TruIdAuthResult?>(null)
    val provider = carService.propertyProviderOfOrNull(TruIdAccountManagerProvider::class)
      ?: return state.asStateFlow()
    provider.subscribeToken { result ->
      state.emit(result)
    }
    return state.asStateFlow()
  }
}

/**
 * Status values returned by the Tru.ID authenticator.
 */
enum class TruIdAuthenticatorStatus {
  SUCCESS,
  REFRESH_TOKEN_ERROR,
  LOGIN_ERROR,
  SERVER_ERROR,
  CONNECTION_ERROR,
  NULL_ACCOUNT_ERROR,
  UNKNOWN_ERROR
}

/**
 * Gets the active Tru.ID bearer token from Android AccountManager.
 */
fun getTruIdToken(context: Context): Flow<TruIdAuthResult> = flow {
  try {
    emit(readTruIdToken(context))
  } catch (e: CancellationException) {
    throw e
  } catch (e: Exception) {
    Timber.e(e, "Tru.ID token read failed")
    emit(TruIdAuthResult.Error.AccountManagerError(e.message))
  }
}

private suspend fun readTruIdToken(context: Context): TruIdAuthResult {
  val accountManager = AccountManager.get(context)
  val accounts = accountManager.getAccountsByType(TruIdAccountManagerConfig.ACCOUNT_TYPE)

  if (accounts.isEmpty()) {
    return TruIdAuthResult.Error.NoAccount
  }

  val currentActiveAccount = accounts.firstOrNull { account ->
    accountManager.getUserData(account, TruIdAccountManagerConfig.ACTIVE_FLAG).equals("true", ignoreCase = true)
  }

  if (currentActiveAccount == null) {
    Timber.d("Current Account is null")
    return TruIdAuthResult.Error.NoAccount
  }

  return getAuthTokenSuspended(
    accountManager = accountManager,
    account = currentActiveAccount,
    authType = TruIdAccountManagerConfig.AUTH_TYPE,
  )
}

private suspend fun getAuthTokenSuspended(
  accountManager: AccountManager,
  account: Account,
  authType: String
): TruIdAuthResult = suspendCancellableCoroutine { continuation ->
  val future = accountManager.getAuthToken(
    account,
    authType,
    null,
    true,
    { future ->
      val result = authTokenCallback(future)
      if (continuation.isActive) {
        continuation.resume(result)
      }
    },
    null
  )

  continuation.invokeOnCancellation {
    future.cancel(true)
  }
}

private fun authTokenCallback(
  future: AccountManagerFuture<Bundle>,
  saveToken: (String) -> Unit = {}
): TruIdAuthResult {
  return try {
    val bundle = future.result
    val actionStatusResult = TruIdAuthenticatorStatus.entries
      .getOrNull(bundle.getInt(TruIdAccountManagerConfig.ACTION_STATUS_FLAG))
      ?: TruIdAuthenticatorStatus.UNKNOWN_ERROR

    if (actionStatusResult != TruIdAuthenticatorStatus.SUCCESS) {
      Timber.d(actionStatusResult.toString())
      return TruIdAuthResult.Error.AccountManagerError(actionStatusResult.toString())
    }

    val token = bundle.getString(AccountManager.KEY_AUTHTOKEN)
    if (token.isNullOrEmpty()) {
      return TruIdAuthResult.Error.EmptyToken
    }

    saveToken(token)
    TruIdAuthResult.Success(token)
  } catch (e: Exception) {
    Timber.e(e, "AccountManagerPlugin failed")
    TruIdAuthResult.Error.AccountManagerError(e.message)
  }
}

/**
 * Represents the result of a Tru.ID authentication operation.
 */
sealed class TruIdAuthResult {
  data class Success(val token: String) : TruIdAuthResult()

  sealed class Error(val message: String) : TruIdAuthResult() {
    data object EmptyToken : Error("EmptyToken")
    data object ExpiredToken : Error("ExpiredToken")
    data object NoAccount : Error("NoAccount")
    data class AccountManagerError(val errorMessage: String?) : Error(errorMessage ?: "AccountManagerError")
  }
}
