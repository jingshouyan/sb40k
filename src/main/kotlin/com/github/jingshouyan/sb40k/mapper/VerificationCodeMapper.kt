package com.github.jingshouyan.sb40k.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.github.jingshouyan.sb40k.entity.VerificationCode
import org.apache.ibatis.annotations.Mapper

@Mapper
interface VerificationCodeMapper : BaseMapper<VerificationCode>
