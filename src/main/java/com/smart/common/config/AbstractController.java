package com.smart.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

/**
 * 通用Controller


 */
@RestController
public class AbstractController {

    protected Logger logger = LoggerFactory.getLogger(getClass());

}