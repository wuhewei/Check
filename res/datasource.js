var ioc = {
    dataSource: {
        type: "org.apache.commons.dbcp.BasicDataSource",
        events: {
            depose: 'close'
        },
        fields: {
            driverClassName: "com.mysql.jdbc.Driver",
            url : "jdbc:mysql://localhost/check?useUnicode=true&characterEncoding=utf-8",
            username : "root",
            password : "123456",
            initialSize: 5,
            maxActive: 10,
            maxIdle: 10,
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