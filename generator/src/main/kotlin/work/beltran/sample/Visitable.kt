package work.beltran.sample

import javax.lang.model.type.MirroredTypesException
import javax.lang.model.type.TypeMirror
import kotlin.reflect.KClass

annotation class Visitable(vararg val classes:KClass<*>)

val Visitable.typeMirrors:List<TypeMirror> get()
{
    try
    {
        classes
        throw Exception("you're not supposed to be here!")
    }
    catch (e:MirroredTypesException)
    {
        return e.typeMirrors
    }
}
