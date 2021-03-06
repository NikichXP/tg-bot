package com.nikichxp.tgbot.service

import com.nikichxp.tgbot.config.AppConfig
import com.nikichxp.tgbot.core.CurrentUpdateProvider
import com.nikichxp.tgbot.util.getContextChatId
import com.nikichxp.tgbot.util.getContextMessageId
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException.TooManyRequests
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForEntity
import org.springframework.web.client.postForEntity
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct


@Service
class TgOperations(
    private val updateProvider: CurrentUpdateProvider,
    private val restTemplate: RestTemplate,
    private val appConfig: AppConfig
) {

    @Value("\${TG_TOKEN}")
    private lateinit var token: String
    private lateinit var apiUrl: String

    private val scheduler = Executors.newScheduledThreadPool(1)

    @PostConstruct
    fun registerWebhook() {
        apiUrl = "https://api.telegram.org/bot$token"
        if (appConfig.appName.isEmpty()) return
        val response = restTemplate.getForEntity<String>(
            apiUrl + "/setWebhook?url=${generateUrl()}"
        )
        println(response)
    }

    fun sendMessage(chatId: Long, text: String, replyToMessageId: Long? = null, retryNumber: Int = 0) {
        val args = mutableListOf<Pair<String, Any>>(
            "chat_id" to chatId,
            "text" to text
        )

        replyToMessageId?.apply { args += "reply_to_message_id" to replyToMessageId }

        try {
            restTemplate.postForEntity<String>("$apiUrl/sendMessage", args.toMap())
        } catch (tooManyRequests: TooManyRequests) {
            if (retryNumber <= 5) {
                logger.warn("429 error reached: try #$retryNumber, chatId = $chatId, text = $text")
                scheduler.schedule(
                    { sendMessage(chatId, text, replyToMessageId, retryNumber + 1) },
                    1,
                    TimeUnit.MINUTES
                )
            } else {
                tooManyRequests.printStackTrace()
            }
        }
    }

    fun sendToCurrentChat(text: String) {
        updateProvider.update?.getContextChatId()?.let {
            sendMessage(it, text)
        } ?: logger.warn("Cannot send message reply in: $text")
    }
    fun replyToCurrentMessage(text: String) {
        updateProvider.update?.getContextChatId()?.let {
            sendMessage(it, text, updateProvider.update?.getContextMessageId())
        } ?: logger.warn("Cannot send message reply in: $text")
    }

    private fun generateUrl(): String = "https://${appConfig.appName}.herokuapp.com/handle"

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }
}