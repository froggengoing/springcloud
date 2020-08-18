package com.froggengo.autoconfiguration;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.dao.support.DaoSupport;

import static org.springframework.util.Assert.notNull;

public class MySqlSessionDaoSupport extends DaoSupport {

    private SqlSession sqlSession;

    /*判断使用sqlsessionfactory还是template
    private boolean externalSqlSession;
    */
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        this.sqlSession = sqlSessionFactory.openSession();
    }

    public SqlSession getSqlSession() {
        return sqlSession;
    }

    @Override
    protected void checkDaoConfig() throws IllegalArgumentException {
        notNull(this.sqlSession, "Property 'sqlSessionFactory' or 'sqlSessionTemplate' are required");
    }

}
