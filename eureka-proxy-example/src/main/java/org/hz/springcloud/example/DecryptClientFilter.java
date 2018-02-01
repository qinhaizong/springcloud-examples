package org.hz.springcloud.example;

import com.sun.jersey.api.client.*;
import com.sun.jersey.api.client.filter.ClientFilter;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.zip.GZIPInputStream;

public class DecryptClientFilter extends ClientFilter {

    private final IEncryption encryption;

    public DecryptClientFilter(IEncryption encryption) {
        this.encryption = encryption;
    }


    @Override
    public ClientResponse handle(ClientRequest request) {
        if (null != request.getEntity()) {
            request.setAdapter(new CipherAdapter(request.getAdapter(), encryption));
        }
        ClientResponse response = getNext().handle(request);
        if (response.hasEntity()) {
            InputStream inputStream = response.getEntityInputStream();
            try {
                /*if ("gzip".equalsIgnoreCase(response.getHeaders().getFirst(HttpHeaders.CONTENT_ENCODING))) {
                    response.getHeaders().remove(HttpHeaders.CONTENT_ENCODING);
                    response.setEntityInputStream(encryption.encrypt(new GZIPInputStream(inputStream)));
                } else {
                }*/
                response.setEntityInputStream(encryption.encrypt(inputStream));
            } catch (IOException ex) {
                if (inputStream != null) try {
                    inputStream.close();
                } catch (IOException ioe) {
                    // ignore
                }
                throw new ClientHandlerException(ex);
            }
        }
        return response;
    }

    private static final class CipherAdapter extends AbstractClientRequestAdapter {

        private final IEncryption encryption;

        protected CipherAdapter(ClientRequestAdapter cra, IEncryption encryption) {
            super(cra);
            this.encryption = encryption;
        }

        @Override
        public OutputStream adapt(ClientRequest request, OutputStream out) throws IOException {
            OutputStream outputStream = getAdapter().adapt(request, out);
            return encryption.decrypt(outputStream);
        }
    }
}