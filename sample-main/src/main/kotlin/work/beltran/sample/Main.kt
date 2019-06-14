package work.beltran.sample

import com.github.ericytsang.generated.work.beltran.sample.UnionTypeElement_Hello
import com.github.ericytsang.generated.work.beltran.sample.UnionType_VisitMe

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
    val hello = UnionTypeElement_Hello(Hello())
    hello.accept(object:UnionType_VisitMe.Visitor<Unit>
    {
        override fun visit(element:Hello) = println("Hello")
        override fun visit(element:Int) = println("Int")
        override fun visit(element:User) = println("User")
        override fun visit(element:Double) = println("Double")
    })
}