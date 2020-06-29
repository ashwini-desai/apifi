package apifi.codegen

import io.kotlintest.matchers.string.shouldContain
import io.kotlintest.shouldBe
import io.kotlintest.specs.DescribeSpec

class ExceptionFileBuilderTest : DescribeSpec( {

    describe("should generate exception and global exception handler for a given exception") {

        val exceptionFileSpec = ExceptionFileBuilder.build(ExceptionDetailsHolder(123, "SomeException", "Some error occurred"), "com.abc")

        it("should generate exception class") {
            exceptionFileSpec.members[0].toString() shouldBe
                    "class SomeException(\n" +
                    "  message: kotlin.String\n" +
                    ") : java.lang.Exception(message)\n"
        }

        it("should generate global exception handler for the exception class with exception's default message and status") {
            exceptionFileSpec.members[1].toString() shouldContain
                    "class GlobalSomeExceptionHandler : io.micronaut.http.server.exceptions.ExceptionHandler<com.abc.exceptions.SomeException?, io.micronaut.http.HttpResponse<String>> {\n" +
                    "  fun handle(request: io.micronaut.http.HttpRequest<Any>?, exception: com.abc.exceptions.SomeException?): io.micronaut.http.HttpResponse<String> {\n" +
                    "    val msg = exception?.conversionError?.cause?.localizedMessage ?: \"Some error occurred\"\n" +
                    "    HttpResponse.status<String>(HttpStatus.valueOf(123), msg)\n" +
                    "  }\n" +
                    "}"
        }

        it("exception handler class should have @Singleton annotation") {
            exceptionFileSpec.members[1].toString() shouldContain "javax.inject.Singleton"
        }
    }

})