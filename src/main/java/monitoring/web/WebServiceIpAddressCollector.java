package monitoring.web;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import monitoring.IpAddressCollector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Functions.toStringFunction;
import static com.google.common.collect.Lists.transform;

class WebServiceIpAddressCollector implements IpAddressCollector {

    private final Cache<String, IpCount> ipCache;

    public WebServiceIpAddressCollector(int maxCacheSize) {
        ipCache = CacheBuilder.newBuilder().maximumSize(maxCacheSize).build();
    }

    @Override
    public void registerUserActivity(String ipAddress) {
        currentIpCount(ipAddress).increment();
    }

    private IpCount currentIpCount(String ipAddress) {
        IpCount currentIpCount = ipCache.getIfPresent(ipAddress);
        if (currentIpCount != null) {
            return currentIpCount;
        }

        IpCount newIpCount = new IpCount(ipAddress);
        ipCache.put(ipAddress, newIpCount);

        return newIpCount;
    }

    @Override
    public List<String> getTop(int count) {
        if (count > ipCache.size()) {
            return topIps();
        }

        return topIps().subList(0, count);
    }

    private List<String> topIps() {
        List<IpCount> ipList = new ArrayList<IpCount>(ipCache.asMap().values());
        Collections.sort(ipList);

        return transform(ipList, toStringFunction());
    }
}
