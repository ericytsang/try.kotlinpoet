package work.beltran.sample

@Visitable(Hello::class,User::class,Int::class)
class VisitMe

@Visitable(Hello::class,Int::class)
class VisitMeToo

@GenName
class Hello

@Kson
data class User(val name: String,
                val email: String)

fun main(args: Array<String>) {
    println("Hello ${Generated_Hello().getName()}")
    println("User to JSON")
    val user = User(
            name = "Test",
            email = "test@email.com"
    )
    println("User: $user")
    println("Json: ${user.toJson()}")
}