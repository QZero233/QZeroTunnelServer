package com.qzero.tunnel.relay;

import com.qzero.tunnel.crypto.DataWithLength;

public interface DataPreprocessor {

    DataWithLength beforeSent(DataWithLength data);

    DataWithLength afterReceived(DataWithLength data);

    /**
     * Determine how many bytes should be read each time
     * @return
     */
    int lengthOfReceive();

}
