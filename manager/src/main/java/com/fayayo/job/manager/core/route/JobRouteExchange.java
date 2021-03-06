package com.fayayo.job.manager.core.route;

import com.fayayo.job.common.constants.Constants;
import com.fayayo.job.common.params.JobInfoParam;
import com.fayayo.job.common.util.EnumUtil;
import com.fayayo.job.core.extension.ExtensionLoader;
import com.fayayo.job.core.transport.protocol.request.RequestPacket;
import com.fayayo.job.manager.core.cluster.Endpoint;
import com.fayayo.job.manager.core.cluster.LoadBalance;
import com.fayayo.job.manager.core.cluster.loadbalance.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author dalizu on 2018/8/12.
 * @version v1.0
 * @desc 路由选择器
 */
@Slf4j
public class JobRouteExchange {

    /**
     *@描述  存储策略  每个任务对应自己的策略
     */
    private static final Map<String,LoadBalance> loadBalanceCenter=new ConcurrentHashMap<String, LoadBalance>();

    private List<Endpoint>endpoints;

    public JobRouteExchange(List<String>addressList) {
        endpoints=addressList.stream().map(e->{
            //解析address:port:weight
            String[]servers=e.split(":");
            if(servers.length==3){
                //构造Endpoint
                return new Endpoint(servers[0],Integer.parseInt(servers[1]),Integer.parseInt(servers[2]));
            }else if(servers.length==2){
                //构造Endpoint
                return new Endpoint(servers[0],Integer.parseInt(servers[1]));
            }else {
                log.info("{}服务地址注册格式有误!!!", Constants.LOG_PREFIX);
                return null;
            }
        }).collect(Collectors.toList());

    }

    /**
     *@描述 获取loadBalance
     *@创建人  dalizu
     *@创建时间  2018/8/12
     */
    public LoadBalance getLoadBalance(JobInfoParam jobInfo){
        String jobId=jobInfo.getId();
        //判断当前job是否已经存在负载策略
        LoadBalance loadBalance=loadBalanceCenter.get(jobId);
        if(loadBalance!=null){
            loadBalance.onRefresh(endpoints);
            return loadBalance;
        }
        Integer loadBalanceCode=jobInfo.getJobLoadBalance();
        String loadBalanceDesc= EnumUtil.getByCode(loadBalanceCode,JobLoadBalanceEnums.class).getDesc();
        loadBalance= ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension(loadBalanceDesc);
        loadBalanceCenter.put(jobInfo.getId(),loadBalance);
        loadBalance.onRefresh(endpoints);
        return loadBalance;
    }

    /**
     *@描述 获取日志 loadBalance
     *@创建人  dalizu
     *@创建时间  2018/10/24
     */
    public LoadBalance getLogLoadBalance(){
        // 执行器日志只在固定执行的机器上面，此处固定策略即可
        LoadBalance loadBalance= ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension(
                JobLoadBalanceEnums.ROUNDROBIN.getDesc());
        loadBalance.onRefresh(endpoints);
        return loadBalance;
    }



    public static void main(String[] args) throws InterruptedException {
        new Thread(new Runnable() {
            @Override
            public void run() {

                while (true){
                    List<String>list=new ArrayList<>();
                    list.add("10.10.10.1");
                    list.add("10.10.10.2");
                    list.add("10.10.10.3");
                    JobRouteExchange jobRouteExchange=new JobRouteExchange(list);
                    JobInfoParam jobInfo=new JobInfoParam();
                    jobInfo.setJobLoadBalance(3);
                    jobInfo.setId("1");
                    LoadBalance loadBalance=jobRouteExchange.getLoadBalance(jobInfo);
                    System.out.println(loadBalance.select(new RequestPacket()).toString());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){

                    List<String>lists=new ArrayList<>();
                    lists.add("10.10.10.99");
                    lists.add("10.10.10.98");
                    lists.add("10.10.10.87");
                    JobRouteExchange jobRouteExchange1=new JobRouteExchange(lists);

                    JobInfoParam jobInfo1=new JobInfoParam();
                    jobInfo1.setJobLoadBalance(3);
                    jobInfo1.setId("1");
                    LoadBalance loadBalance1=jobRouteExchange1.getLoadBalance(jobInfo1);
                    System.out.println(loadBalance1.select(new RequestPacket()).toString());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

}
