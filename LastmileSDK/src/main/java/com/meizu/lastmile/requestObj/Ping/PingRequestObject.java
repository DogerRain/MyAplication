package com.meizu.lastmile.requestObj.Ping;

import com.meizu.lastmile.requestObj.Instruction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: huangyongwen
 * @CreateDate: 2020/5/18 17:49
 * @CreateDate:
 */
@Data
@Builder
public class PingRequestObject extends Instruction{
    String ip;
    String hostName;
}
