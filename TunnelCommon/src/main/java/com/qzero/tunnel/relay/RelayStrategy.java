package com.qzero.tunnel.relay;

public class RelayStrategy {

    /**
     * If it's true
     * The relay thread will send it out directly without sending the length before the data package
     */
    private boolean directlySend =true;

    /**
     * If it's true
     * The relay thread will read data without regrading the first 4 bytes as the length
     */
    private boolean directlyRead=true;

    public RelayStrategy() {
    }

    public RelayStrategy(boolean directlySend, boolean directlyRead) {
        this.directlySend = directlySend;
        this.directlyRead = directlyRead;
    }

    public boolean isDirectlySend() {
        return directlySend;
    }

    public void setDirectlySend(boolean directlySend) {
        this.directlySend = directlySend;
    }

    public boolean isDirectlyRead() {
        return directlyRead;
    }

    public void setDirectlyRead(boolean directlyRead) {
        this.directlyRead = directlyRead;
    }

    @Override
    public String toString() {
        return "RelayStrategy{" +
                "directlySend=" + directlySend +
                ", directlyRead=" + directlyRead +
                '}';
    }
}
