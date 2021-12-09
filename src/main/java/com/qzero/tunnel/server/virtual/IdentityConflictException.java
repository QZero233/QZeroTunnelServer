package com.qzero.tunnel.server.virtual;

import com.qzero.tunnel.server.exception.ErrorCodeList;
import com.qzero.tunnel.server.exception.ResponsiveException;

public class IdentityConflictException extends ResponsiveException {

    public IdentityConflictException(String conflictedIdentity){
        super(ErrorCodeList.CODE_BAD_REQUEST_PARAMETER,"There is already a mapping with identity "+conflictedIdentity);
    }

}
