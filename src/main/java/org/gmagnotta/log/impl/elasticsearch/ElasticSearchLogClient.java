package org.gmagnotta.log.impl.elasticsearch;

import org.apache.http.HttpHost;
import org.apache.http.StatusLine;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.gmagnotta.log.LogEvent;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Log client based on elastic search
 * Java Low Level REST Client 6.5
 *
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/client/java-rest/6.5/java-rest-low-usage-requests.html">ES Low Level Client</a>
 *
 * More info about api here:
 * https://www.elastic.co/blog/a-practical-introduction-to-elasticsearch
 * https://www.elastic.co/guide/en/elasticsearch/client/java-rest/master/java-rest-high-create-index.html
 * https://discuss.elastic.co/t/delete-logs-in-elasticsearch-after-certain-period/75067
 * https://www.lindstromhenrik.com/running-elasticsearch-curator-as-an-aws-lambda-function/ 
 */
public class ElasticSearchLogClient {

    private static final String TYPE = "_doc";

    private RestClient client;
    private BulkProcessor bulkProcessor;

    /**
     * Constructor
     *
     * @param url      url of elasticsearch master-node
     */
    public ElasticSearchLogClient(URL url) {
        HttpHost host = new HttpHost(url.getHost(), url.getPort(), url.getProtocol());
        client = RestClient.builder(host).build();
        bulkProcessor = new BulkProcessor(client);
        bulkProcessor.setFlushInterval(5);              // every 5 sec
        bulkProcessor.setBulkActions(50);               // max 50 actions
        bulkProcessor.setBulkSize(2 * 1024L * 1024L);   // max 2 MB
    }


    /**
     * Close all resources related to ElasticSearch
     *
     * @throws IOException
     */
    public void close() throws IOException {
        bulkProcessor.close();
        client.close();
    }


    /**
     * Method create index with given name
     *
     * @param index         index name to create
     * @throws IOException
     */
    public void createLogIndex(String index) throws IOException {
        String body = "{" +
                        "  \"mappings\": {" +
                        "    \"" + TYPE + "\": {" +
                        "      \"properties\": {" +
                        "        \"date\":      { \"type\": \"date\"  }," +
                        "        \"logLevel\":  { \"type\": \"keyword\"  }" +
                        "      }" +
                        "    }" +
                        "  }" +
                        "}";

        Request request = new Request("PUT", "/" + index);
        request.setJsonEntity(body);
        Response response = client.performRequest(request);

        StatusLine status = response.getStatusLine();
        if (status.getStatusCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("Server returned unexpected http status response " + status.getStatusCode() + ": " + status.getReasonPhrase());
        }
    }


    /**
     * Method delete index by name
     *
     * @param index         index name to delete
     * @throws IOException
     */
    public void deleteLogIndex(String index) throws IOException {
        Request request = new Request("DELETE", "/" + index);
        Response response = client.performRequest(request);

        StatusLine status = response.getStatusLine();
        if (status.getStatusCode() != HttpURLConnection.HTTP_OK) {
            throw new IOElasticException(status.getStatusCode(), status.getReasonPhrase());
        }
    }


    /**
     * Method implements elasticsearch reindex operation allows copy
     * all documents from source to destination index
     *
     * Since RestHighLevelClient is not support flag 'wait_for_completion'
     * which is need to avoid Connection timeout exception, was implemented
     * raw http call for reindex operation.
     *
     * @param sourceIndex   source index name
     * @param destIndex     destination index name
     * @throws IOException
     */
    public void reIndexLog(String sourceIndex, String destIndex) throws IOException {
        String body = "{\"source\": {\"index\": \"" + sourceIndex + "\"},\"dest\": {\"index\": \"" + destIndex + "\"}}";

        Request request = new Request("POST", "/_reindex?wait_for_completion=true");
        request.setJsonEntity(body);
        Response response = client.performRequest(request);

        StatusLine status = response.getStatusLine();
        if (status.getStatusCode() != HttpURLConnection.HTTP_OK) {
            throw new IOElasticException(status.getStatusCode(), status.getReasonPhrase());
        }
    }


    /**
     * Method send log event to elastic search
     *
     * @param index         index name where log event need to be putted
     * @param app           application name
     * @param logEvent      log event that need to be putted
     */
    public void putLogEvent(String index, String app, LogEvent logEvent) throws IOException, InterruptedException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PrintStream ps = new PrintStream(baos, true)) {
            Throwable throwable = logEvent.getThrowable();
            ps.println(logEvent.getMessage());
            if (throwable != null) {
                throwable.printStackTrace(ps);
            }
        }
        String message = new String(baos.toByteArray(), java.nio.charset.StandardCharsets.UTF_8);
        message = message.replace("\n", "\\n");

        String body = "{" +
                      "    \"date\" : \"" + logEvent.getDate().getTime() + "\"," +
                      "    \"app\" : \"" + app + "\"," +
                      "    \"logLevel\" : \"" + logEvent.getLogLevel() + "\"," +
                      "    \"sourceClass\" : \"" + logEvent.getSourceClass() + "\"," +
                      "    \"thread\" : \"" + logEvent.getThreadName() + "\"," +
                      "    \"message\" : \"" + message + "\"" +
                      "}";

        bulkProcessor.add(index, TYPE, body);
    }

    /**
     * Method to force send pending messages buffered in
     * bulk processor
     */
    public void flushLogEvents() throws IOException, InterruptedException {
        if (bulkProcessor != null) {
            bulkProcessor.flush();
        }
    }

    /**
     * Method check, if index exist
     *
     * @param indexName     index name for check
     * @return              true in case if index with given name exist
     * @throws IOException
     */
    public boolean isLogIndexExist(String indexName) throws IOException {
        Request request = new Request("HEAD", "/" + indexName);
        Response response = client.performRequest(request);

        StatusLine status = response.getStatusLine();
        switch (status.getStatusCode()) {
            case HttpURLConnection.HTTP_OK:
                return true;
            case HttpURLConnection.HTTP_NOT_FOUND:
                return false;
            default:
                throw new IOElasticException(status.getStatusCode(), status.getReasonPhrase());
        }
    }
}
