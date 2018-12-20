package org.gmagnotta.log.impl.elasticsearch;

public class ElasticSearchLogEventWriterHolder {

    private static ElasticSearchLogEventWriter INSTANCE;

    private ElasticSearchLogEventWriterHolder() {

    }

    public static synchronized ElasticSearchLogEventWriter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ElasticSearchLogEventWriter();
        }

        return INSTANCE;
    }

}
