package com.jsc.zk;

import org.I0Itec.zkclient.IZkDataListener;

import java.util.concurrent.CountDownLatch;

public class ZookeeperDistrbuteLock extends ZookeeperAbstractLock {

    private CountDownLatch countDownLatch;

    @Override
    boolean tryLock() {
        try {
            //创建节点，如果节点存在，证明有人创建节点，此时获取锁失败，返回false
            //如果节点不存在，创建节点成功，获取锁成功，加锁
            zkClient.createEphemeral(lockPath);
//            System.out.println("#########获取锁######");
            return true;
        } catch (Exception e) {
            // 如果失败 直接catch
            return false;
        }
    }

    @Override
    void waitLock() {

        IZkDataListener iZkDataListener = new IZkDataListener() {

            // 节点被删除
            public void handleDataDeleted(String arg0) throws Exception {
                if (countDownLatch != null) {
                    countDownLatch.countDown(); // 计数器为0的情况，await 后面的继续执行
                }

            }

            // 节点被修改
            public void handleDataChange(String arg0, Object arg1) throws Exception {

            }
        };

        // 监听事件通知
        zkClient.subscribeDataChanges(lockPath, iZkDataListener);
        // 控制程序的等待
        if (zkClient.exists(lockPath)) {  //如果 检查出 已经被创建了 就new 然后进行等待
            countDownLatch = new CountDownLatch(1);
            try {
                countDownLatch.wait(); //等待时候 就不往下走了   当为0 时候 后面的继续执行
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        //后面代码继续执行
        //为了不影响程序的执行 建议删除该事件监听 监听完了就删除掉
        zkClient.unsubscribeDataChanges(lockPath, iZkDataListener);

    }

}