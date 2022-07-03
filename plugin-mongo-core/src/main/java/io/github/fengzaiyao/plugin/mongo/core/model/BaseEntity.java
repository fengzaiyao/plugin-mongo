package io.github.fengzaiyao.plugin.mongo.core.model;

import io.github.fengzaiyao.plugin.mongo.core.annotation.EntityLogicId;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

@Setter
@Getter
public class BaseEntity implements Serializable {

    /**
     * ObjectId
     */
    @Id
    protected String id;

    /**
     * 分布式ID
     */
    @EntityLogicId
    protected String bzId;

    /**
     * 记录创建时间
     */
    protected Long gmtCreate;

    /**
     * 记录修改时间
     */
    protected Long gmtModified;

    /**
     * 记录创建人ID
     */
    protected String createUid;

    /**
     * 记录创建人昵称
     */
    protected String createUname;

    /**
     * 记录修改人ID
     */
    protected String modifiedUid;

    /**
     * 记录修改人昵称
     */
    protected String modifiedUname;

    /**
     * 逻辑删除
     */
    protected Boolean delete;

    /**
     * 是否可用
     */
    protected Boolean enabled;
}