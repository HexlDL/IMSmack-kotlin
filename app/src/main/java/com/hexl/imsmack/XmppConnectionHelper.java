package com.hexl.imsmack;

import android.util.Log;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.net.InetAddress;

public class XmppConnectionHelper {
    public static final String TAG = XmppConnectionHelper.class.getSimpleName();

    private AbstractXMPPConnection connection = null;

    private static XmppConnectionHelper xmppConnectionHelper = new XmppConnectionHelper();

    public static XmppConnectionHelper getXmppConnectionHelper() {
        return xmppConnectionHelper;
    }

    /**
     * 创建连接
     */
    public AbstractXMPPConnection getConnection() {
        if (connection == null) {
            // 开线程打开连接，避免在主线程里面执行HTTP请求
            // Caused by: android.os.NetworkOnMainThreadException
            new Thread(new Runnable() {
                @Override
                public void run() {
                    openConnection();
                }
            }).start();
        }
        return connection;
    }

    public boolean openConnection() {
        try {
            if (null == connection || !connection.isAuthenticated()) {
                SmackConfiguration.DEBUG = true;
                XMPPTCPConnectionConfiguration.Builder config = XMPPTCPConnectionConfiguration.builder();
                //设置openfire主机IP
                config.setHostAddress(InetAddress.getByName("192.168.1.101"));
                //设置openfire服务器名称
                config.setXmppDomain("openfire.im.hexl");
                //设置端口号：默认5222
                config.setPort(5222);
                //禁用SSL连接
                config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled).setCompressionEnabled(false);
                //设置Debug
                config.setDebuggerEnabled(true);
                //设置离线状态
                config.setSendPresence(false);
                //设置开启压缩，可以节省流量
                config.setCompressionEnabled(true);

                //需要经过同意才可以添加好友
                Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.manual);

                connection = new XMPPTCPConnection(config.build());
                connection.connect();// 连接到服务器
                Log.d(TAG, "openConnection: 连接成功");
                return true;
            }
        } catch (XMPPException | SmackException | IOException | InterruptedException xe) {
            xe.printStackTrace();
            connection = null;
            Log.d(TAG, "openConnection: 连接失败");
        }
        return false;
    }

}
