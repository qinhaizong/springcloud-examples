package org.hz.springcloud.example;

import com.sun.jersey.api.client.*;
import com.sun.jersey.api.client.filter.ClientFilter;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class EncryptClientFilter extends ClientFilter {

    private final IEncryption encryption;

    public EncryptClientFilter(IEncryption encryption) {
        this.encryption = encryption;
    }

    @Override
    public ClientResponse handle(ClientRequest cr) throws ClientHandlerException {
        if (null != cr.getEntity()) {
            cr.setAdapter(new CipherAdapter(cr.getAdapter(), encryption));
        }
        ClientResponse response = getNext().handle(cr);
        if (response.hasEntity()) {
            InputStream inputStream = response.getEntityInputStream();
            try {
                response.setEntityInputStream(encryption.decrypt(inputStream));
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

    public static class CipherAdapter extends AbstractClientRequestAdapter {

        private final IEncryption encryption;

        public CipherAdapter(ClientRequestAdapter adapter, IEncryption encryption) {
            super(adapter);
            this.encryption = encryption;
        }

        @Override
        public OutputStream adapt(ClientRequest request, OutputStream out) throws IOException {
            OutputStream outputStream = getAdapter().adapt(request, out);
            return encryption.encrypt(outputStream);
        }
    }
}
