package work.beltran.sample

import kotlin.reflect.KClass

annotation class Visitable(vararg val classes:KClass<*>)
