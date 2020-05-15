package com.meizu.lastmile.http;

import com.meizu.gslb2.GlobalConfiguration;
import com.meizu.gslb2.GslbManager;
import com.meizu.gslb2.IPHelper;
import com.meizu.gslb2.IpInfo;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Dns;

/**
 * @Author: huangyongwen
 * @CreateDate: 2020/5/15 11:03
 * @CreateDate:
 */

public class LastmileHTTP implements Dns {

    private GslbManager mGslbManager;

    public LastmileHTTP(GslbManager gslbManager) {
        mGslbManager = gslbManager;
    }

    @Override
    public List<InetAddress> lookup(String hostname) throws UnknownHostException {
        List<InetAddress> result = null;
        IpInfo ipInfo = getIpInfo(hostname);
        if (ipInfo != null) {
            result = new ArrayList<>();
            result.add(InetAddress.getByName(ipInfo.getIp()));
        }
        if (result == null) {
            try {
                result = Dns.SYSTEM.lookup(hostname);
            } catch (IllegalArgumentException e) {
                throw new UnknownHostException(e.getMessage());
            }
        }
        return result;
    }

    public IpInfo getIpInfo(String domain) {
        if (!IPHelper.isMatch(domain)) {
            return mGslbManager.convert(domain,
                    GlobalConfiguration.getInstance().getCustomerParams(domain));
        }
        return null;
    }


}
