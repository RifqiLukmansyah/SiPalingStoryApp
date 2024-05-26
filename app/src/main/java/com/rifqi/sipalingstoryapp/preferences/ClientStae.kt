package com.rifqi.sipalingstoryapp.preferences

sealed class ClientState<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T): ClientState<T>(data)
    class Loading<T>: ClientState<T>()
    class Error<T>(message: String, data: T? = null): ClientState<T>(data, message)
}