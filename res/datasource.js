var ioc = {
    dataSource: {
        type: "org.apache.commons.dbcp.BasicDataSource",
        events: {
            depose: 'close'
        },
        fields: {
            driverClassName: "com.mysql.jdbc.Driver",
            url : "jdbc:mysql://www.panming.net/caiyibang_test?useUnicode=true&characterEncoding=utf-8",
            username : "developer",
            password : "shengyu",
            initialSize: 5,
            maxActive: 10,
            maxIdle: 1000,
            timeBetweenEvictionRunsMillis : 3600000,
            minEvictableIdleTimeMillis : 3600000,
            defaultAutoCommit: false,
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