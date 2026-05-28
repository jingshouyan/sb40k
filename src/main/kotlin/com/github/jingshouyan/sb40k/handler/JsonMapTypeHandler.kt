package com.github.jingshouyan.sb40k.handler

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.ibatis.type.BaseTypeHandler
import org.apache.ibatis.type.JdbcType
import org.apache.ibatis.type.MappedJdbcTypes
import org.apache.ibatis.type.MappedTypes
import java.sql.CallableStatement
import java.sql.PreparedStatement
import java.sql.ResultSet

@MappedTypes(Map::class)
@MappedJdbcTypes(JdbcType.VARCHAR)
class JsonMapTypeHandler : BaseTypeHandler<Map<String, String>?>() {

    private val mapper = ObjectMapper()

    private val typeRef = object : TypeReference<Map<String, String>>() {}

    override fun setNonNullParameter(ps: PreparedStatement, i: Int, parameter: Map<String, String>?, jdbcType: JdbcType) {
        ps.setString(i, if (parameter == null) null else mapper.writeValueAsString(parameter))
    }

    override fun getNullableResult(rs: ResultSet, columnName: String): Map<String, String>? {
        val raw = rs.getString(columnName)
        return if (raw == null) null else mapper.readValue(raw, typeRef)
    }

    override fun getNullableResult(rs: ResultSet, columnIndex: Int): Map<String, String>? {
        val raw = rs.getString(columnIndex)
        return if (raw == null) null else mapper.readValue(raw, typeRef)
    }

    override fun getNullableResult(rs: CallableStatement, columnIndex: Int): Map<String, String>? {
        val raw = rs.getString(columnIndex)
        return if (raw == null) null else mapper.readValue(raw, typeRef)
    }
}
