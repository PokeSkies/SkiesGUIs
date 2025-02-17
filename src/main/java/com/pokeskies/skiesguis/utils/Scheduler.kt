package com.pokeskies.skiesguis.utils

import com.pokeskies.skiesguis.SkiesGUIs
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap

object Scheduler {
    private val delays: ConcurrentHashMap<Int, MutableList<Job>> = ConcurrentHashMap()
    private var job: Job? = null

    @OptIn(DelicateCoroutinesApi::class)
    fun start() {
        if (job == null || job?.isActive == false) {
            job = GlobalScope.launch(Dispatchers.Default) {
                while (isActive) {
                    tick()
                    delay(50)
                }
            }
        }
    }

    fun stop() {
        job?.cancel()
        job = null
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun scheduleTask(delay: Int, action: Action) {
        val delayMillis = delay * 50L
        val job = GlobalScope.launch(Dispatchers.Default) {
            if (action.repeatInfinitely) {
                val predicateAction = action as PredicatedInfiniteAction
                while (isActive && !predicateAction.predicate()) {
                    predicateAction.onTick(delay)
                    delay(delayMillis)
                }
                if (isActive) predicateAction.onComplete()
            } else {
                repeat(delay) { currentDelay ->
                    action.onTick(delay - currentDelay)
                    delay(50)
                }
                action.onComplete()
            }
        }
        delays.compute(delay) { _, existing ->
            if (existing != null) {
                existing.add(job)
                existing
            } else {
                mutableListOf(job)
            }
        }
    }

    inline fun scheduleTask(delay: Int = 0, builder: ActionScope.() -> Unit) {
        val action = Action().apply {
            ActionScope(this).builder()
        }
        scheduleTask(delay, action)
    }

    inline fun scheduleMainThreadTask(delay: Int, builder: ActionScope.() -> Unit) {
        val action = Action().apply {
            ActionScope(this).builder()
        }
        scheduleMainThreadTask(delay, action)
    }

    fun addStaggeredDelay(elseDelay: Int, action: Action) {
        val staggeredDelay = delays.keys.maxOrNull() ?: elseDelay
        scheduleTask(staggeredDelay + 5, action)
    }

    private fun tick() {
        val toRemove = mutableSetOf<Int>()
        delays.forEach { (delay, jobs) ->
            if (delay <= 0) {
                jobs.removeAll { !it.isActive }
                if (jobs.isEmpty()) {
                    toRemove.add(delay)
                }
            } else {
                val remainingJobs = jobs.filter { job ->
                    !(job.isCompleted && job.children.all { it.isCompleted })
                }.toMutableList()
                delays[delay - 1] = remainingJobs
                if (delays[delay - 1]?.isEmpty() == true) {
                    delays.remove(delay - 1)
                }
                toRemove.add(delay)
            }
        }
        toRemove.forEach { delays.remove(it) }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun scheduleMainThreadTask(delay: Int, action: Action) {
        scheduleTask(delay, DelayedAction({
            SkiesGUIs.INSTANCE.server.execute {
                if (action.repeatInfinitely) {
                    val predicateAction = action as PredicatedInfiniteAction
                    if (!predicateAction.predicate()) {
                        predicateAction.onTick(delay)
                        scheduleMainThreadTask(delay, action)
                    } else {
                        predicateAction.onComplete()
                    }
                } else {
                    action.onComplete()
                }
            }
        }))
    }

    open class Action(var onComplete: () -> Unit = {}, var onTick: (Int) -> Unit = {}, val repeatInfinitely: Boolean = false) {
        companion object {
            // DSL builders for Action
            inline fun action(builder: ActionScope.() -> Unit): Action {
                return Action().apply {
                    ActionScope(this).builder()
                }
            }

            fun onTick(builder: (Int) -> Unit): ActionScope.() -> Unit {
                return { onTick = builder }
            }

            fun onComplete(builder: () -> Unit): ActionScope.() -> Unit {
                return { onComplete = builder }
            }

            fun repeatInfinitely(value: Boolean): ActionScope.() -> Unit {
                return { repeatInfinitely = value }
            }
        }
    }

    class DelayedAction(onComplete: () -> Unit, onTick: (Int) -> Unit = {}) : Action(onComplete, onTick) {
        companion object {
            // DSL builders for DelayedAction
            inline fun delayedAction(builder: DelayedActionScope.() -> Unit): DelayedAction {
                return DelayedAction({}).apply {
                    DelayedActionScope(this).builder()
                }
            }

            fun onTick(builder: (Int) -> Unit): DelayedActionScope.() -> Unit {
                return { onTick = builder }
            }

            fun onComplete(builder: () -> Unit): DelayedActionScope.() -> Unit {
                return { onComplete = builder }
            }
        }
    }

    class PredicatedInfiniteAction(
        onComplete: () -> Unit,
        onTick: (Int) -> Unit = {},
        var predicate: () -> Boolean
    ) : Action(onComplete, onTick, true) {
        companion object {
            // DSL builders for PredicatedInfiniteAction
            inline fun predicatedInfiniteAction(builder: PredicatedInfiniteActionScope.() -> Unit): PredicatedInfiniteAction {
                return PredicatedInfiniteAction({}, {}, { return@PredicatedInfiniteAction true }).apply {
                    PredicatedInfiniteActionScope(this).builder()
                }
            }

            fun onTick(builder: (Int) -> Unit): PredicatedInfiniteActionScope.() -> Unit {
                return { onTick = builder }
            }

            fun onComplete(builder: () -> Unit): PredicatedInfiniteActionScope.() -> Unit {
                return { onComplete = builder }
            }

            fun predicate(builder: () -> Boolean): PredicatedInfiniteActionScope.() -> Unit {
                return { predicate = builder }
            }
        }
    }

    class ActionScope(val action: Action) {
        var onComplete: () -> Unit = action.onComplete
        infix fun onComplete(builder: () -> Unit) {
            onComplete = builder
        }

        var onTick: (Int) -> Unit = action.onTick
        infix fun onTick(builder: (Int) -> Unit) {
            onTick = builder
        }

        var repeatInfinitely: Boolean = action.repeatInfinitely
        infix fun repeatInfinitely(value: Boolean) {
            repeatInfinitely = value
        }
    }

    class DelayedActionScope(val action: DelayedAction) {
        var onComplete: () -> Unit = action.onComplete
        infix fun onComplete(builder: () -> Unit) {
            onComplete = builder
        }

        var onTick: (Int) -> Unit = action.onTick
        infix fun onTick(builder: (Int) -> Unit) {
            onTick = builder
        }
    }

    class PredicatedInfiniteActionScope(val action: PredicatedInfiniteAction) {
        var onComplete: () -> Unit = action.onComplete
        infix fun onComplete(builder: () -> Unit) {
            onComplete = builder
        }

        var onTick: (Int) -> Unit = action.onTick
        infix fun onTick(builder: (Int) -> Unit) {
            onTick = builder
        }

        var predicate: () -> Boolean = action.predicate
        infix fun predicate(builder: () -> Boolean) {
            predicate = builder
        }
    }
}
