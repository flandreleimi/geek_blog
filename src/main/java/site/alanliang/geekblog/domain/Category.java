package site.alanliang.geekblog.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @Descriptin 文章分类
 * @Author AlanLiang
 * Date 2020/4/5 21:00
 * Version 1.0
 **/
@Data
@TableName("t_category")
public class Category {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private Date createTime;
}
