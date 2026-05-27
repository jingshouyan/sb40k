package com.github.jingshouyan.sb40k.config

import com.baomidou.mybatisplus.extension.ddl.IDdl
import org.springframework.stereotype.Component
import java.util.function.Consumer
import javax.sql.DataSource

@Component
class MybatisPlusDdl(
    private val dataSource: DataSource
) : IDdl {

    override fun runScript(consumer: Consumer<DataSource>) {
        consumer.accept(dataSource)
    }

    override fun getSqlFiles(): List<String> {
        return listOf("db/schema.sql")
    }
}
