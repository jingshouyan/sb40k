package com.github.jingshouyan.sb40k.config

import org.hibernate.boot.model.naming.Identifier
import org.hibernate.boot.model.naming.PhysicalNamingStrategy
import org.hibernate.boot.model.naming.PhysicalNamingStrategySnakeCaseImpl
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment

class MyNamingStrategy : PhysicalNamingStrategy, PhysicalNamingStrategySnakeCaseImpl() {

    override fun toPhysicalTableName(
        logicalName: Identifier?,
        jdbcEnvironment: JdbcEnvironment?
    ): Identifier? {
        val id = super.toPhysicalTableName(logicalName, jdbcEnvironment)
        return Identifier.toIdentifier("t_${id?.text}")
    }

    override fun toPhysicalColumnName(logicalName: Identifier?, jdbcEnvironment: JdbcEnvironment?): Identifier? {
        val id = super.toPhysicalColumnName(logicalName, jdbcEnvironment)
        return Identifier.toIdentifier("c_${id?.text}")
    }

}