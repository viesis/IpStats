package monitoring.web

import spock.lang.Specification

class WebServiceIpAddressCollectorSpec extends Specification {

    WebServiceIpAddressCollector ipAddressCollector

    ActivitySimulator activitySimulator

    def "cache does not exceed max size, least recently accessed ips are evicted"() {
        setup:
        def maxCacheSize = 3
        ipAddressCollector = new WebServiceIpAddressCollector(maxCacheSize)
        activitySimulator = new ActivitySimulator(ipAddressCollector)

        when:
        activitySimulator.activities "10.0.0.1", "10.0.0.2", "10.0.0.1", "127.0.0.1", "192.168.1.1"

        then:
        allCache().size() == maxCacheSize
        allCache() == ["10.0.0.1", "127.0.0.1", "192.168.1.1"]
    }

    def "all ip addresses are collected when not exceeding max size"() {
        setup:
        ipAddressCollector = new WebServiceIpAddressCollector(100)
        activitySimulator = new ActivitySimulator(ipAddressCollector)

        when:
        activitySimulator.activities "10.0.0.1", "10.0.0.2", "10.0.0.3", "127.0.0.1", "192.168.1.1"

        then:
        allCache().containsAll(["10.0.0.1", "10.0.0.2", "10.0.0.3", "127.0.0.1", "192.168.1.1"])
    }

    def "specified count of top ip addresses are returned"() {
        setup:
        ipAddressCollector = new WebServiceIpAddressCollector(10000)
        activitySimulator = new ActivitySimulator(ipAddressCollector)

        when:
        activitySimulator.uniqueIpActivities 1000

        then:
        ipAddressCollector.getTop(100).size() == 100
        ipAddressCollector.getTop(2000).size() == 1000
    }

    def "all ip addresses are returned when top addresses are fewer than specified"() {
        setup:
        ipAddressCollector = new WebServiceIpAddressCollector(10000)
        activitySimulator = new ActivitySimulator(ipAddressCollector)

        when:
        activitySimulator.uniqueIpActivities 1000

        then:
        ipAddressCollector.getTop(2500).size() == 1000
    }


    def "most used ip addresses are returned as top addresses"() {
        setup:
        ipAddressCollector = new WebServiceIpAddressCollector(100)
        activitySimulator = new ActivitySimulator(ipAddressCollector)

        when:
        activitySimulator.uniqueIpActivities 1000
        activitySimulator.activities "10.0.0.1", "10.0.0.1", "10.0.0.3", "127.0.0.1", "192.168.1.1", "10.0.0.3", "10.0.0.4"

        then:
        ipAddressCollector.getTop(2) == ["10.0.0.3", "10.0.0.1"]
    }

    def allCache() {
        return ipAddressCollector.getTop(Integer.MAX_VALUE)
    }
}
