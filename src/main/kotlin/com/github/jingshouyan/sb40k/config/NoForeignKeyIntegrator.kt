package com.github.jingshouyan.sb40k.config

import org.hibernate.boot.Metadata
import org.hibernate.boot.spi.BootstrapContext
import org.hibernate.engine.spi.SessionFactoryImplementor
import org.hibernate.integrator.spi.Integrator
import org.hibernate.mapping.ForeignKey
import org.hibernate.mapping.Table

class NoForeignKeyIntegrator : Integrator {

    override fun integrate(
        metadata: Metadata?,
        bootstrapContext: BootstrapContext?,
        sessionFactory: SessionFactoryImplementor?
    ) {
        // 在 Metadata 构建完成后，遍历所有表，忽略外键
        metadata?.entityBindings?.forEach { entity ->
            val table: Table? = entity.table
            table?.foreignKeyCollection?.forEach { fk: ForeignKey ->
                fk.disableCreation()
            }
        }
    }
}