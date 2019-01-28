package org.gmagnotta.log.impl.elasticsearch;

public class BulkInterruptedException extends InterruptedException {
    public BulkInterruptedException() {
        super("Bulk Processor was interrupted. Need recreate Bulk Processor to use.");
    }
}
