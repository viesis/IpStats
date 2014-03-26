package monitoring.web;

import java.util.concurrent.atomic.AtomicInteger;

class IpCount implements Comparable<IpCount> {

    private final String ip;

    private AtomicInteger count = new AtomicInteger(0);

    public IpCount(String ip) {
        this.ip = ip;
    }

    public void increment() {
        count.incrementAndGet();
    }

    @Override
    public String toString() {
        return ip;
    }

    @Override
    public int compareTo(IpCount that) {
        if (this.count.get() > that.count.get()) {
            return -1;
        } else if (this.count.get() < that.count.get()) {
            return 1;
        }

        return 0;
    }
}
