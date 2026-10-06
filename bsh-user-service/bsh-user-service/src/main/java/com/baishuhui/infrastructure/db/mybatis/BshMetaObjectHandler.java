package com.baishuhui.infrastructure.db.mybatis;

import com.baishuhui.common.persistence.OperatorContext;
import com.baishuhui.common.persistence.OperatorInfo;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.Objects;

/**
 * 新增/修改时自动填充审计字段与 data_year、逻辑删除时间。
 *
 * @author wei yz
 */
@Component
public class BshMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        OperatorInfo op = OperatorContext.get();
        strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
        strictInsertFill(metaObject, "createUser", String.class, op.userId());
        strictInsertFill(metaObject, "createUserName", String.class, op.userName());
        strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);
        strictInsertFill(metaObject, "updateUser", String.class, op.userId());
        strictInsertFill(metaObject, "deleted", Integer.class, 0);
        strictInsertFill(metaObject, "dataYear", Integer.class, Year.now().getValue());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        OperatorInfo op = OperatorContext.get();
        strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, now);
        strictUpdateFill(metaObject, "updateUser", String.class, op.userId());
        // 逻辑删除带填充时 deleted 会被置为 1，同步写入 delete_time
        Object deleted = getFieldValByName("deleted", metaObject);
        // 空值分支判断
        if (deleted != null && Objects.equals(1, Integer.valueOf(deleted.toString()))) {
            strictUpdateFill(metaObject, "deleteTime", LocalDateTime.class, now);
        }
    }
}
