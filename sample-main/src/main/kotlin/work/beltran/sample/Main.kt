package work.beltran.sample

import com.github.ericytsang.generated.work.beltran.sample.UnionType_VisitMeToo
import com.github.ericytsang.generated.work.beltran.sample.asUnionType

@Visitable(Hello::class,User::class,Int::class,Double::class)
class VisitMe

@Visitable(Hello::class,Int::class)
class VisitMeToo

class Hello

data class User(
        val name: String,
        val email: String)

fun main()
{
    val hello:UnionType_VisitMeToo = Hello().asUnionType()
    hello.accept(object:UnionType_VisitMeToo.Visitor<Unit>
    {
        override fun visit(element:Hello) = println("Hello")
        override fun visit(element:Int) = println("Int")
    })
}
