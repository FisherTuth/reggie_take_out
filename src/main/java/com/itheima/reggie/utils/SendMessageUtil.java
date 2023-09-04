package com.itheima.reggie.utils;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.itheima.reggie.common.CustomException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SendMessageUtil {
    @Value("${sendMsg.signName}")
    private  String signName;
    @Value("${sendMsg.templateCode}")
    private  String templateCode;
    public  void sendMessages(String phoneNumbers,Integer param) {

        DefaultProfile profile = DefaultProfile.getProfile("cn-shanghai", System.getenv("ALIBABA_CLOUD_ACCESS_KEY_ID"), System.getenv("ALIBABA_CLOUD_ACCESS_KEY_SECRET"));
        IAcsClient client = new DefaultAcsClient(profile);
        SendSmsRequest request = new SendSmsRequest();
        request.setSysRegionId("cn-shanghai");
        request.setPhoneNumbers(phoneNumbers);
        request.setSignName(signName);
        request.setTemplateCode(templateCode);
        SmsCode smsCode = new SmsCode(param);
        String jsonString = JSON.toJSONString(smsCode);
        log.info("param：{}", param);
        log.info("code：{}", smsCode.getCode());
        log.info("Json字符串：{}", jsonString);
        request.setTemplateParam(jsonString);
        try {
            SendSmsResponse response = client.getAcsResponse(request);
            log.info("短信发送成功,{},{},{},{}",response.getCode(),response.getMessage(),response.getRequestId(),response.getBizId());
        } catch (ClientException e) {
            log.info("发送短信错误，{}",e);
            throw new CustomException("发送短信错误");
        }

    }
}
@AllArgsConstructor
@Data
class SmsCode{
   private Integer code;
}