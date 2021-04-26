package com.awesomeJdk.system;

import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import org.junit.Test;

/**
 * @author froggengo@qq.com
 * @date 2021/4/25 13:32.
 */
public class TestSystem {

    double mx = 1024d * 1024d * 1024d;

    @Test
    public void test() {
        final Runtime runtime = Runtime.getRuntime();
        System.out.println(runtime.freeMemory() / mx);
        System.out.println(runtime.totalMemory() / mx);
        final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        System.out.println(memoryMXBean.getHeapMemoryUsage().getUsed() / mx);
        System.out.println(memoryMXBean.getHeapMemoryUsage().getMax() / mx);
        System.out.println(memoryMXBean.getNonHeapMemoryUsage().getUsed()/mx);
        System.out.println(memoryMXBean.getNonHeapMemoryUsage().getMax()/mx);
        System.out.println(ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage());
        System.out.println("==================");
        final OperatingSystemMXBean operatingSystemMXBean = (com.sun.management.OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
        System.out.println(operatingSystemMXBean.getTotalPhysicalMemorySize() / mx);
        System.out.println(operatingSystemMXBean.getFreePhysicalMemorySize()/mx);
        System.out.println(operatingSystemMXBean.getSystemCpuLoad());
    }

}
