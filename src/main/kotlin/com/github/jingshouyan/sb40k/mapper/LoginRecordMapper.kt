package com.github.jingshouyan.sb40k.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.github.jingshouyan.sb40k.entity.LoginRecord
import org.apache.ibatis.annotations.Mapper

@Mapper
interface LoginRecordMapper : BaseMapper<LoginRecord>
