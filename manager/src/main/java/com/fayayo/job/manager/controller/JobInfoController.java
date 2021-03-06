package com.fayayo.job.manager.controller;

import com.fayayo.job.common.constants.Constants;
import com.fayayo.job.common.enums.ResultEnum;
import com.fayayo.job.common.exception.CommonException;
import com.fayayo.job.common.result.ResultVO;
import com.fayayo.job.common.result.ResultVOUtil;
import com.fayayo.job.entity.JobInfo;
import com.fayayo.job.entity.params.JobInfoParams;
import com.fayayo.job.manager.core.helper.TriggerHelper;
import com.fayayo.job.manager.service.JobInfoService;
import com.fayayo.job.manager.vo.JobInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/job")
public class JobInfoController {

    @Autowired
    private JobInfoService jobInfoService;


    /**
     * @描述 手动执行一次任务
     * @参数 服务名称
     */
    @PostMapping("/trigger")
    public ResultVO trigger(@RequestParam("jobId") String jobId) {

        TriggerHelper.Trigger(jobId);

        return ResultVOUtil.success();
    }

    /**
     * @描述 任务详情
     */
    @PostMapping("/detail")
    public ResultVO detail(@RequestParam("jobId") String jobId) {
        JobInfoVo jobInfoVo = jobInfoService.findJobInfoVo(jobId);
        return ResultVOUtil.success(jobInfoVo);
    }


    /**
     * @描述 任务流新增任务
     */
    @PostMapping("/save")
    public ResultVO save(@Valid JobInfoParams jobInfoParams, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new CommonException(ResultEnum.PARAM_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }

        log.info("{}任务流新增任务,参数:{}", Constants.LOG_PREFIX, jobInfoParams);
        JobInfo jobInfo = jobInfoService.saveOrUpdate(jobInfoParams);
        return ResultVOUtil.success(jobInfo);
    }

    @PostMapping("/editor")
    public ResultVO editor(@Valid JobInfoParams jobInfoParams, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new CommonException(ResultEnum.PARAM_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }

        log.info("{}任务流新增任务,参数:{}", Constants.LOG_PREFIX, jobInfoParams);
        JobInfo jobInfo = jobInfoService.saveOrUpdate(jobInfoParams);
        return ResultVOUtil.success(jobInfo);
    }

    /**
     * @描述 删除任务
     */
    @PostMapping("/delete")
    public ResultVO delete(@RequestParam("jobId") String jobId) {

        jobInfoService.deleteJob(jobId);

        return ResultVOUtil.success();
    }

    /**
     * @描述 分页条件查询
     * @返回值 List
     */
    @PostMapping("/flowJobList")
    public ResultVO<Page<JobInfo>> flowJobList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                               @RequestParam(value = "size", defaultValue = "10") Integer size,
                                               @RequestParam(value = "flowId") String flowId) {

        log.info("查询任务流下的任务,pageNum={},pageSize={}", page, size);
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        Pageable pageable = PageRequest.of((page - 1), size, sort);
        Page<JobInfo> jobInfoPage = jobInfoService.queryByFlowId(pageable, flowId);
        log.info("查询任务流下的任务,结果={}", jobInfoPage);
        return ResultVOUtil.success(jobInfoPage);

    }


    /**
     * @描述 分页条件查询
     * @返回值 List
     */
    @PostMapping("/list")
    public ResultVO<Page<JobInfoVo>> list(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                        @RequestParam(value = "size", defaultValue = "10") Integer size,
                                        @RequestParam(value = "executorType", required = false) String executorType,
                                        @RequestParam(value = "status", required = false) Integer status) {

        log.info("查询执行器,pageNum={},pageSize={}", page, size);
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        Pageable pageable = PageRequest.of((page - 1), size, sort);
        Page<JobInfoVo> jobInfoPage = jobInfoService.query(pageable, executorType, status);
        log.info("查询执行器,结果={}", jobInfoPage);
        return ResultVOUtil.success(jobInfoPage);

    }




}
