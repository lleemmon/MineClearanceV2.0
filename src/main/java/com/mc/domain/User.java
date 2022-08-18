package com.mc.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 江辉彬
 * @version 1.0
 */
@Data
@TableName("user")
public class User implements Serializable {
    /**
     * 用户名 最大长度20 并且只能由字母数字构成
     */
    @TableId(type= IdType.AUTO)
    private Integer id;
    private String username;

    @TableField(exist = false)
    private String uuid;

    private String password;
    private Integer rankOwn;
    private Integer rankBattle;
    private Integer winNumber;
    private Integer loseNumber;
    private Integer gameNumber;
    private Integer battleWinNumber;
    private Integer battleLoseNumber;
    private Integer battleGameNumber;
    private Date createTime;
}
