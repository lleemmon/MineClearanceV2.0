package com.mc.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author 江辉彬
 * @version 1.0
 */
@Data
@TableName("game_record_own")
public class Score {
    @TableId(type= IdType.AUTO)
    private Integer id;
    private String username;
    private String gameResult;
    private Integer useTime;
    private Integer boardLevel;
    private Integer winLoseScore;
    private Integer boardScore;
    private Integer mineScore;
    private Integer timeScore;
    private Integer totalScore;
    private Integer originalRank;
    private Integer newRank;
    private Integer rankFloat;
    private Date playTime;
}
