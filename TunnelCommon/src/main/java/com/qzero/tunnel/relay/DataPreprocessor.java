package com.qzero.tunnel.relay;

import com.qzero.tunnel.crypto.DataWithLength;

public interface DataPreprocessor {

    DataWithLength beforeSent(DataWithLength data);

    DataWithLength afterReceived(DataWithLength data);

}
