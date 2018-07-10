var ioc = {
    dataSource: {
        type: "com.alibaba.druid.pool.DruidDataSource",
        events: {depose: "close"},
        fields: {
            driverClassName: "com.mysql.jdbc.Driver",
            url: "jdbc:mysql://cyb.child2newness.com/caiyibang_test?useUnicode=true&characterEncoding=utf-8",
            username: "developer",
            password: "Shengyu@123",
            initialSize: 5,
            maxActive: 30,
            minIdle: 5,
            defaultAutoCommit: false,
            timeBetweenEvictionRunsMillis: 1800000,
            minEvictableIdleTimeMillis: 1800000
        }
    },
    dao: {
        type: "org.nutz.dao.impl.NutDao",
        args: [
            {
                refer: 'dataSource'
            }
        ]
    }
};