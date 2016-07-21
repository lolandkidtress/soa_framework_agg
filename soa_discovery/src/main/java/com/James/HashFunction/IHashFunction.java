package com.James.HashFunction;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;


/**
 * Created by James on 16/6/2.
 */
public interface iHashFunction {
  public static final iHashFunction MURMUR_HASH = new MurmurHash();
  public static final iHashFunction MD5 = new MD5Hash();

  public long hash(String key, Charset charset) throws UnsupportedEncodingException;

  public long hash(byte[] key);
}
