package com.ktmi.tmi.client.builder.scopes.filters

import com.ktmi.tmi.client.builder.TwitchDsl
import com.ktmi.tmi.client.builder.TwitchScope
import com.ktmi.tmi.messages.*
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlin.coroutines.CoroutineContext

/**
 * [TwitchDsl] scope used to filter out [TwitchMessage]s using given predicate (which is set in the body with [withPredicate])
 * @param parent parent scope where messages are forwarded and from  where main [Flow] of [TwitchMessage]s is retrieved
 * @param coroutineContext [CoroutineContext] used for creating [TwitchMessage] listeners
 */
class FilterScope(
    parent: TwitchScope,
    coroutineContext: CoroutineContext
) : TwitchScope(parent,coroutineContext + CoroutineName("UserTwitch")) {
    private var predicate: (suspend (TwitchMessage) -> Boolean) = { true }

    /**
     * Sets predicate of [FilterScope] used in filtering incoming [TwitchMessage]s
     */
    infix fun withPredicate(filter: suspend (TwitchMessage) -> Boolean) { predicate = filter }

    override suspend fun getTwitchFlow(): Flow<TwitchMessage> =
        super.getTwitchFlow()
            .filter(predicate)
}

/**
 * [TwitchDsl] builder function for [FilterScope]
 * @param block body of the DSL ([FilterScope])
 */
@TwitchDsl
inline fun TwitchScope.filter(block: FilterScope.() -> Unit) =
    FilterScope(this, coroutineContext).apply(block)