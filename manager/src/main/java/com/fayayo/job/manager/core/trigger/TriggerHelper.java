package com.fayayo.job.manager.core.trigger;

import com.fayayo.job.common.enums.ResultEnum;
import com.fayayo.job.common.exception.CommonException;
import com.fayayo.job.entity.JobInfo;
import com.fayayo.job.manager.config.SpringHelper;
import com.fayayo.job.manager.core.cluster.support.Cluster;
import com.fayayo.job.manager.core.cluster.support.ClusterSupport;
import com.fayayo.job.manager.service.JobInfoService;
import lombok.extern.slf4j.Slf4j;
/**
 * @author dalizu on 2018/8/10.
 * @version v1.0
 * @desc
 */
@Slf4j
public class TriggerHelper {


     /**
       *@描述 处理job的业务

       *@参数  job的唯一标示
     */
    public static void Trigger(Integer jobId){

        //获取job的详细信息  或者执行器信息
        JobInfoService jobInfoService=SpringHelper.popBean(JobInfoService.class);
        JobInfo jobInfo=jobInfoService.findOne(jobId);
        if(jobInfo==null){
            throw new CommonException(ResultEnum.JOB_NOT_EXIST);
        }

        //build cluster  配置机器的ha和选择服务的策略
        ClusterSupport clusterSupport=new ClusterSupport();
        Cluster cluster=clusterSupport.buildClusterSupport(jobInfo);
        cluster.call(jobId);

    }












}
