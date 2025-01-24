package ru.yandex.praktikumchatapp.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.retryWhen

class ChatRepository(
    private val api: ChatApi = ChatApi()
) {

    fun getReplyMessage(): Flow<String> {
        var currentDelay = DEFAULT_DELAY

        return api.getReply()
            .retryWhen { cause, _ ->
                if (cause is Exception) {
                    delay(currentDelay)
                    currentDelay *= DELAY_FACTOR
                    true
                } else {
                    false
                }
            }
            .onEach {
                currentDelay = DEFAULT_DELAY
            }
    }

    companion object {
        const val DELAY_FACTOR = 2
        const val DEFAULT_DELAY = 1000L
    }
}


/**
 * Так как в задании не указано, что делать в случае с превышением количества повторов -
 * мне прилось не использовать данную переменную, т.к. рано или поздно она бы привела к крашу приложения
 * а в ТЗ указано, что приложение должно работать стабильно.
 */