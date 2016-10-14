package com.James.basic.HashFunction;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;


/**
 * Created by James on 16/6/2.
 */
public interface iHashFunction {
  long hash(String key, Charset charset) throws UnsupportedEncodingException;

  long hash(byte[] key);
}
