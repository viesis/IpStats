package monitoring;

import java.util.List;

public interface IpAddressCollector {

    void registerUserActivity(String ipAddress);

    List<String> getTop(int count);

}
