package com.James.HashFunction;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;


/**
 * Created by James on 16/6/2.
 */
public interface IHashFunction {
  public static final IHashFunction MURMUR_HASH = new MurmurHash();
  public static final IHashFunction MD5 = new MD5Hash();

  public long hash(String key, Charset charset) throws UnsupportedEncodingException;

  public long hash(byte[] key);
}
