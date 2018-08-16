package com.fayayo.job.core.zookeeper;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author dalizu on 2018/8/4.
 * @version v1.0
 * @desc zk配置  有相关配置则加载
 */
@ConditionalOnProperty(value ={"faya-job.zookeeperServer","faya-job.registerPath"})
@Configuration
public class ZkCuratorConfig {


    @Bean(initMethod = "init")
    public ZKCuratorClient zkCurator(){


        return new ZKCuratorClient();
    }


}
