package monitoring.web

import spock.lang.Specification

import static groovy.time.TimeCategory.minus

class WebServiceIpAddressCollectorLoadTest extends Specification {

    WebServiceIpAddressCollector ipAddressCollector = new WebServiceIpAddressCollector(100000000)

    ActivitySimulator sequentActivitySimulator = new ActivitySimulator(ipAddressCollector)


    def "submit bunch of unique ips sequentially"() {
        setup:
        def startTime = new Date()

        when:
        sequentActivitySimulator.uniqueIpActivities(uniqueIpCount)

        then:
        ipAddressCollector.ipCache.size() == uniqueIpCount

        println "Unique ${uniqueIpCount} ip addresses added to cache in " + executionTime(startTime)

        where:
        uniqueIpCount << [1000, 10000, 100000, 1000000, 5000000]
    }

    def "submit bunch of random non unique ips sequentially"() {
        setup:
        def startTime = new Date()

        when:
        sequentActivitySimulator.randomIpActivities(randomIpCount)

        then:
        ipAddressCollector.ipCache.size() > 0

        println "Random ${randomIpCount} ip addresses added to cache in " + executionTime(startTime)

        where:
        randomIpCount << [1000, 10000, 100000, 1000000, 5000000]
    }


    def "submit bunch of unique ips in parallel"() {
        setup:
        def startTime = new Date()

        when:
        sequentActivitySimulator.parallelRandomActivities(randomIpCount)

        then:
        ipAddressCollector.ipCache.size() > 0

        println "Random ${randomIpCount} ip addresses in parallel added to cache in " + executionTime(startTime)

        where:
        randomIpCount << [1000, 10000, 100000, 1000000, 5000000]
    }

    def "get top 100 ips with different load"() {
        when:
        sequentActivitySimulator.randomIpActivities(randomIpCount)
        def startTime = new Date()

        then:
        ipAddressCollector.getTop(100).size() == 100

        println "Top 100 ip addresses from cache with ${randomIpCount} ip addresses retrieved in " + executionTime(startTime)

        where:
        randomIpCount << [1000, 10000, 100000, 1000000, 5000000]
    }

    def executionTime(def startTime) {
        minus(new Date(), startTime)
    }

}
