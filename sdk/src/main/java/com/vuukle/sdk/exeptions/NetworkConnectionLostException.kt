package com.vuukle.sdk.exeptions

// Now accepts an optional diagnostic message while preserving the
// existing no-arg signature for backward compat with any caller still
// instantiating it as `NetworkConnectionLostException()`.
class NetworkConnectionLostException(message: String = "Network connection lost")
    : VuukleException(message)
