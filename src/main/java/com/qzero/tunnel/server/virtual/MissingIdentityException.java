package com.qzero.tunnel.server.virtual;

import com.qzero.tunnel.server.exception.ErrorCodeList;
import com.qzero.tunnel.server.exception.ResponsiveException;

public class MissingIdentityException extends ResponsiveException {

    public MissingIdentityException(String missingLocalIdentity) {
        super(ErrorCodeList.CODE_MISSING_RESOURCE,"Missing local identity called "+missingLocalIdentity);
    }


}
