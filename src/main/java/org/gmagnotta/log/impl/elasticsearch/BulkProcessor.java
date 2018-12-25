package org.gmagnotta.log.impl.elasticsearch;

import org.apache.http.StatusLine;
import org.apache.logging.log4j.util.Strings;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * BulkProcessor for elasticsearch
 * based on bulk processor from High level Java Client
 *
 * More info:
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/client/java-rest/master/java-rest-high-document-bulk.html#java-rest-high-document-bulk-processor">BulkProcessor</a>
 */
public class BulkProcessor implements Runnable {

    private RestClient client;
    private final StringBuilder buffer = new StringBuilder();
    private long actionsCount = 0;

    private int bulkActions = 1000;
    private long bulkSize = 5 * 1024L * 1024L;    // 5MB
    private long flushInterval = -1L;

    private final Object waitSync = new Object();
    private Thread bulkThread;

    /**
     * Constructor
     *
     * @param client        RestClient to call elastic api
     */
    public BulkProcessor(RestClient client) {
        this.client = client;
        bulkThread = new Thread(this, "LoggerBulkProcessor");
        bulkThread.start();
    }

    /**
     * Method add new document to buffer to send (document id will be
     * generated automatically by elasticsearch)
     *
     * @param index         index name, where to push document
     * @param type          type of the document
     * @param jsonDoc       document to push in json format
     * @throws IOException
     */
    public void add(String index, String type, String jsonDoc) throws IOException, InterruptedException {
        add(index, type, null, jsonDoc);
    }

    /**
     * Method add new document to buffer to send
     *
     * @param index         index name, where to push document
     * @param type          type of the document
     * @param id            document id
     * @param jsonDoc       document to push in json format
     * @throws IOException
     */
    public void add(String index, String type, String id, String jsonDoc) throws IOException, InterruptedException {
        if (bulkThread.isInterrupted()) {
            throw new BulkInterruptedException();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("{\"index\" : { \"_index\" : \"");
        sb.append(index);
        if (Strings.isNotBlank(id)) {
            sb.append("\", \"_id\" : \"");
            sb.append(id);
        }
        sb.append("\", \"_type\" : \"");
        sb.append(type);
        sb.append("\" }}\n");
        sb.append(jsonDoc);
        sb.append("\n");
        actionsCount++;

        synchronized (buffer) {
            buffer.append(sb);
        }

        if (bulkSize > -1 && buffer.length() > bulkSize) {
            flush();
        }
        if (bulkActions > -1 && actionsCount > bulkActions) {
            flush();
        }
    }

    /**
     * Method push all buffered documents to elasticsearch
     * using Bulk API
     * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/6.5/docs-bulk.html">Bulk API</a>
     *
     * @throws IOException
     */
    public void flush() throws IOException, InterruptedException {
        if (bulkThread.isInterrupted()) {
            throw new BulkInterruptedException();
        }

        synchronized (buffer) {
            if (buffer.length() > 0) {
                Request request = new Request("POST", "/_bulk");
                request.setJsonEntity(buffer.toString());
                Response response = client.performRequest(request);

                // Clear buffer for future use
                buffer.setLength(0);
                actionsCount = 0;

                StatusLine status = response.getStatusLine();
                if (status.getStatusCode() != HttpURLConnection.HTTP_OK) {
                    throw new IOElasticException(status.getStatusCode(), status.getReasonPhrase());
                }
            }
        }
    }

    /**
     * Method to set max bulk actions in buffer before send to elasticsearch
     * when buffered actions reach max value processor automatically send
     * actions to elasticsearch.
     * Value -1, means disable max bulk actions.
     *
     * @param bulkActions       max number of buffered actions
     */
    public void setBulkActions(int bulkActions) {
        this.bulkActions = bulkActions;
    }

    /**
     * Method to set max size of bulk actions in buffer before send to elasticsearch
     * when buffer size reach max value processor automatically send
     * actions to elasticsearch.
     * Value -1, means disable max bulk size.
     *
     * @param bulkSize          max total size of actions in buffer
     */
    public void setBulkSize(long bulkSize) {
        this.bulkSize = bulkSize;
    }

    /**
     * Method set max interval before automatically send buffered actions to
     * elasticsearch.
     * Value -1, means disable interval.
     *
     * @param flushInterval     max interval before send actions in seconds
     */
    public void setFlushInterval(long flushInterval) {
        synchronized (waitSync) {
            this.flushInterval = flushInterval;
            waitSync.notify();
        }
    }

    /**
     * Method close/interrupt bulk processor
     */
    public void close() {
        bulkThread.interrupt();
    }

    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()) {
            try {
                flush();
                synchronized (waitSync) {
                    if (flushInterval < 0) {
                        waitSync.wait();
                    }
                }
                Thread.sleep(flushInterval * 1000);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
