package apifi.codegen

import apifi.helpers.toKotlinPoetType
import apifi.parser.models.Operation
import apifi.parser.models.Param
import apifi.parser.models.ParamType
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import io.swagger.v3.oas.models.responses.ApiResponses
import org.apache.http.HttpRequest
import org.apache.http.message.BasicHttpRequest
import org.apache.http.protocol.HTTP


fun Operation.queryParamSpecs() = params?.filter { it.type == ParamType.Query }?.map(QueryParamBuilder::build)
        ?: emptyList()

fun Operation.queryParamSpecNames() = params?.filter { it.type == ParamType.Query }?.map(QueryParamBuilder::build)?.map { it.name }
        ?: emptyList()

fun Operation.queryParams(): List<Param> = params?.filter { it.type == ParamType.Query } ?: emptyList()

fun Operation.pathParamSpecs() = params?.filter { it.type == ParamType.Path }?.map(PathVariableBuilder::build)
        ?: emptyList()

fun Operation.pathParamSpecNames() = params?.filter { it.type == ParamType.Path }?.map(PathVariableBuilder::build)?.map { it.name }
        ?: emptyList()

fun Operation.pathParams(): List<Param> = params?.filter { it.type == ParamType.Path } ?: emptyList()

fun Operation.headerParamSpecs() = params?.filter { it.type == ParamType.Header }?.map(HeaderBuilder::build)
        ?: emptyList()

fun Operation.requestParams(modelMapping: Map<String, String>) = request?.let {
    listOf(ParameterSpec.builder("body", it.type.toKotlinPoetType(modelMapping))
            .addAnnotation(ClassName(micronautHttpAnnotationPackage, "Body"))
            .build())
} ?: emptyList()

fun Operation.requestParamNames(modelMapping: Map<String, String>) = request?.let {
    listOf(ParameterSpec.builder("body", it.type.toKotlinPoetType(modelMapping))
            .addAnnotation(ClassName(micronautHttpAnnotationPackage, "Body"))
            .build())
}?.map { it.name } ?: emptyList()

fun Operation.returnType(modelMapping: Map<String, String>): ParameterizedTypeName? =
    responses?.let { res ->
        if (res.size == 1 && res.first().defaultOrStatus == ApiResponses.DEFAULT) ClassName(micronautHttpPackage, "HttpResponse").parameterizedBy(res[0].type.toKotlinPoetType(modelMapping))
        else if (res.size > 1 && res.map { it.defaultOrStatus }.all { it.startsWith("2") }) error("Invalid responses defined for operation with identifier: ${this.name}. Has more than one 2xx responses defined")
        else res.firstOrNull { it.defaultOrStatus.startsWith("2") }?.let { ClassName(micronautHttpPackage, "HttpResponse").parameterizedBy(it.type.toKotlinPoetType(modelMapping)) }
    }