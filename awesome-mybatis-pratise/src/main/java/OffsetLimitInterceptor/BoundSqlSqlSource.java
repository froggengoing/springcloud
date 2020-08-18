package OffsetLimitInterceptor;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;

public class BoundSqlSqlSource implements SqlSource {
    public BoundSqlSqlSource(BoundSql newBoundSql) {
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        return null;
    }
}
