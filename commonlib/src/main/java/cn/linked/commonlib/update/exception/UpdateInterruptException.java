package cn.linked.commonlib.update.exception;

import cn.linked.commonlib.exception.ApplicationException;

public class UpdateInterruptException extends ApplicationException {

    public UpdateInterruptException(String message){
        super(message);
    }

}
