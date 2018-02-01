package org.hz.springcloud.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface IEncryption {

    /**
     * 客户端加密
     *
     * @param outputStream
     * @return
     * @throws IOException
     */
    OutputStream encrypt(OutputStream outputStream) throws IOException;

    /**
     * 代理端解密
     *
     * @param outputStream
     * @return
     * @throws IOException
     */
    OutputStream decrypt(OutputStream outputStream) throws IOException;

    /**
     * 代理端加密
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    InputStream encrypt(InputStream inputStream) throws IOException;

    /**
     * 客户端解密
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    InputStream decrypt(InputStream inputStream) throws IOException;

}
