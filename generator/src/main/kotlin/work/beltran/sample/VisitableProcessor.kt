//package work.beltran.sample
//
//import com.google.auto.service.AutoService
//import com.squareup.kotlinpoet.ClassName
//import com.squareup.kotlinpoet.FileSpec
//import com.squareup.kotlinpoet.FunSpec
//import com.squareup.kotlinpoet.KModifier
//import com.squareup.kotlinpoet.ParameterSpec
//import com.squareup.kotlinpoet.ParameterizedTypeName
//import com.squareup.kotlinpoet.TypeName
//import com.squareup.kotlinpoet.TypeSpec
//import com.squareup.kotlinpoet.TypeVariableName
//import java.io.File
//import javax.annotation.processing.AbstractProcessor
//import javax.annotation.processing.ProcessingEnvironment
//import javax.annotation.processing.Processor
//import javax.annotation.processing.RoundEnvironment
//import javax.lang.model.SourceVersion
//import javax.lang.model.element.Element
//import javax.lang.model.element.TypeElement
//import kotlin.reflect.KClass
//
//@AutoService(Processor::class)
//class VisitableProcessor : AbstractProcessor()
//{
//    companion object
//    {
//        private const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
//        private val returnGenericType = TypeVariableName.invoke("Return")
//    }
//
//    override fun getSupportedAnnotationTypes() = mutableSetOf(Visitable::class.qualifiedName)
//    override fun getSupportedSourceVersion():SourceVersion = SourceVersion.latest()
//
//    private data class UnionTypeElement(
//            val wrappedValueType:KClass<*>)
//    {
//        val className = ClassName.bestGuess("${wrappedValueType.qualifiedName}_UnionTypeElement")
//    }
//    private data class UnionType(
//            val nameData:Element)
//    {
//        fun visitorClassName(processingEnvironment:ProcessingEnvironment):ClassName
//        {
//            return ClassName.bestGuess(className(processingEnvironment).canonicalName+".Visitor")
//        }
//        fun parameterizedVisitorClassName(processingEnvironment:ProcessingEnvironment):TypeName
//        {
//            return ParameterizedTypeName.get(
//                    visitorClassName(processingEnvironment),
//                    returnGenericType)
//        }
//        fun className(processingEnvironment:ProcessingEnvironment):ClassName
//        {
//            // todo: add ClassName to that stackoverflow.com post..or make your own question/answer
//            return ClassName.bestGuess("${nameData.packageName(processingEnvironment)}.${nameData.simpleName}_UnionType")
//        }
//    }
//
//    /**
//     * use this to create the visited element in the visitor pattern.
//     */
//    private data class UnionTypeElementHierarchy(
//            val unionTypeElement:UnionTypeElement,
//            val superTypes:Set<UnionType>)
//    {
//        fun toTypeSpec(processingEnvironment:ProcessingEnvironment):TypeSpec
//        {
//            return TypeSpec.classBuilder(unionTypeElement.className)
//                    .apply {
//                        superTypes.map {it.nameData.simpleName}
//                        superTypes
//                                .map {it.className(processingEnvironment)}
//                                .forEach {addSuperinterface(it)}
//                    }
//                    .addProperty("value",unionTypeElement.wrappedValueType,KModifier.PUBLIC)
//                    .addFunctions(superTypes
//                            .map {
//                                FunSpec.builder("accept")
//                                    .addParameter("visitor",it.visitorClassName(processingEnvironment))
//                                    .returns(returnGenericType)
//                                    .addStatement("return visitor.visit(this)")
//                                    .build()})
//                    .build()
//        }
//    }
//
//    /**
//     * use this to create the union type in the visitor pattern.
//     */
//    private data class UnionTypeVisitorHierarchy(
//            val unionType:UnionType,
//            val elements:Set<UnionTypeElementHierarchy>)
//    {
//        fun toTypeSpec(processingEnvironment:ProcessingEnvironment):TypeSpec
//        {
//            return TypeSpec.interfaceBuilder(unionType.className(processingEnvironment))
//                    .addFunction(FunSpec.builder("accept")
//                            .addTypeVariable(returnGenericType)
//                            .addParameter(
//                                    "visitor",
//                                    unionType.parameterizedVisitorClassName(processingEnvironment))
//                            .returns(returnGenericType)
//                            .build())
//                    .addType(TypeSpec.interfaceBuilder(unionType.visitorClassName(processingEnvironment))
//                            .addTypeVariable(returnGenericType)
//                            .addFunction(FunSpec.builder("visit")
//                                    .addParameters(elements
//                                            .map {it.unionTypeElement.className}
//                                            .map {
//                                                ParameterSpec.builder("element",it)
//                                                    .build()})
//                                    .returns(returnGenericType)
//                                    .build())
//                            .build())
//                    .build()
//        }
//    }
//
//    override fun process(set:MutableSet<out TypeElement>,roundEnv:RoundEnvironment):Boolean
//    {
//        println("process")
//
//        val unionTypes = roundEnv.getElementsAnnotatedWith(Visitable::class.java)
//                .map {UnionType(it)}
//
//        val unionTypeHierarchies = unionTypes
//                .flatMap()
//                {
//                    unionTypeSrc ->
//                    unionTypeSrc.nameData
//                            .getAnnotation(Visitable::class.java).classes.asIterable()
//                            .map {UnionTypeElement(it)}
//                            .map {unionTypeElement -> unionTypeSrc to unionTypeElement}
//                }
//                .groupBy {it.second}
//                .mapValues {it.value.map {it.first}}
//                .map {UnionTypeElementHierarchy(it.key,it.value.toSet())}
//
//        val unionTypeVisitors = unionTypeHierarchies
//                .flatMap {unionTypeHierarchy -> unionTypeHierarchy.superTypes.map {unionTypeHierarchy to it}}
//                .groupBy {it.second}
//                .mapValues {it.value.map {it.first}}
//                .map {UnionTypeVisitorHierarchy(it.key,it.value.toSet())}
//
//        unionTypeVisitors.forEach()
//        {
//            unionTypeVisitor ->
//            val className = unionTypeVisitor.unionType.className(processingEnv)
//            FileSpec.builder(
//                    className.packageName(),
//                    className.simpleName())
//                    .addType(unionTypeVisitor.toTypeSpec(processingEnv))
//                    .build()
//                    .writeTo(File(
//                            processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME],
//                            className.canonicalName+".kt"))
//        }
//
//        unionTypeHierarchies.forEach()
//        {
//            unionTypeElement ->
//            val className = unionTypeElement.unionTypeElement.className
//            FileSpec.builder(
//                    className.packageName(),
//                    className.simpleName())
//                    .addType(unionTypeElement.toTypeSpec(processingEnv))
//                    .build()
//                    .writeTo(File(
//                            processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME],
//                            className.canonicalName+".kt"))
//        }
//
//        return true
//    }
//}