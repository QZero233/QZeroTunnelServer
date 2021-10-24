package com.qzero.tunnel.server.relay;

public interface DataPreprocessor {

    byte[] beforeSent(byte[] data);

    byte[] afterReceived(byte[] data);

}
