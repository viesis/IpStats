package monitoring.web

import spock.lang.Specification

class IpCountSpec extends Specification {

    def "initial count is 0"() {
        when:
        IpCount ipCount = new IpCount()

        then:
        ipCount.count.intValue() == 0
    }

    def "count is incremental"() {
        when:
        IpCount ipCount = new IpCount()
        1000.times({
            ipCount.increment()
        })

        then:
        ipCount.count.intValue() == 1000
    }

}
