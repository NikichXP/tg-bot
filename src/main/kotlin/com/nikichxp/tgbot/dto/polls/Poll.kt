package com.nikichxp.tgbot.dto.polls

import com.nikichxp.tgbot.dto.MessageEntity
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Contains information about a poll.
 * https://core.telegram.org/bots/api#poll
 */
data class Poll(
    @JsonProperty(PollFields.ID) val id: Long,
    @JsonProperty(PollFields.QUESTION) val question: String,
    @JsonProperty(PollFields.OPTIONS) val options: List<PollOption>,
    @JsonProperty(PollFields.TOTAL_VOTER_COUNT) val totalVoterCount: Int,
    @JsonProperty(PollFields.IS_CLOSED) val isClosed: Boolean,
    @JsonProperty(PollFields.IS_ANONYMOUS) val isAnonymous: Boolean,
    @JsonProperty(PollFields.TYPE) val type: PollType,
    @JsonProperty(PollFields.ALLOWS_MULTIPLE_ANSWERS) val allowsMultipleAnswers: Boolean,
    @JsonProperty(PollFields.CORRECT_OPTION_ID) val correctOptionId: Int? = null,
    @JsonProperty(PollFields.EXPLANATION) val explanation: String? = null,
    @JsonProperty(PollFields.EXPLANATION_ENTITIES) val explanationEntities: List<MessageEntity>? = null,
    @JsonProperty(PollFields.OPEN_PERIOD) val openPeriod: Int? = null,
    @JsonProperty(PollFields.CLOSE_DATE) val closeData: Long? = null
)
