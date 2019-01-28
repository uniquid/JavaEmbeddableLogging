package org.gmagnotta.log.impl.elasticsearch;

import java.io.IOException;

public class IOElasticException extends IOException {

    public IOElasticException(int statusCode, String statusMessage) {
        super("Server returned unexpected http status response " + statusCode + ": " + statusMessage);
    }
}
