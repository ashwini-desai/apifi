package apifi.codegen

import apifi.helpers.toKotlinPoetType
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

object ExceptionFileBuilder {

    fun build(exception: ExceptionDetailsHolder, basePackageName: String): FileSpec {
        val packageName = "$basePackageName.exceptions"
        val exceptionClassName = exception.exceptionClassName

        val builder = FileSpec.builder(packageName, "$exceptionClassName.kt")
        builder.addType(
                TypeSpec.classBuilder(ClassName(packageName, exceptionClassName))
                        .superclass(Exception::class)
                        .addSuperclassConstructorParameter("%L", "message")
                        .primaryConstructor(
                                FunSpec.constructorBuilder()
                                        .addParameter(ParameterSpec.builder("message", String::class).build()).build()
                        ).build())

        builder.addType(
                TypeSpec.classBuilder(ClassName(packageName, "Global${exceptionClassName}Handler"))
                        .addAnnotation(ClassName("javax.inject", "Singleton"))
                        .addSuperinterface(
                                ClassName("io.micronaut.http.server.exceptions", "ExceptionHandler")
                                        .parameterizedBy(ClassName(packageName, exceptionClassName).copy(nullable = true),
                                                         ClassName("io.micronaut.http", "HttpResponse").parameterizedBy("String".toKotlinPoetType()))
                        )
                        .addFunction(FunSpec.builder("handle")
                                .addParameter("request", ClassName("io.micronaut.http", "HttpRequest").parameterizedBy("Any".toKotlinPoetType()).copy(nullable = true))
                                .addParameter("exception", ClassName(packageName, exceptionClassName).copy(nullable = true))
                                .returns(ClassName("io.micronaut.http", "HttpResponse").parameterizedBy("String".toKotlinPoetType()))
                                .addStatement("val msg = exception?.conversionError?.cause?.localizedMessage ?: \"${exception.defaultExceptionMessage}\"")
                                .addStatement("HttpResponse.status<String>(HttpStatus.valueOf(${exception.status}), msg)")
                                .build()
                        )
                        .build()
        )
        return builder.build()
    }

}


/*
 class BadRequestException(string: String) : Exception(string)

@Singleton
class GlobalBadRequestExceptionHandler : ExceptionHandler<BadRequestException?, HttpResponse<String>> {
    override fun handle(request: HttpRequest<*>?, exception: BadRequestException?): HttpResponse<String> {
        val msg = exception?.conversionError?.cause?.localizedMessage ?: "Bad request"
        return HttpResponse.badRequest(msg)
    }
}
*/