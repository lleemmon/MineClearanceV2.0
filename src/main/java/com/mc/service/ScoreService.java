package com.mc.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mc.domain.Score;

/**
 * @author 江辉彬
 * @version 1.0
 */
public interface ScoreService extends IService<Score> {
    String insertGameRecord(Score score);
}
