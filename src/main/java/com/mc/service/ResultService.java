package com.mc.service;

import com.mc.vo.ScoreVo;

/**
 * @author 江辉彬
 * @version 1.0
 */
public interface ResultService {
    ScoreVo getGameResult(ScoreVo scoreVo);

    ScoreVo getGameResultWithNoRankChange(ScoreVo scoreVo);
}
