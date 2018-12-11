package org.gmagnotta.log.impl.elasticsearch;

import org.gmagnotta.log.LogEvent;
import org.gmagnotta.log.LogEventWriter;

import java.io.IOException;
import java.net.URL;


public class ElasticSearchLogEventWriter implements LogEventWriter {

    /*
     *  initial index name:     unknown-log-201812110940
     *  after initialization:   pepsico-log-201812110940
     */


    private String index;
    private String type;

    private ElasticSearchLogClient client;

    public ElasticSearchLogEventWriter(URL elasticUrl, String index, String type) {
        try {
            this.type = type;
            this.index = index;
            client = new ElasticSearchLogClient(elasticUrl.getHost(), elasticUrl.getPort(), elasticUrl.getProtocol());

            if (!client.isLogIndexExist(index)) {
                client.createLogIndex(index, type);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void write(LogEvent logEvent) {
        if (client != null && logEvent != null) {
            client.putLogEvent(index, type, logEvent);
        }
    }

    @Override
    public void stop() {
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
        try {
            if (client != null) {
                client.createLogIndex(index, type);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
