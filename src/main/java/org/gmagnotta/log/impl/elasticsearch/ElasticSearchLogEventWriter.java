package org.gmagnotta.log.impl.elasticsearch;

import org.apache.logging.log4j.util.Strings;
import org.gmagnotta.log.LogEvent;
import org.gmagnotta.log.LogEventWriter;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Executors;


public class ElasticSearchLogEventWriter implements LogEventWriter {

    private String index;
    private String app;
    private ElasticSearchLogClient client;

    @Override
    public void write(LogEvent logEvent) {
        if (client != null && logEvent != null) {
            try {
                client.putLogEvent(index, app, logEvent);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
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

    @Override
    public void setLogName(String logName) {
        renameIndex(logName);
    }

    /**
     * Start elastic search writer
     *
     * @param elasticSearchUrl      url of elastic search
     * @param index                 index name where log events will be putted
     * @param app                   application name
     */
    public ElasticSearchLogEventWriter(URL elasticSearchUrl, String index, String app) {
        if (elasticSearchUrl == null) {
            throw new IllegalArgumentException("Elasticsearch url can't be null");
        }

        if (Strings.isBlank(index)) {
            throw new IllegalArgumentException("Index can't be null or empty");
        }

        if (Strings.isBlank(app)) {
            throw new IllegalArgumentException("App can't be null or empty");
        }

        try {
            this.app = app;
            this.index = index;

            client = new ElasticSearchLogClient(elasticSearchUrl);
            if (!client.isLogIndexExist(index)) {
                client.createLogIndex(index);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method allows to set index name, where to put log messages
     * In case if some messages already was written on old index
     * they will be moved (reindex) to new index and delete old
     * index.
     *
     * @param newIndex      new name of index
     * @return
     */
    public boolean renameIndex(String newIndex) {
        if (client == null || Strings.isBlank(newIndex)) {
            return false;
        }

        // ElasticSearch doesn't have renaming operation, so we need to create new index,
        // move all documents to new index and delete old index.
        try {
            if (!client.isLogIndexExist(newIndex)) {
                client.createLogIndex(newIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String oldIndex = this.index;
        this.index = newIndex;

        // Since moving documents can take much time, we run this task in separated
        // thread as async operation.
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // Send all buffered events to elasticsearch before switching to new index
                client.flushLogEvents();

                // After flush, documents have to be processed by elasticsearch node
                // so we wait for 3 seconds before reindex. I wasn't able to find any
                // better solution.
                Thread.sleep(3000);

                // if prev index exist - reindex documents to new index
                if (oldIndex != null) {
                    client.reIndexLog(oldIndex, newIndex);
                    client.deleteLogIndex(oldIndex);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return true;
    }
}
