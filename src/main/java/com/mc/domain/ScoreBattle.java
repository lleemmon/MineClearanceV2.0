package com.mc.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName game_record_battle
 */
@TableName(value ="game_record_battle")
@Data
public class ScoreBattle implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    private String usernameOwn;

    /**
     * 
     */
    private String usernameOther;

    /**
     * 
     */
    private String gameResultOwn;

    /**
     * 
     */
    private String gameResultOther;

    /**
     * 
     */
    private String battleResult;

    /**
     * 
     */
    private Integer boardLevel;

    /**
     * 
     */
    private Integer winLoseScoreOwn;

    /**
     * 
     */
    private Integer winLoseScoreOther;

    /**
     * 
     */
    private Integer boardScoreOwn;

    /**
     * 
     */
    private Integer boardScoreOther;

    /**
     * 
     */
    private Integer timeScoreOwn;

    /**
     * 
     */
    private Integer timeScoreOther;

    /**
     * 
     */
    private Integer mineScoreOwn;

    /**
     * 
     */
    private Integer mineScoreOther;

    /**
     * 
     */
    private Integer totalScoreOwn;

    /**
     * 
     */
    private Integer totalScoreOther;

    /**
     * 
     */
    private Integer originalRank;

    /**
     * 
     */
    private Integer newRank;

    /**
     * 
     */
    private Integer rankFloat;

    /**
     * 
     */
    private Date playTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}