package io.github.duguyixiaono1.serviceImp;

import io.github.duguyixiaono1.share.IEchoService;

/**
 * Created by jliu1 on 2017/10/29.
 */
public class EchoService implements IEchoService {
    public String echo(String sth) {
        return "recv " + sth;
    }
}
