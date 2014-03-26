package monitoring.web

import monitoring.IpAddressCollector

import java.util.concurrent.Callable
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

import static java.util.concurrent.TimeUnit.SECONDS

class ActivitySimulator {

    final static int THREAD_POOL_SIZE = 20

    final IpAddressCollector ipAddressCollector

    final ExecutorService executor

    final CountDownLatch parallelTaskLatch = new CountDownLatch(THREAD_POOL_SIZE)

    class RandomIpActivitiesTask implements Callable<Void> {

        int activityCount

        RandomIpActivitiesTask(int activityCount) {
            this.activityCount = activityCount
        }

        @Override
        public Void call() throws Exception {
            randomIpActivities(activityCount)
            parallelTaskLatch.countDown()
        }
    }


    public ActivitySimulator(IpAddressCollector ipAddressCollector) {
        this.ipAddressCollector = ipAddressCollector
        this.executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE)
    }

    void uniqueIpActivities(int count) {
        count.times({
            ipAddressCollector.registerUserActivity(it as String)
        })

    }

    void parallelRandomActivities(int count) {
        int taskCount = (int) count / THREAD_POOL_SIZE
        THREAD_POOL_SIZE.times({
            executor.submit(new RandomIpActivitiesTask(taskCount))
        })
        waitForActivitiesToExecute()
    }

    void waitForActivitiesToExecute() {
        parallelTaskLatch.await(20, SECONDS)
    }

    void randomIpActivities(int count) {
        Random random = new Random()
        for (i in 1..count) {
            ipAddressCollector.registerUserActivity(random.nextInt(count) as String)
        }
    }

    def activities(... ips) {
        ips.each {
            ipAddressCollector.registerUserActivity(it)
        }
    }

}
