package com.mc.vo;

import com.mc.domain.Score;
import lombok.Data;

/**
 * @author 江辉彬
 * @version 1.0
 */
@Data
public class ScoreVo extends Score {
    private Integer killMineNumber;
    private Integer totalMineNumber;
    private Integer openBoardNumber;
    private Integer totalBoardNumber;
}

