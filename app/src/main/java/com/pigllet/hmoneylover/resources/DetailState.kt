package com.pigllet.hmoneylover.resources

import com.pigllet.hmoneylover.model.Transaction

sealed class DetailState{
    object Loading: DetailState()
    object Empty: DetailState()
    data class Success(val transaction: Transaction): DetailState()
    data class Error(val exception:Throwable): DetailState()
}
