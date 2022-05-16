package com.azure.core.amqp.implementation.handler;

import com.azure.core.amqp.implementation.ReactorDispatcher;
import org.apache.qpid.proton.engine.Connection;
import org.apache.qpid.proton.engine.Link;
import org.apache.qpid.proton.engine.Session;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class EndpointsReferences {
    final static String psPrefix = "eh4/ConsumerGroups/$Default/Partitions/";
    private final static Map<String, AmqpSessionInfo> sessionHandlerMap = new HashMap<>();
    private final static Map<String, AmqpReceiverInfo> receiverHandlerMap = new HashMap<>();
    private final static Map<String, AmqpSenderInfo> sendHandlerMap = new HashMap<>();
    private static AtomicBoolean isConnectionSet = new AtomicBoolean();
    private static ConnectionInfo connectionInfo;

    private final static AtomicBoolean closedOnce = new AtomicBoolean();


    public synchronized static void sessionOpened(String sessionName, SessionHandler sessionHandler, Session session) {
        if (hasAllSessions()) {
            return;
        }

        if (sessionName.equals("mgmt-session")) {
            sessionHandlerMap.put("mgmt-session", new AmqpSessionInfo("mgmt-session", sessionHandler, session));
        } else if (sessionName.equals(psPrefix + "0")) {
            sessionHandlerMap.put(psPrefix + "0", new AmqpSessionInfo(psPrefix + "0", sessionHandler, session));
        } else if (sessionName.equals(psPrefix + "1")) {
            sessionHandlerMap.put(psPrefix + "1", new AmqpSessionInfo(psPrefix + "1", sessionHandler, session));
        } else if (sessionName.equals(psPrefix + "2")) {
            sessionHandlerMap.put(psPrefix + "2", new AmqpSessionInfo(psPrefix + "2", sessionHandler, session));
        } else if (sessionName.equals(psPrefix + "3")) {
            sessionHandlerMap.put(psPrefix + "3", new AmqpSessionInfo(psPrefix + "3", sessionHandler, session));
        } else if (sessionName.equals("cbs-session")) {
            sessionHandlerMap.put("cbs-session", new AmqpSessionInfo("cbs-session", sessionHandler, session));
        }
    }

    private synchronized static boolean hasAllSessions() {
        return sessionHandlerMap.size() == 6;
    }

    public synchronized static void receiverOpened(String receiverPath, ReceiveLinkHandler receiveLinkHandler, Link link) {
        if (hasAllReceivers()) {
            return;
        }

        if (receiverPath.equals("$management")) {
            receiverHandlerMap.put("$management", new AmqpReceiverInfo("$management", receiveLinkHandler, link));
        } else if (receiverPath.equals("$cbs")) {
            receiverHandlerMap.put("$cbs", new AmqpReceiverInfo("$cbs", receiveLinkHandler, link));
        } else if (receiverPath.equals(psPrefix + "0")) {
            receiverHandlerMap.put(psPrefix + "0", new AmqpReceiverInfo(psPrefix + "0", receiveLinkHandler, link));
        } else if (receiverPath.equals(psPrefix + "1")) {
            receiverHandlerMap.put(psPrefix + "1", new AmqpReceiverInfo(psPrefix + "1", receiveLinkHandler, link));
        } else if (receiverPath.equals(psPrefix + "2")) {
            receiverHandlerMap.put(psPrefix + "2", new AmqpReceiverInfo(psPrefix + "2", receiveLinkHandler, link));
        } else if (receiverPath.equals(psPrefix + "3")) {
            receiverHandlerMap.put(psPrefix + "3", new AmqpReceiverInfo(psPrefix + "3", receiveLinkHandler, link));
        }
    }

    private synchronized static boolean hasAllReceivers() {
        return receiverHandlerMap.size() == 6;
    }

    public synchronized static void senderOpened(String senderPath, SendLinkHandler sendLinkHandler, Link link) {
        if (hasAllSenders()) {
            return;
        }

        if (senderPath.equals("$management")) {
            sendHandlerMap.put("$management", new AmqpSenderInfo("$management", sendLinkHandler, link));
        } else if (senderPath.equals("$cbs")) {
            sendHandlerMap.put("$cbs", new AmqpSenderInfo("$cbs", sendLinkHandler, link));
        }
    }

    private synchronized static boolean hasAllSenders() {
        return sendHandlerMap.size() == 2;
    }

    private synchronized static void closeSenders(ReactorDispatcher dispatcher) {
        try {
            dispatcher.invoke(() -> {
                for (Map.Entry<String, AmqpSenderInfo> entry : sendHandlerMap.entrySet()) {
                    entry.getValue().sendLinkHandler.onLinkRemoteCloseMock(entry.getValue().link);
                    entry.getValue().clear();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized static void onConnection(ConnectionHandler connectionHandler, Connection connection) {
        if (isConnectionSet.getAndSet(true)) {
            return;
        }
        connectionInfo = new ConnectionInfo(connectionHandler, connection);
    }

    public synchronized static void closeInOrder(ReactorDispatcher dispatcher) {
        if (!hasAllSessions()) {
            return;
        }

        if (!hasAllReceivers()) {
            return;
        }

        if (!hasAllSenders()) {
            return;
        }

        if (!isConnectionSet.get()) {
            return;
        }

        if (!closedOnce.getAndSet(true)) {

            sleepMS(0);
            // SendLinkHandler {"az.sdk.message":"onLinkRemoteClose","connectionId":"MF_0b1fd8_1645781411086","errorCondition":null,"errorDescription":null,"linkName":"mgmt:sender"}
            AmqpSenderInfo mgmtSender = sendHandlerMap.get("$management");
            Objects.requireNonNull(mgmtSender);
            try {
                dispatcher.invoke(() -> {
                    mgmtSender.sendLinkHandler.onLinkRemoteCloseMock(mgmtSender.link);
                    mgmtSender.clear();
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

            sleepMS(744 - 720);
            // ReceiveLinkHandler {"az.sdk.message":"onLinkRemoteClose","connectionId":"MF_0b1fd8_1645781411086","errorCondition":null,"errorDescription":null,"linkName":"mgmt:receiver"}
            AmqpReceiverInfo mgmtReceiver = receiverHandlerMap.get("$management");
            Objects.requireNonNull(mgmtReceiver);
            try {
                dispatcher.invoke(() -> {
                    mgmtReceiver.receiveLinkHandler.onLinkRemoteCloseMock(mgmtReceiver.link);
                    mgmtReceiver.clear();
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

            sleepMS(767 - 744);
            // SessionHandler {"az.sdk.message":"onSessionRemoteClose closing a local session.","connectionId":"MF_0b1fd8_1645781411086","errorCondition":null,"errorDescription":null,"sessionName":"mgmt-session"}
            AmqpSessionInfo mgmtSession = sessionHandlerMap.get("mgmt-session");
            Objects.requireNonNull(mgmtSession);
            try {
                dispatcher.invoke(() -> {
                    mgmtSession.sessionHandler.onSessionRemoteCloseMock(mgmtSession.session);
                    mgmtSession.clear();
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

            sleepMS(768 - 767);
            // ReceiveLinkHandler {"az.sdk.message":"onLinkRemoteClose","connectionId":"MF_0b1fd8_1645781411086","errorCondition":null,"errorDescription":null,"linkName":"0_0dd157_1645781444080"}
            AmqpReceiverInfo partition0Receiver = receiverHandlerMap.get(psPrefix + "0");
            Objects.requireNonNull(partition0Receiver);
            try {
                dispatcher.invoke(() -> {
                    partition0Receiver.receiveLinkHandler.onLinkRemoteCloseMock(partition0Receiver.link);
                    partition0Receiver.clear();
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

            sleepMS(783 - 768);
            // SessionHandler {"az.sdk.message":"onSessionRemoteClose","connectionId":"MF_0b1fd8_1645781411086","errorCondition":null,"errorDescription":null,"sessionName":"connectedcooler-ingest-q-/ConsumerGroups/cosmosdb-input/Partitions/0"}
            AmqpSessionInfo partition0Session = sessionHandlerMap.get(psPrefix + "0");
            Objects.requireNonNull(partition0Session);
            try {
                dispatcher.invoke(() -> {
                    partition0Session.sessionHandler.onSessionRemoteCloseMock(partition0Session.session);
                    partition0Session.clear();
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

            sleepMS(791 - 783);
            // SendLinkHandler {"az.sdk.message":"onLinkRemoteClose","connectionId":"MF_0b1fd8_1645781411086","errorCondition":null,"errorDescription":null,"linkName":"cbs:sender"}
            AmqpSenderInfo cbsSender = sendHandlerMap.get("$cbs");
            Objects.requireNonNull(cbsSender);
            try {
                dispatcher.invoke(() -> {
                    cbsSender.sendLinkHandler.onLinkRemoteCloseMock(cbsSender.link);
                    cbsSender.clear();
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

            sleepMS(793 - 791);
            // ReceiveLinkHandler {"az.sdk.message":"onLinkRemoteClose","connectionId":"MF_0b1fd8_1645781411086","errorCondition":null,"errorDescription":null,"linkName":"cbs:receiver"}
            AmqpReceiverInfo cbsReceiver = receiverHandlerMap.get("$cbs");
            Objects.requireNonNull(cbsReceiver);
            try {
                dispatcher.invoke(() -> {
                    cbsReceiver.receiveLinkHandler.onLinkRemoteCloseMock(cbsReceiver.link);
                    cbsReceiver.clear();
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

            sleepMS(812 - 793);
            // SessionHandler {"az.sdk.message":"onSessionRemoteClose","connectionId":"MF_0b1fd8_1645781411086","errorCondition":null,"errorDescription":null,"sessionName":"cbs-session"}
            AmqpSessionInfo cbsSession = sessionHandlerMap.get("cbs-session");
            Objects.requireNonNull(cbsSession);
            try {
                dispatcher.invoke(() -> {
                    cbsSession.sessionHandler.onSessionRemoteCloseMock(cbsSession.session);
                    cbsSession.clear();
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

            sleepMS(814 - 812);
            // ReceiveLinkHandler {"az.sdk.message":"onLinkRemoteClose","connectionId":"MF_0b1fd8_1645781411086","errorCondition":null,"errorDescription":null,"linkName":"1_25ff02_1645781444249"}
            AmqpReceiverInfo partition1Receiver = receiverHandlerMap.get(psPrefix + "1");
            Objects.requireNonNull(partition0Receiver);
            try {
                dispatcher.invoke(() -> {
                    partition1Receiver.receiveLinkHandler.onLinkRemoteCloseMock(partition1Receiver.link);
                    partition1Receiver.clear();
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

            sleepMS(849 - 814);
            // SessionHandler {"az.sdk.message":"onSessionRemoteClose","connectionId":"MF_0b1fd8_1645781411086","errorCondition":null,"errorDescription":null,"sessionName":"connectedcooler-ingest-q-/ConsumerGroups/cosmosdb-input/Partitions/1"}
            AmqpSessionInfo partition1Session = sessionHandlerMap.get(psPrefix + "1");
            Objects.requireNonNull(partition1Session);
            try {
                dispatcher.invoke(() -> {
                    partition1Session.sessionHandler.onSessionRemoteCloseMock(partition1Session.session);
                    partition1Session.clear();
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

            sleepMS(850 - 849);
            // ReceiveLinkHandler {"az.sdk.message":"onLinkRemoteClose","connectionId":"MF_0b1fd8_1645781411086","errorCondition":null,"errorDescription":null,"linkName":"2_539546_1645781444278"}
            AmqpReceiverInfo partition2Receiver = receiverHandlerMap.get(psPrefix + "2");
            Objects.requireNonNull(partition2Receiver);
            try {
                dispatcher.invoke(() -> {
                    partition2Receiver.receiveLinkHandler.onLinkRemoteCloseMock(partition2Receiver.link);
                    partition2Receiver.clear();
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

            sleepMS(870 - 850);
            // SessionHandler {"az.sdk.message":"onSessionRemoteClose","connectionId":"MF_0b1fd8_1645781411086","errorCondition":null,"errorDescription":null,"sessionName":"connectedcooler-ingest-q-/ConsumerGroups/cosmosdb-input/Partitions/2"}
            AmqpSessionInfo partition2Session = sessionHandlerMap.get(psPrefix + "2");
            Objects.requireNonNull(partition2Session);
            try {
                dispatcher.invoke(() -> {
                    partition2Session.sessionHandler.onSessionRemoteCloseMock(partition2Session.session);
                    partition2Session.clear();
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

            sleepMS(872 - 870);
            // ReceiveLinkHandler {"az.sdk.message":"onLinkRemoteClose","connectionId":"MF_0b1fd8_1645781411086","errorCondition":null,"errorDescription":null,"linkName":"3_39ee9e_1645781444302"}
            AmqpReceiverInfo partition3Receiver = receiverHandlerMap.get(psPrefix + "3");
            Objects.requireNonNull(partition3Receiver);
            try {
                dispatcher.invoke(() -> {
                    partition3Receiver.receiveLinkHandler.onLinkRemoteCloseMock(partition3Receiver.link);
                    partition3Receiver.clear();
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

            sleepMS(885 - 872);
            // SessionHandler {"az.sdk.message":"onSessionRemoteClose","connectionId":"MF_0b1fd8_1645781411086","errorCondition":null,"errorDescription":null,"sessionName":"connectedcooler-ingest-q-/ConsumerGroups/cosmosdb-input/Partitions/3"}
            AmqpSessionInfo partition3Session = sessionHandlerMap.get(psPrefix + "3");
            Objects.requireNonNull(partition3Session);
            try {
                dispatcher.invoke(() -> {
                    partition3Session.sessionHandler.onSessionRemoteCloseMock(partition3Session.session);
                    partition3Session.clear();
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

            sleepMS(887 - 885);
            // ConnectionHandler {"az.sdk.message":"onConnectionRemoteClose","connectionId":"MF_0b1fd8_1645781411086","errorCondition":null,"errorDescription":null,"hostName":"iothub-ns-connectedc-6612905-12e4401781.servicebus.windows.net"}
            try {
                dispatcher.invoke(() -> {
                    connectionInfo.connectionHandler.onConnectionRemoteCloseMock(connectionInfo.connection);
                    connectionInfo.connectionHandler.onConnectionLocalCloseMock(connectionInfo.connection);
                    connectionInfo.clear();
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void sleepMS(long ms) {
        try {
            TimeUnit.MILLISECONDS.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void print() {
        int i = 0;
        for (Map.Entry<String, AmqpSessionInfo> entry : sessionHandlerMap.entrySet()) {
            System.out.println("session_" + i + ":" + entry.getValue().sessionName);
            i++;
        }
        i = 0;
        for (Map.Entry<String, AmqpReceiverInfo> entry : receiverHandlerMap.entrySet()) {
            System.out.println("receiver_" + i + ":" + entry.getValue().receiverPath);
            i++;
        }
        for (Map.Entry<String, AmqpSenderInfo> entry : sendHandlerMap.entrySet()) {
            System.out.println("sender_" + i + ":" + entry.getValue().receiverPath);
            i++;
        }
        System.out.println("connection set:" + isConnectionSet.get());
    }

    private static class AmqpSessionInfo {
        public String sessionName;
        public SessionHandler sessionHandler;
        public Session session;

        AmqpSessionInfo(String sessionName, SessionHandler sessionHandler, Session session) {
            this.sessionName = sessionName;
            this.sessionHandler = sessionHandler;
            this.session = session;
        }

        void clear() {
            this.sessionHandler = null;
            this.session = null;
        }
    }

    private static class AmqpReceiverInfo {
        public String receiverPath;
        public ReceiveLinkHandler receiveLinkHandler;
        public Link link;

        AmqpReceiverInfo(String receiverPath, ReceiveLinkHandler receiveLinkHandler, Link link) {
            this.receiverPath = receiverPath;
            this.receiveLinkHandler = receiveLinkHandler;
            this.link = link;
        }

        void clear() {
            this.receiveLinkHandler = null;
            this.link = null;
        }
    }

    private static class AmqpSenderInfo {
        public String receiverPath;
        public SendLinkHandler sendLinkHandler;
        public Link link;

        AmqpSenderInfo(String receiverPath, SendLinkHandler sendLinkHandler, Link link) {
            this.receiverPath = receiverPath;
            this.sendLinkHandler = sendLinkHandler;
            this.link = link;
        }

        void clear() {
            this.sendLinkHandler = null;
            this.link = null;
        }
    }

    private static class ConnectionInfo {
        public ConnectionHandler connectionHandler;
        public Connection connection;

        ConnectionInfo(ConnectionHandler connectionHandler, Connection connection) {
            this.connectionHandler = connectionHandler;
            this.connection = connection;
        }

        void clear() {
            this.connectionHandler = null;
            this.connection = null;
        }
    }


    public synchronized static void closeEndpoints(ReactorDispatcher dispatcher) {
        if (!hasAllSessions()) {
            return;
        }

        if (!hasAllReceivers()) {
            return;
        }

        if (!hasAllSenders()) {
            return;
        }

        if (!isConnectionSet.get()) {
            return;
        }

        if (!closedOnce.getAndSet(true)) {
            closeSessions(dispatcher);
            closeReceivers(dispatcher);
            closeSenders(dispatcher);
            closeConnection(dispatcher);
        }
    }

    private synchronized static void closeSessions(ReactorDispatcher dispatcher) {
        try {
            dispatcher.invoke(() -> {
                for (Map.Entry<String, AmqpSessionInfo> entry : sessionHandlerMap.entrySet()) {
                    entry.getValue().sessionHandler.onSessionRemoteCloseMock(entry.getValue().session);
                    entry.getValue().clear();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized static void closeReceivers(ReactorDispatcher dispatcher) {
        try {
            dispatcher.invoke(() -> {
                for (Map.Entry<String, AmqpReceiverInfo> entry : receiverHandlerMap.entrySet()) {
                    entry.getValue().receiveLinkHandler.onLinkRemoteCloseMock(entry.getValue().link);
                    entry.getValue().clear();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized static void closeConnection(ReactorDispatcher dispatcher) {
        try {
            dispatcher.invoke(() -> {
                connectionInfo.connectionHandler.onConnectionRemoteCloseMock(connectionInfo.connection);
                connectionInfo.connectionHandler.onConnectionLocalCloseMock(connectionInfo.connection);
                connectionInfo.clear();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
