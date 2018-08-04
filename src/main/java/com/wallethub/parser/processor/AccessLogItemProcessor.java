package com.wallethub.parser.processor;

import com.wallethub.parser.model.AccessLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class AccessLogItemProcessor implements ItemProcessor<AccessLog, AccessLog> {

    private static final Logger log = LoggerFactory.getLogger(AccessLogItemProcessor.class);

    @Override
    //*I could have completely wipe out this process, or made date transformation to keep nanoseconds
    //I was using mysql 5.5 and the nanoseconds from the date field are not allowed on this version.
    public AccessLog process(AccessLog accessLog) throws Exception {
        //NO TRANSFORMATION FOR NOW...
        return accessLog;
    }

}
