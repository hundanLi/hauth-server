package com.hauth.cas;

import com.github.yitter.contract.IdGeneratorOptions;
import com.github.yitter.idgen.YitIdHelper;
import org.junit.jupiter.api.Test;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/22 10:05
 */
public class UserIdGeneratorTests {

    @Test
    void testIdGenerator() {
        // 创建 IdGeneratorOptions 对象，可在构造函数中输入 WorkerId：
        IdGeneratorOptions options = new IdGeneratorOptions();
        options.WorkerId = 63;
        options.WorkerIdBitLength = 6;
//        options.BaseTime
//        options.
// options.WorkerIdBitLength = 10; // 默认值6，限定 WorkerId 最大值为2^6-1，即默认最多支持64个节点。
// options.SeqBitLength = 6; // 默认值6，限制每毫秒生成的ID个数。若生成速度超过5万个/秒，建议加大 SeqBitLength 到 10。
// options.BaseTime = Your_Base_Time; // 如果要兼容老系统的雪花算法，此处应设置为老系统的BaseTime。
// ...... 其它参数参考 IdGeneratorOptions 定义。

// 保存参数（务必调用，否则参数设置不生效）：
        YitIdHelper.setIdGenerator(options);

// 以上过程只需全局一次，且应在生成ID之前完成。
        System.out.println(YitIdHelper.nextId());
        System.out.println(YitIdHelper.nextId());
        System.out.println(YitIdHelper.nextId());
        System.out.println(YitIdHelper.nextId());
        System.out.println(YitIdHelper.nextId());
    }


    @Test
    void test() {
//        int n = 1804289383;
        int n = 10;
        int candidate = (int) Math.sqrt((double) 2 * n);
        if ((candidate * (candidate + 1)) > 2 * n) {
            System.out.println(candidate - 1);
        } else {
            System.out.println(candidate);
        }
    }
}
