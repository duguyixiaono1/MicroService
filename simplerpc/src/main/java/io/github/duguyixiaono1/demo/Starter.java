package io.github.duguyixiaono1.demo;

import io.github.duguyixiaono1.invoker.Invoker;
import io.github.duguyixiaono1.publisher.ServiceExporter;
import io.github.duguyixiaono1.serviceImp.EchoService;
import io.github.duguyixiaono1.share.IEchoService;

import java.net.InetSocketAddress;

/**
 * Created by jliu1 on 2017/10/29.
 */
public class Starter {
    private static final int port = 8889;

    public static void main(String[] args) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    ServiceExporter.export("localhost", port);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
        Invoker invoker = new Invoker<IEchoService>();
        IEchoService echoService = (IEchoService) invoker.invoke(EchoService.class, new InetSocketAddress("localhost", port));
        String ackMsg = echoService.echo("hello, rpc");
        System.out.println(ackMsg);
    }
}
