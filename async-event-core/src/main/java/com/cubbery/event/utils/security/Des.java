/**
 * Copyright (c) 2016, www.cubbery.com. All rights reserved.
 */
package com.cubbery.event.utils.security;

import javax.crypto.Cipher;

class Des {
    protected final String FACTORY_KEY = "PBEWithMD5AndDES";
    protected Cipher cipher;

    // 8-byte Salt
    static byte[] salt = {
            (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32,
            (byte) 0x56, (byte) 0x35, (byte) 0xE3, (byte) 0x03
    };

    // Iteration count
    static int iterationCount = 3;
}

